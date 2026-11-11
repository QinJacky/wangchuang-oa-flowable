// LogicFlow 图数据 <-> Flowable BPMN XML 互转
// 目标：导出后端 Flowable 引擎可直接解析部署的 BPMN 2.0 XML（含 flowable: 扩展属性）
// 节点业务字段统一存放在 LogicFlow 节点的 properties 中（见 OA流程引擎节点详细设计）。
// 约束：或签(multiMode=any) 不创建多实例；驳回(rejectTargets) 仅存储、运行时跳转，不生成 BPMN 静态路由。
// 注意：后端存量 XML 使用默认命名空间（<startEvent> 无 bpmn: 前缀），导入必须按 localName 匹配。

const FLOWABLE_NS = "http://flowable.org/bpmn";

// LF type -> BPMN 元素（导出用，标准小驼峰）
const NODE_BPMN = {
  startEvent: "bpmn:startEvent",
  endEvent: "bpmn:endEvent",
  userTask: "bpmn:userTask",
  serviceTask: "bpmn:serviceTask", // 抄送节点
  exclusiveGateway: "bpmn:exclusiveGateway",
  parallelGateway: "bpmn:parallelGateway",
  inclusiveGateway: "bpmn:inclusiveGateway",
};

// BPMN localName -> LF type（导入用，兼容有无前缀）
const BPMN_NODE = {
  startEvent: "startEvent",
  endEvent: "endEvent",
  userTask: "userTask",
  task: "userTask",
  serviceTask: "serviceTask",
  exclusiveGateway: "exclusiveGateway",
  parallelGateway: "parallelGateway",
  inclusiveGateway: "inclusiveGateway",
};

function rid() {
  return Math.random().toString(36).slice(-8);
}

// 转成 BPMN 合法的 NCName（id 不允许 -、不能以数字开头等）
function toNCName(raw, fallbackPrefix, used) {
  let s = String(raw == null ? "" : raw).replace(/[^a-zA-Z0-9_.]/g, "_");
  if (!s || /^[0-9]/.test(s)) s = (fallbackPrefix || "id") + "_" + (s || rid());
  let uniq = s;
  let i = 1;
  while (used.has(uniq)) uniq = `${s}_${i++}`;
  used.add(uniq);
  return uniq;
}

function esc(s) {
  if (s == null) return "";
  return String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&apos;");
}

function defaultTitle(type) {
  return {
    startEvent: "发起",
    endEvent: "结束",
    userTask: "审批",
    serviceTask: "抄送",
    exclusiveGateway: "条件分支",
    parallelGateway: "并行",
  }[type] || "节点";
}

// 按任意命名空间取子元素
function byLocal(root, name) {
  return root.getElementsByTagNameNS("*", name);
}

// 读取 flowable 命名空间属性（兼容 flowable:assignee / assignee 两种写法）
function fAttr(el, name) {
  let v = el.getAttributeNS(FLOWABLE_NS, name);
  if (v == null || v === "") v = el.getAttribute("flowable:" + name);
  return v || "";
}

// 读取 extensionElements 内 flowable:* 子元素的文本内容
function readFlowableChild(el, name) {
  const ext = byLocal(el, "extensionElements")[0];
  if (!ext) return "";
  const e = byLocal(ext, name)[0];
  return e ? (e.textContent || "").trim() : "";
}

const OP_MAP = { eq: "==", ne: "!=", gt: ">", lt: "<", ge: ">=", le: "<=", in: "in" };

// 业务条件数组 -> Flowable UEL 表达式：${ field op 'value' && ... }
export function buildConditionExpression(conditions) {
  if (!conditions || !conditions.length) return "";
  const parts = conditions.map((c) => {
    const v = c.value == null ? "" : String(c.value);
    const op = OP_MAP[c.operator] || "==";
    if (op === "in") {
      const arr = v
        .split(",")
        .map((x) => `'${(x || "").trim()}'`)
        .join(",");
      return `${c.field} in [${arr}]`;
    }
    if (["gt", "lt", "ge", "le"].indexOf(c.operator) !== -1) {
      const num = /^-?\d+(\.\d+)?$/.test(v) ? v : `'${v}'`;
      return `${c.field} ${op} ${num}`;
    }
    return `${c.field} ${op} '${v}'`;
  });
  return "${" + parts.join(" && ") + "}";
}

