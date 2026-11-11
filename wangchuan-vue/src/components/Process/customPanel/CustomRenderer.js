import BaseRenderer from "diagram-js/lib/draw/BaseRenderer";
import {
  create as svgCreate,
  attr as svgAttr,
  append as svgAppend,
  classes as svgClasses,
} from "tiny-svg";
import { is } from "bpmn-js/lib/util/ModelUtil";

const HIGH_PRIORITY = 2000;

// LogicFlow 风格配色（与原型 process-designer.html 对齐）
const COLORS = {
  start: "#10B981", // 开始 绿
  end: "#6B7280", // 结束 灰
  approve: "#2563EB", // 审批 蓝
  cc: "#F59E0B", // 抄送 橙
  cond: "#8B5CF6", // 条件 紫
  parallel: "#0EA5E9", // 并行 青
  default: "#2563EB",
};

const ICONS = {
  start: "▶",
  end: "■",
  approve: "✓",
  cc: "✉",
  cond: "◆",
  parallel: "⇉",
  default: "◆",
};

const FONT = '"PingFang SC","Microsoft YaHei",sans-serif';

function nodeKind(element) {
  const t = element.type;
  if (t === "bpmn:StartEvent") return "start";
  if (t === "bpmn:EndEvent") return "end";
  if (is(element, "bpmn:UserTask") || is(element, "bpmn:Task") || is(element, "bpmn:ServiceTask") || is(element, "bpmn:SendTask") || is(element, "bpmn:ReceiveTask") || is(element, "bpmn:ManualTask") || is(element, "bpmn:ScriptTask") || is(element, "bpmn:BusinessRuleTask"))
    return "approve";
  if (t === "bpmn:ParallelGateway") return "parallel";
  if (t.endsWith("Gateway")) return "cond";
  if (t.indexOf("Event") !== -1) return "start";
  return "default";
}

function defaultTitle(kind) {
  return { start: "开始", end: "结束", approve: "审批", cc: "抄送", cond: "条件", parallel: "并行", default: "节点" }[kind] || "节点";
}

function truncate(str, n) {
  if (!str) return "";
  const arr = Array.from(String(str));
  return arr.length > n ? arr.slice(0, n).join("") + "…" : arr.join("");
}

// 审批人字段常为加密哈希串，不展示原串
function isCipher(s) {
  return typeof s === "string" && s.length >= 16 && /^[0-9A-Fa-f]+$/.test(s.trim());
}

function describe(element) {
  const bo = element.businessObject || {};
  const kind = nodeKind(element);
  if (kind === "start") return "流程发起";
  if (kind === "end") return "流程结束";
  if (kind === "approve") {
    const who =
      bo.assignee ||
      (bo.candidateUsers ? "候选:" + bo.candidateUsers : "") ||
      (bo.candidateGroups ? "角色:" + bo.candidateGroups : "") ||
      "";
    if (!who) return "未配置审批人";
    if (isCipher(who)) return "已指定审批人";
    return "审批人：" + who;
  }
  if (kind === "cond") {
    const ce = bo.conditionExpression && bo.conditionExpression.body;
    return ce ? "条件：" + ce : "条件分支";
  }
  if (kind === "parallel") return "并行会签";
  if (kind === "cc") return "抄送节点";
  return "";
}

// 绘制连线箭头
function drawArrow(parent, x, y, angle, color) {
  const sz = 8;
  const a1 = angle - Math.PI / 7;
  const a2 = angle + Math.PI / 7;
  const p1x = x - sz * Math.cos(a1);
  const p1y = y - sz * Math.sin(a1);
  const p2x = x - sz * Math.cos(a2);
  const p2y = y - sz * Math.sin(a2);
  const poly = svgCreate("polygon");
  svgAttr(poly, {
    points: `${x},${y} ${p1x.toFixed(1)},${p1y.toFixed(1)} ${p2x.toFixed(1)},${p2y.toFixed(1)}`,
    fill: color,
  });
  svgClasses(poly).add("lf-conn-arrow");
  svgAppend(parent, poly);
}

export default class CustomRenderer extends BaseRenderer {
  constructor(eventBus, bpmnRenderer, connect) {
    super(eventBus, HIGH_PRIORITY);
    this.bpmnRenderer = bpmnRenderer;
    this._connect = connect;
  }

  canRender() {
    return true;
  }