// userTask 处理人属性 -> flowable 标准属性
function assigneeAttrs(p) {
  let a = "";
  const type = p.assigneeType;
  const val = p.assigneeValue || "";
  if (type === "fixedUser") {
    const arr = val.split(",").filter(Boolean);
    if (arr.length === 1) a += ` flowable:assignee="${esc(arr[0])}"`;
    else if (arr.length > 1) a += ` flowable:candidateUsers="${esc(arr.join(","))}"`;
  } else if (type === "role" || type === "deptRole") {
    if (val) a += ` flowable:candidateGroups="${esc(val)}"`;
  }
  if (p.dueDate) a += ` flowable:dueDate="${esc(p.dueDate)}"`;
  return a;
}

// 多实例：会签(multiMode=all) 才创建；或签(any) 不创建（约束#1）
function multiInstanceXml(p) {
  if (!p || p.multiMode !== "all") return "";
  const seq = p.sequential ? ` isSequential="true"` : "";
  // 并行会签需全部完成；顺序会签无需完成条件（依次处理）
  const cc = p.sequential
    ? ""
    : `<bpmn:completionCondition xsi:type="bpmn:tFormalExpression">${esc("${nrOfCompletedInstances==nrOfInstances}")}</bpmn:completionCondition>`;
  // collection / elementVariable 由后端从候选处理人(candidateUsers/Groups)派生
  return `<bpmn:multiInstanceLoopCharacteristics${seq}>${cc}</bpmn:multiInstanceLoopCharacteristics>`;
}

// 业务字段 -> flowable 扩展元素（后端自定义解析）
function businessExt(p, type) {
  const parts = [];
  if (type === "userTask") {
    if (p.assigneeType) parts.push(`<flowable:assigneeType>${esc(p.assigneeType)}</flowable:assigneeType>`);
    if (p.assigneeValue) parts.push(`<flowable:assigneeValue>${esc(p.assigneeValue)}</flowable:assigneeValue>`);
    if (p.assigneeLabel) parts.push(`<flowable:assigneeLabel>${esc(p.assigneeLabel)}</flowable:assigneeLabel>`);
    if (p.multiMode) parts.push(`<flowable:multiMode>${esc(p.multiMode)}</flowable:multiMode>`);
    if (p.sequential != null) parts.push(`<flowable:sequential>${p.sequential ? "true" : "false"}</flowable:sequential>`);
    if (p.allowApprove != null) parts.push(`<flowable:allowApprove>${p.allowApprove ? "true" : "false"}</flowable:allowApprove>`);
    if (p.allowReject != null) parts.push(`<flowable:allowReject>${p.allowReject ? "true" : "false"}</flowable:allowReject>`);
    if (p.rejectTargets && p.rejectTargets.length)
      parts.push(`<flowable:rejectTargets>${esc(p.rejectTargets.join(","))}</flowable:rejectTargets>`);
    if (p.commentRequired != null) parts.push(`<flowable:commentRequired>${p.commentRequired ? "true" : "false"}</flowable:commentRequired>`);
    if (p.fieldPermissions && Object.keys(p.fieldPermissions).length)
      parts.push(`<flowable:fieldPermissions>${esc(JSON.stringify(p.fieldPermissions))}</flowable:fieldPermissions>`);
    if (p.dueDuration) parts.push(`<flowable:dueDuration>${esc(p.dueDuration)}</flowable:dueDuration>`);
    if (p.overdueAction) parts.push(`<flowable:overdueAction>${esc(p.overdueAction)}</flowable:overdueAction>`);
  } else if (type === "serviceTask") {
    if (p.copyType) parts.push(`<flowable:copyType>${esc(p.copyType)}</flowable:copyType>`);
    if (p.copyValue) parts.push(`<flowable:copyValue>${esc(p.copyValue)}</flowable:copyValue>`);
    if (p.copyLabel) parts.push(`<flowable:copyLabel>${esc(p.copyLabel)}</flowable:copyLabel>`);
    if (p.notifyChannels && p.notifyChannels.length)
      parts.push(`<flowable:notifyChannels>${esc(p.notifyChannels.join(","))}</flowable:notifyChannels>`);
  } else if (type === "startEvent") {
    if (p.startFormKey) parts.push(`<flowable:formKey>${esc(p.startFormKey)}</flowable:formKey>`);
  }
  return parts.join("");
}

// list: [{ event, type:'class'|'expression'|'delegateExpression', value }]
function listenersXml(list, kind) {
  if (!list || !list.length) return "";
  const tag = kind === "taskListener" ? "flowable:taskListener" : "flowable:executionListener";
  const items = list
    .map((l) => {
      const attr =
        l.type === "expression"
          ? `expression="${esc(l.value)}"`
          : l.type === "delegateExpression"
          ? `delegateExpression="${esc(l.value)}"`
          : `class="${esc(l.value)}"`;
      return `<${tag} event="${esc(l.event)}" ${attr}/>`;
    })
    .join("");
  return items;
}

// 单节点 -> BPMN 片段（用于导出与预览）
function buildNodeXml(n, idMap, defaultEdgeOf) {
  const p = n.properties || {};
  const bpmnType = NODE_BPMN[n.type] || "bpmn:userTask";
  const name = esc(p.name || defaultTitle(n.type));
  const isGw = (n.type || "").indexOf("Gateway") !== -1;
  const w = isGw ? 56 : 150;
  const h = isGw ? 56 : 54;
  let attrs = "";
  const childParts = []; // 直接子元素（如 multiInstance）
  const extParts = []; // extensionElements 内子元素

  if (n.type === "userTask") {
    attrs += assigneeAttrs(p);
    const mi = multiInstanceXml(p);
    if (mi) childParts.push(mi);
    const biz = businessExt(p, "userTask");
    if (biz) extParts.push(biz);
    if (p.executionListeners && p.executionListeners.length)
      extParts.push(listenersXml(p.executionListeners, "executionListener"));
    if (p.taskListeners && p.taskListeners.length) extParts.push(listenersXml(p.taskListeners, "taskListener"));
  } else if (n.type === "serviceTask") {
    attrs += ` flowable:delegateExpression="${esc("${bpmCopyTaskDelegate}")}"`;
    const biz = businessExt(p, "serviceTask");
    if (biz) extParts.push(biz);
  } else if (n.type === "startEvent") {
    const biz = businessExt(p, "startEvent");
    if (biz) extParts.push(biz);
  }

  const nid = idMap[n.id];
  const def = defaultEdgeOf[n.id] ? ` default="${defaultEdgeOf[n.id]}"` : "";
  // BPMN schema：extensionElements 必须位于内容模型最前（紧跟 documentation，早于 loopCharacteristics / rendering 等）
  let inner = "";
  if (extParts.length) inner += `<bpmn:extensionElements>${extParts.join("")}</bpmn:extensionElements>`;
  inner += childParts.join("");
  let xml;
  if (inner) {
    xml = `    <${bpmnType} id="${nid}" name="${name}"${attrs}${def}>${inner}</${bpmnType}>\n`;
  } else {
    xml = `    <${bpmnType} id="${nid}" name="${name}"${attrs}${def} />\n`;
  }
  return { xml, w, h };
}

// 单连线 -> BPMN 片段（用于导出与预览）
function buildEdgeXml(e, idMap) {
  const p = e.properties || {};
  const src = idMap[e.sourceNodeId];
  const tgt = idMap[e.targetNodeId];
  const eid = idMap[e.id];
  const expr = buildConditionExpression(p.conditions) || p.conditionExpression || "";
  const extParts = [];
  if (p.conditions && p.conditions.length)
    extParts.push(`<flowable:conditions>${esc(JSON.stringify(p.conditions))}</flowable:conditions>`);
  if (p.isDefault) extParts.push(`<flowable:isDefault>true</flowable:isDefault>`);
  let children = "";
  // BPMN schema：extensionElements 必须位于内容模型最前（早于 conditionExpression / loopCharacteristics / rendering 等）
  if (extParts.length) children += `<bpmn:extensionElements>${extParts.join("")}</bpmn:extensionElements>`;
  if (expr) children += `<bpmn:conditionExpression xsi:type="bpmn:tFormalExpression">${esc(expr)}</bpmn:conditionExpression>`;
  const tagOpen = `    <bpmn:sequenceFlow id="${eid}" sourceRef="${src}" targetRef="${tgt}"`;
  const xml = children ? `${tagOpen}>${children}</bpmn:sequenceFlow>\n` : `${tagOpen} />\n`;
  return { xml };
}