  drawShape(parentNode, element) {
    const t = element.type;

    // 吞掉 bpmn 默认外部 label（卡片内已绘制标题/描述，避免重复）
    if (t === "label") {
      return svgCreate("g");
    }
    // 复杂元素（泳道/子流程/数据对象等）交给默认渲染器
    const custom =
      /(Task|Gateway|Event)$/.test(t) || t === "bpmn:StartEvent" || t === "bpmn:EndEvent";
    if (!element.businessObject || !custom) {
      return this.bpmnRenderer.drawShape(parentNode, element);
    }

    const kind = nodeKind(element);
    const color = COLORS[kind] || COLORS.default;
    const w = element.width || 100;
    const h = element.height || 60;
    const bo = element.businessObject || {};
    const title = bo.name || defaultTitle(kind);
    const compact = h < 60 || w < 60; // 事件类矮卡片：紧凑布局

    // 背景卡片
    const rect = svgCreate("rect");
    svgAttr(rect, {
      x: 0,
      y: 0,
      width: w,
      height: h,
      rx: 12,
      ry: 12,
      fill: "#ffffff",
      stroke: color,
      strokeWidth: 1.5,
    });
    svgClasses(rect).add("lf-node-bg");
    svgAppend(parentNode, rect);

    // 头部彩色图标方块
    const icoSize = compact ? 22 : 26;
    const pad = 12;
    const iy = compact ? (h - icoSize) / 2 : pad;
    const icoBox = svgCreate("rect");
    svgAttr(icoBox, { x: pad, y: iy, width: icoSize, height: icoSize, rx: 8, ry: 8, fill: color });
    svgAppend(parentNode, icoBox);
    const ico = svgCreate("text");
    svgAttr(ico, {
      x: pad + icoSize / 2,
      y: iy + icoSize / 2 + 4.5,
      "text-anchor": "middle",
      "font-size": 14,
      fill: "#fff",
      "font-family": "sans-serif",
    });
    ico.textContent = ICONS[kind] || ICONS.default;
    svgAppend(parentNode, ico);

    // 标题
    const titleEl = svgCreate("text");
    svgAttr(titleEl, {
      x: pad + icoSize + 8,
      y: compact ? h / 2 + 4.5 : pad + icoSize / 2 + 1,
      "font-size": 13,
      "font-weight": 700,
      fill: "#1F2937",
      "font-family": FONT,
    });
    titleEl.textContent = truncate(title, 9);
    svgAppend(parentNode, titleEl);

    // 描述行（仅非紧凑卡片显示）
    if (!compact) {
      const desc = describe(element);
      if (desc) {
        const descEl = svgCreate("text");
        svgAttr(descEl, {
          x: pad,
          y: pad + icoSize + 16,
          "font-size": 11,
          fill: "#6B7280",
          "font-family": FONT,
        });
        descEl.textContent = truncate(desc, 18);
        svgAppend(parentNode, descEl);
      }
    }

    // 右侧 port 圆点：拖拽即可连线（贴近原型"添加下一个节点"）
    if (kind !== "end") {
      const port = svgCreate("circle");
      svgAttr(port, {
        cx: w,
        cy: h / 2,
        r: 6,
        fill: "#ffffff",
        stroke: color,
        strokeWidth: 2,
        cursor: "crosshair",
      });
      svgClasses(port).add("lf-port");
      svgAppend(parentNode, port);
      port.addEventListener("mousedown", (e) => {
        e.stopPropagation();
        this._connect.start(e, element);
      });
    }

    return rect;
  }

  drawConnection(parentGfx, connection) {
    const waypoints = connection.waypoints;
    if (!waypoints || waypoints.length < 2 || !connection.source || !connection.target) {
      return this.bpmnRenderer.drawConnection(parentGfx, connection);
    }
    const pts = waypoints.map((p) => `${p.x},${p.y}`).join(" ");
    const path = svgCreate("polyline");
    svgAttr(path, {
      points: pts,
      fill: "none",
      stroke: "#94A3B8",
      "stroke-width": 2,
      "stroke-linejoin": "round",
    });
    svgClasses(path).add("lf-conn-path");
    svgAppend(parentGfx, path);

    const a = waypoints[waypoints.length - 2];
    const b = waypoints[waypoints.length - 1];
    const angle = Math.atan2(b.y - a.y, b.x - a.x);
    drawArrow(parentGfx, b.x, b.y, angle, "#94A3B8");
    return path;
  }
}

CustomRenderer.$inject = ["eventBus", "bpmnRenderer", "connect"];