// graph(LF) -> Flowable BPMN XML
export function graphToBpmn(graph, processInfo = { id: "", name: "" }) {
  const nodes = (graph && graph.nodes) || [];
  const edges = (graph && graph.edges) || [];
  const pid = processInfo.id || "flow_" + rid();
  const pname = processInfo.name || pid;

  // 所有 id 必须是合法 NCName（BPMN 约束：不允许 -、不能以数字开头等）。
  const used = new Set();
  const idMap = {};
  nodes.forEach((n) => (idMap[n.id] = toNCName(n.id, "Activity", used)));
  edges.forEach((e) => (idMap[e.id] = toNCName(e.id, "Flow", used)));

  // 记录默认连线（default 属性挂在源节点上）
  const defaultEdgeOf = {};
  edges.forEach((e) => {
    const p = e.properties || {};
    if (p.isDefault && e.sourceNodeId) defaultEdgeOf[e.sourceNodeId] = idMap[e.id];
  });

  let shapes = "";
  let di = "";
  nodes.forEach((n) => {
    const { xml, w, h } = buildNodeXml(n, idMap, defaultEdgeOf);
    shapes += xml;
    const x = Math.round(n.x - w / 2);
    const y = Math.round(n.y - h / 2);
    const nid = idMap[n.id];
    di += `      <bpmndi:BPMNShape id="BPMNShape_${nid}" bpmnElement="${nid}">\n        <omgdc:Bounds x="${x}" y="${y}" width="${w}" height="${h}" />\n      </bpmndi:BPMNShape>\n`;
  });

  let flows = "";
  edges.forEach((e) => {
    const { xml } = buildEdgeXml(e, idMap);
    flows += xml;
    const eid = idMap[e.id];
    const wps = (e.pointsList && e.pointsList.length ? e.pointsList : [])
      .map((pt) => `        <omgdi:waypoint x="${Math.round(pt.x)}" y="${Math.round(pt.y)}" />`)
      .join("\n");
    di += `      <bpmndi:BPMNEdge id="BPMNEdge_${eid}" bpmnElement="${eid}">\n${wps}\n      </bpmndi:BPMNEdge>\n`;
  });

  // 流程级业务配置：绑定表单 / 正文 / 附件 / 流程类型，写入 process 的 extensionElements（随 XML 往返，后端只需存 XML 即可，无需改表结构）
  const pi = processInfo || {};
  const pExt = [];
  if (pi.processType) pExt.push(`<flowable:processType>${esc(pi.processType)}</flowable:processType>`);
  if (pi.processTypeName) pExt.push(`<flowable:processTypeName>${esc(pi.processTypeName)}</flowable:processTypeName>`);
  const fi = pi.formInfo || {};
  if (fi.formType) pExt.push(`<flowable:formType>${esc(fi.formType)}</flowable:formType>`);
  if (fi.formCode) pExt.push(`<flowable:formCode>${esc(fi.formCode)}</flowable:formCode>`);
  if (fi.formId) pExt.push(`<flowable:formId>${esc(fi.formId)}</flowable:formId>`);
  if (fi.dynamicForm) pExt.push(`<flowable:dynamicForm>${esc(JSON.stringify(fi.dynamicForm))}</flowable:dynamicForm>`);
  if (pi.mainText) pExt.push(`<flowable:mainText>${esc(JSON.stringify(pi.mainText))}</flowable:mainText>`);
  if (pi.attachment) pExt.push(`<flowable:attachment>${esc(JSON.stringify(pi.attachment))}</flowable:attachment>`);
  const procInner = pExt.length ? `    <bpmn:extensionElements>${pExt.join("")}</bpmn:extensionElements>\n` : "";

  return `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
             xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC"
             xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI"
             xmlns:flowable="http://flowable.org/bpmn"
             targetNamespace="http://www.flowable.org/processdef">
  <bpmn:process id="${esc(pid)}" name="${esc(pname)}" isExecutable="true">
${procInner}${shapes}${flows}  </bpmn:process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="${esc(pid)}">
${di}    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>`;
}

// nodeData: 后端 flowXmlAndNode 返回的高亮数据 [{key, completed}]，用于标记节点审批进度
export function bpmnToGraph(xml, nodeData) {
  const parser = new DOMParser();
  const doc = parser.parseFromString(xml, "application/xml");
  const processEl = byLocal(doc, "process")[0] || doc.documentElement;
  const pid = processEl.getAttribute("id") || "flow_" + rid();
  const pname = processEl.getAttribute("name") || pid;

  // DI 坐标（localName 匹配，兼容有无前缀）
  const shapeMap = {};
  const shapes = byLocal(doc, "BPMNShape");
  for (let i = 0; i < shapes.length; i++) {
    const s = shapes[i];
    const id = s.getAttribute("bpmnElement");
    const b = byLocal(s, "Bounds")[0];
    if (id && b) {
      shapeMap[id] = {
        x: parseFloat(b.getAttribute("x")) + parseFloat(b.getAttribute("width")) / 2,
        y: parseFloat(b.getAttribute("y")) + parseFloat(b.getAttribute("height")) / 2,
        width: parseFloat(b.getAttribute("width")),
        height: parseFloat(b.getAttribute("height")),
      };
    }
  }

  // 节点（遍历 process 直接子元素，按 localName 匹配）
  const nodes = [];
  const children = processEl.children || [];
  for (let i = 0; i < children.length; i++) {
    const el = children[i];
    if (el.nodeType !== 1) continue;
    const lfType = BPMN_NODE[el.localName];
    if (!lfType) continue;
    const id = el.getAttribute("id");
    const bounds = shapeMap[id] || { x: 200 + nodes.length * 30, y: 200, width: 120, height: 64 };
    const props = { name: el.getAttribute("name") || "" };

    if (lfType === "userTask") {
      props.userType = readFlowableChild(el, "assigneeType") || "fixedUser";
      props.assigneeType = props.userType;
      props.assigneeValue = readFlowableChild(el, "assigneeValue") || fAttr(el, "assignee") || fAttr(el, "candidateUsers") || "";
      props.assigneeLabel = readFlowableChild(el, "assigneeLabel") || "";
      props.multiMode = readFlowableChild(el, "multiMode") || (byLocal(el, "multiInstanceLoopCharacteristics")[0] ? "all" : "all");
      props.sequential = readFlowableChild(el, "sequential") === "true";
      props.allowApprove = readFlowableChild(el, "allowApprove") !== "false";
      props.allowReject = readFlowableChild(el, "allowReject") !== "false";
      const rt = readFlowableChild(el, "rejectTargets");
      props.rejectTargets = rt ? rt.split(",") : ["initiator"];
      props.commentRequired = readFlowableChild(el, "commentRequired") === "true";
      const fp = readFlowableChild(el, "fieldPermissions");
      try {
        props.fieldPermissions = fp ? JSON.parse(fp) : {};
      } catch (e) {
        props.fieldPermissions = {};
      }
      props.dueDuration = readFlowableChild(el, "dueDuration") || "";
      props.overdueAction = readFlowableChild(el, "overdueAction") || "";
      const mi = byLocal(el, "multiInstanceLoopCharacteristics")[0];
      if (mi) {
        props.multiInstance = {
          enabled: true,
          isSequential: mi.getAttribute("isSequential") === "true",
          collection: fAttr(mi, "collection") || "",
          elementVariable: fAttr(mi, "elementVariable") || "",
          completionCondition: "",
        };
      }
      const ext = byLocal(el, "extensionElements")[0];
      if (ext) {
        const exl = byLocal(ext, "executionListener");
        if (exl && exl.length) props.executionListeners = Array.from(exl).map(mapListener);
        const tl = byLocal(ext, "taskListener");
        if (tl && tl.length) props.taskListeners = Array.from(tl).map(mapListener);
      }
    } else if (lfType === "serviceTask") {
      props.copyType = readFlowableChild(el, "copyType") || "fixedUser";
      props.copyValue = readFlowableChild(el, "copyValue") || "";
      props.copyLabel = readFlowableChild(el, "copyLabel") || "";
      const nc = readFlowableChild(el, "notifyChannels");
      props.notifyChannels = nc ? nc.split(",") : ["site"];
    } else if (lfType === "startEvent") {
      props.startFormKey = readFlowableChild(el, "formKey") || "";
    }

    // 审批进度高亮
    const flowEntry = nodeData && nodeData.find((m) => m.key === id);
    if (flowEntry) props.status = flowEntry.completed ? "completed" : "todo";
    nodes.push({ id, type: lfType, x: bounds.x, y: bounds.y, properties: props });
  }

  // 节点审批状态映射（用于连线配色）
  const statusById = {};
  nodes.forEach((n) => {
    statusById[n.id] = n.properties.status || "pending";
  });

  // 连线
  const edges = [];
  const flows = byLocal(processEl, "sequenceFlow");
  for (let i = 0; i < flows.length; i++) {
    const el = flows[i];
    const id = el.getAttribute("id");
    const src = el.getAttribute("sourceRef");
    const tgt = el.getAttribute("targetRef");
    const props = {};
    const cond = byLocal(el, "conditionExpression")[0];
    if (cond) props.conditionExpression = cond.textContent || "";
    const fc = readFlowableChild(el, "conditions");
    try {
      props.conditions = fc ? JSON.parse(fc) : [];
    } catch (e) {
      props.conditions = [];
    }
    props.isDefault = readFlowableChild(el, "isDefault") === "true";
    let style;
    const srcStatus = statusById[src] || "pending";
    const tgtStatus = statusById[tgt] || "pending";
    if (srcStatus === "completed" && tgtStatus !== "pending") {
      style = { stroke: "#10B981", strokeWidth: 2, hoverStroke: "#10B981", outlineColor: "#10B981" };
    } else if (srcStatus === "todo") {
      style = { stroke: "#2563EB", strokeWidth: 2, hoverStroke: "#2563EB", outlineColor: "#2563EB" };
    }
    edges.push({ id, type: "polyline", sourceNodeId: src, targetNodeId: tgt, properties: props, style });
  }

  // 默认连线标记（源节点 default 属性）
  const allEls = doc.getElementsByTagNameNS("*", "*");
  for (let i = 0; i < allEls.length; i++) {
    const d = allEls[i].getAttribute("default");
    if (d) {
      const e = edges.find((x) => x.id === d);
      if (e) e.properties.isDefault = true;
    }
  }

  // 解析流程级业务配置（来自 process 的 extensionElements）
  const DEFAULT_MAIN = { mainTextFlag: "0", type: "0", limitSize: 200, limitType: "", fileId: "", fileName: "", extendName: "", tipContent: "" };
  const DEFAULT_ATTACH = { attachFlag: "0", name: "", limitSize: 500, limitType: "", tipContent: "" };
  const pi = {
    id: pid,
    name: pname,
    processType: "",
    processTypeName: "",
    formInfo: { formType: "", formCode: "", formId: "", dynamicForm: {} },
    mainText: { ...DEFAULT_MAIN },
    attachment: { ...DEFAULT_ATTACH },
  };
  const procExtEl = byLocal(processEl, "extensionElements")[0];
  if (procExtEl) {
    const gc = (n) => {
      const e = byLocal(procExtEl, n)[0];
      return e ? (e.textContent || "").trim() : "";
    };
    pi.processType = gc("processType");
    pi.processTypeName = gc("processTypeName");
    pi.formInfo.formType = gc("formType");
    pi.formInfo.formCode = gc("formCode");
    pi.formInfo.formId = gc("formId");
    try {
      const d = gc("dynamicForm");
      if (d) pi.formInfo.dynamicForm = JSON.parse(d);
    } catch (e) {}
    try {
      const m = gc("mainText");
      if (m) pi.mainText = JSON.parse(m);
    } catch (e) {}
    try {
      const a = gc("attachment");
      if (a) pi.attachment = JSON.parse(a);
    } catch (e) {}
  }

  return { graph: { nodes, edges }, processInfo: pi };
}

function mapListener(node) {
  const cls = node.getAttribute("class");
  const expr = node.getAttribute("expression");
  const del = node.getAttribute("delegateExpression");
  const type = cls ? "class" : expr ? "expression" : "delegateExpression";
  const value = cls || expr || del || "";
  return { event: node.getAttribute("event") || "", type, value };
}

// 预览单个元素导出后的 BPMN 片段（映射预览面板用）
export function previewElementBpmn(element) {
  if (!element) return "";
  const idMap = { [element.id]: element.id };
  const defaultEdgeOf = {};
  if (element.type === "polyline" || element.type === "sequenceFlow") {
    return buildEdgeXml({ ...element, type: "polyline" }, idMap).xml.trim();
  }
  return buildNodeXml(element, idMap, defaultEdgeOf).xml.trim();
}
