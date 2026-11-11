<template>
  <div class="flow-containers" :class="{ 'view-mode': isView }">
    <el-container style="height: 100%">
      <el-header class="lf-header" style="border-bottom: 1px solid rgb(218 218 218); height: auto; padding-left: 0">
        <div style="display: flex; padding: 10px 0; justify-content: space-between">
          <el-button-group>
            <el-tooltip effect="dark" content="新建" placement="bottom">
              <el-button size="mini" icon="el-icon-circle-plus" @click="newDiagram" />
            </el-tooltip>
            <el-tooltip effect="dark" content="适应屏幕" placement="bottom">
              <el-button size="mini" icon="el-icon-rank" @click="fitViewport" />
            </el-tooltip>
            <el-tooltip effect="dark" content="放大" placement="bottom">
              <el-button size="mini" icon="el-icon-zoom-in" @click="zoomViewport(true)" />
            </el-tooltip>
            <el-tooltip effect="dark" content="缩小" placement="bottom">
              <el-button size="mini" icon="el-icon-zoom-out" @click="zoomViewport(false)" />
            </el-tooltip>
            <el-tooltip effect="dark" content="撤销" placement="bottom">
              <el-button size="mini" icon="el-icon-back" @click="lf && lf.undo && lf.undo()" />
            </el-tooltip>
            <el-tooltip effect="dark" content="重做" placement="bottom">
              <el-button size="mini" icon="el-icon-right" @click="lf && lf.redo && lf.redo()" />
            </el-tooltip>
          </el-button-group>
          <el-button-group>
            <el-tooltip effect="dark" content="查看xml" placement="bottom">
              <el-button size="mini" icon="el-icon-view" @click="showXML" />
            </el-tooltip>
            <el-tooltip effect="dark" content="下载xml" placement="bottom">
              <el-button size="mini" icon="el-icon-download" @click="saveXML(true)" />
            </el-tooltip>
            <el-tooltip effect="dark" content="下载svg" placement="bottom">
              <el-button size="mini" icon="el-icon-picture" @click="saveImg(true)" />
            </el-tooltip>
            <el-button size="mini" type="info" @click="goBack">关闭</el-button>
            <el-button size="mini" type="primary" @click="save">保存模型</el-button>
          </el-button-group>
        </div>
      </el-header>
      <el-container style="align-items: stretch; position: relative">
        <!-- 左侧节点栏：与右侧属性栏同为独立列，画布不与其重叠 -->
        <el-card shadow="never" class="palettePanel">
          <div
            v-for="item in paletteItems"
            :key="item.type"
            class="lf-dnd-item"
            @mousedown="startDrag(item)"
          >
            <span class="lf-dnd-shape" :style="{ background: item.color }">{{ item.icon }}</span>
            <span class="lf-dnd-text">{{ item.label }}</span>
          </div>
        </el-card>
        <el-main class="lf-main">
          <div ref="canvas" class="canvas" />

          <!-- 连线中点 + 号（点击展开追加菜单） -->
          <div
            v-if="hoverEdge && !isView"
            class="lf-edge-plus"
            :style="{ left: hoverEdge.x + 'px', top: hoverEdge.y + 'px' }"
            @mouseenter="cancelClear"
            @mouseleave="onEdgeLeave"
            @click.stop="openInsertMenu(hoverEdge)"
          >
            +
          </div>

          <!-- 追加节点气泡菜单 -->
          <div
            v-if="insertMenu"
            class="lf-insert-menu"
            :style="{ left: insertMenu.x + 'px', top: insertMenu.y + 'px' }"
          >
            <div
              v-for="opt in insertOptions"
              :key="opt.type"
              class="lf-insert-opt"
              @click.stop="insertIntoEdge(opt.type)"
            >
              <span class="lf-pal-ico" :style="{ background: opt.color }">{{ opt.icon }}</span>
              {{ opt.label }}
            </div>
          </div>

          <!-- 节点悬浮操作（齿轮/删除） -->
          <div
            v-if="hoverNode && !isView"
            class="lf-node-tools"
            :style="{ left: hoverNode.x + 'px', top: hoverNode.y + 'px' }"
            @mouseenter="cancelClear"
            @mouseleave="onNodeLeave"
          >
            <el-button
              circle
              size="mini"
              icon="el-icon-delete"
              title="删除"
              @click.stop="deleteNodeWithReconnect(hoverNode.id)"
            />
          </div>
        </el-main>

        <!-- 右侧属性栏 -->
        <el-card shadow="never" class="normalPanel">
          <designer />
        </el-card>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import LogicFlow from "@logicflow/core";
import "@logicflow/core/dist/style/index.css";
import { Menu } from "@logicflow/extension";
import "@logicflow/extension/lib/style/index.css";
import { modelerStore } from "./common/global";
import { registerFlowNodes } from "./customPanel/lf-nodes";
import { graphToBpmn, bpmnToGraph } from "./common/flowableBpmn";
import Designer from "./designer";
import { StrUtil } from "@/utils/StrUtil";

// 调色板：按 OA流程节点设计文档 的节点与配色（发起为自动创建节点，不在此列出）
const PALETTE = [
  { type: "userTask", label: "审批", icon: "✓", color: "#f59e0b" },
  { type: "serviceTask", label: "抄送", icon: "✉", color: "#10b981" },
  { type: "exclusiveGateway", label: "条件", icon: "◆", color: "#ec4899" },
  { type: "endEvent", label: "结束", icon: "■", color: "#64748b" },
];
const INSERT_OPTS = [
  { type: "userTask", label: "审批节点", icon: "✓", color: "#f59e0b" },
  { type: "serviceTask", label: "抄送节点", icon: "✉", color: "#10b981" },
  { type: "exclusiveGateway", label: "条件分支", icon: "◆", color: "#ec4899" },
  { type: "endEvent", label: "结束节点", icon: "■", color: "#64748b" },
];

// 各节点类型的业务默认属性（挂在 LogicFlow 节点的 properties 上）
const BUSINESS_DEFAULTS = {
  startEvent: () => ({ name: "发起", startFormKey: "" }),
  userTask: () => ({
    name: "审批",
    assigneeType: "fixedUser",
    assigneeValue: "",
    assigneeLabel: "",
    multiMode: "all",
    sequential: false,
    allowApprove: true,
    allowReject: true,
    rejectTargets: ["initiator"],
    commentRequired: false,
    fieldPermissions: {},
    dueDuration: "",
    overdueAction: "",
  }),
  serviceTask: () => ({ name: "抄送", copyType: "fixedUser", copyValue: "", copyLabel: "", notifyChannels: ["site"] }),
  exclusiveGateway: () => ({ name: "条件分支" }),
  endEvent: () => ({ name: "结束" }),
};
function defaultBusinessProps(type) {
  const f = BUSINESS_DEFAULTS[type];
  return f ? JSON.parse(JSON.stringify(f())) : { name: "节点" };
}

export default {
  name: "BpmnModel",
  components: { Designer },
  props: {
    xml: { type: String, default: "" },
    isView: { type: Boolean, default: false },
  },
  data() {
    return {
      lf: null,
      paletteItems: PALETTE,
      insertOptions: INSERT_OPTS,
      insertMenu: null, // { edgeId, x, y }
      hoverEdge: null, // { id, x, y }
      hoverNode: null, // { id, x, y }
    };
  },
  computed: {
    processInfo() {
      return modelerStore.processInfo;
    },
  },
  watch: {
    xml: {
      handler(val) {
        if (StrUtil.isNotBlank(val)) this.loadXml(val);
        else this.newDiagram();
      },
      immediate: false,
    },
  },
  mounted() {
    const lf = new LogicFlow({
      container: this.$refs.canvas,
      grid: { size: 12, type: "mesh", config: { color: "#e6e9f0" } },
      keyboard: { enabled: true },
      plugins: [Menu],
    });
    registerFlowNodes(lf);
    // 左侧节点栏在模板中独立成列（与右侧属性栏同级），拖拽走 lf.dnd.startDrag
    // 右键菜单：节点可删除/复制（删除自动重连前后连线），连线可删除
    lf.setMenuConfig({
      nodeMenu: [
        { text: "删除", callback: (node) => this.deleteNodeWithReconnect(node.id) },
        { text: "复制", callback: (node) => this.lf.cloneNode(node.id) },
      ],
      edgeMenu: [{ text: "删除", callback: (edge) => this.lf.deleteEdge(edge.id) }],
    });
    this.lf = lf;
    modelerStore.setLf(lf);

    lf.on("node:click", ({ data }) => modelerStore.setElement(data));
    lf.on("edge:click", ({ data }) => modelerStore.setElement(data));
    lf.on("blank:click", () => {
      modelerStore.setElement(null);
      this.clearHover();
    });
    lf.on("node:mouseenter", ({ data, e }) => this.showNodeTools(data, e));
    lf.on("node:mouseleave", this.onNodeLeave);
    lf.on("edge:mouseenter", ({ data }) => this.showEdgePlus(data));
    lf.on("edge:mouseleave", this.onEdgeLeave);
    lf.on("graph:transform", () => this.clearHover());
    lf.on("node:drag", () => this.clearHover());

    if (StrUtil.isNotBlank(this.xml)) this.loadXml(this.xml);
    else this.newDiagram();
    // 限制拖放区域：仅中间画布可放置节点；左(节点栏)/右(属性栏)面板拦截 drop，避免节点被面板遮挡
    this._blockPanelDrop = (e) => {
      const t = e.target;
      if (t && t.closest && t.closest(".palettePanel, .normalPanel")) {
        // 仅阻止事件继续冒泡到画布（避免 LogicFlow 在面板区域创建节点）；
        // dragover 不 preventDefault，使面板保持“禁止放置”光标，体验更佳
        e.stopPropagation();
        if (e.type === "drop") e.preventDefault();
      }
    };
    window.addEventListener("drop", this._blockPanelDrop, true);
    window.addEventListener("dragover", this._blockPanelDrop, true);
  },
  beforeDestroy() {
    if (this._blockPanelDrop) {
      window.removeEventListener("drop", this._blockPanelDrop, true);
      window.removeEventListener("dragover", this._blockPanelDrop, true);
    }
  },
  methods: {
    // 模型坐标 -> 容器内像素坐标（1.2.27 字段为 SCALE_X/TRANSLATE_X 大写）
    toScreen(point) {
      const tm = (this.lf.graphModel && this.lf.graphModel.transformModel) || {};
      const tx = tm.TRANSLATE_X || 0;
      const ty = tm.TRANSLATE_Y || 0;
      const sx = tm.SCALE_X || 1;
      const sy = tm.SCALE_Y || 1;
      return { x: tx + point.x * sx, y: ty + point.y * sy };
    },
    clearHover() {
      this.clearTimer && clearTimeout(this.clearTimer);
      this.hoverEdge = null;
      this.hoverNode = null;
      this.insertMenu = null;
    },
    // 鼠标移出时延迟清除，给“移到 + 号/工具按钮”留出时间
    onEdgeLeave() {
      this.clearTimer && clearTimeout(this.clearTimer);
      this.clearTimer = setTimeout(() => {
        this.hoverEdge = null;
        this.insertMenu = null;
      }, 200);
    },
    onNodeLeave() {
      this.clearTimer && clearTimeout(this.clearTimer);
      this.clearTimer = setTimeout(() => {
        this.hoverNode = null;
      }, 200);
    },
    cancelClear() {
      this.clearTimer && clearTimeout(this.clearTimer);
    },
    showEdgePlus(data) {
      const pts = (data.pointsList && data.pointsList.length ? data.pointsList : []);
      let mid = { x: 0, y: 0 };
      if (pts.length) {
        const a = pts[Math.floor((pts.length - 1) / 2)];
        const b = pts[Math.floor((pts.length - 1) / 2) + 1] || a;
        mid = { x: (a.x + b.x) / 2, y: (a.y + b.y) / 2 };
      }
      const s = this.toScreen(mid);
      this.hoverEdge = { id: data.id, x: s.x, y: s.y };
      this.insertMenu = null;
    },
    showNodeTools(data, e) {
      // 用节点真实 DOM 矩形定位工具按钮，避免模型坐标换算误差
      let rect = null;
      if (e && e.target) {
        let el = e.target;
        while (el && el.tagName && el.tagName.toLowerCase() !== "g") el = el.parentElement;
        if (el && el.getBoundingClientRect) rect = el.getBoundingClientRect();
      }
      let right, top;
      if (rect) {
        right = rect.right;
        top = rect.top;
      } else {
        const s = this.toScreen({ x: data.x, y: data.y });
        right = s.x + 60;
        top = s.y - 32;
      }
      this.hoverNode = { id: data.id, x: right - 14, y: top - 14 };
    },
    openInsertMenu(edge) {
      this.insertMenu = { edgeId: edge.id, x: edge.x + 12, y: edge.y + 12 };
    },
    // 在连线中间插入节点：删除原连线，新增节点并补两条连线（自动重连）
    insertIntoEdge(type) {
      const edgeId = this.insertMenu.edgeId;
      const g = this.lf.getGraphData();
      const edge = (g.edges || []).find((e) => e.id === edgeId);
      if (!edge) return;
      const mid = this.edgeMid(edge);
      const node = this.lf.addNode({
        type,
        x: mid.x,
        y: mid.y,
        properties: defaultBusinessProps(type),
      });
      this.lf.deleteEdge(edgeId);
      this.lf.addEdge({ type: "polyline", sourceNodeId: edge.sourceNodeId, targetNodeId: node.id });
      this.lf.addEdge({ type: "polyline", sourceNodeId: node.id, targetNodeId: edge.targetNodeId });
      this.insertMenu = null;
      this.hoverEdge = null;
    },
    edgeMid(edge) {
      const pts = edge.pointsList || [];
      if (pts.length) {
        const a = pts[Math.floor((pts.length - 1) / 2)];
        const b = pts[Math.floor((pts.length - 1) / 2) + 1] || a;
        return { x: (a.x + b.x) / 2, y: (a.y + b.y) / 2 };
      }
      return { x: 0, y: 0 };
    },
    // 删除节点并自动重连前后连线
    deleteNodeWithReconnect(nodeId) {
      const g = this.lf.getGraphData();
      const node = (g.nodes || []).find((n) => n.id === nodeId);
      if (!node) return;
      const gm = this.lf.graphModel;
      const ins = gm.getNodeIncomingNode(nodeId) || [];
      const outs = gm.getNodeOutgoingNode(nodeId) || [];
      ins.forEach((s) =>
        outs.forEach((t) => {
          if (s.id !== t.id) {
            this.lf.addEdge({ type: "polyline", sourceNodeId: s.id, targetNodeId: t.id });
          }
        })
      );
      this.lf.deleteNode(nodeId);
      if (modelerStore.element && modelerStore.element.id === nodeId) modelerStore.setElement(null);
      this.hoverNode = null;
    },
    defaultName(type) {
      return { userTask: "审批", serviceTask: "抄送", exclusiveGateway: "条件分支", parallelGateway: "并行", endEvent: "结束" }[type] || "节点";
    },

    // 左侧节点栏拖拽：与官方 DndPanel 相同的底层 API（lf.dnd.startDrag）
    startDrag(item) {
      this.lf && this.lf.dnd && this.lf.dnd.startDrag({ type: item.type, properties: defaultBusinessProps(item.type) });
    },


    newDiagram() {
      // 新建流程默认包含“开始”（左侧）与“结束”（右侧）两个必备节点
      const graph = {
        nodes: [
          { id: "StartEvent_1", type: "startEvent", x: 200, y: 240, properties: defaultBusinessProps("startEvent") },
          { id: "EndEvent_1", type: "endEvent", x: 520, y: 240, properties: defaultBusinessProps("endEvent") },
        ],
        edges: [],
      };
      this.lf.render(graph);
      modelerStore.setProcessInfo({ id: "flow_" + Math.random().toString(36).slice(-8), name: "新建流程" });
      modelerStore.setElement(null);
      this.scheduleFit();
    },
    loadXml(xml) {
      try {
        const { graph, processInfo } = bpmnToGraph(xml);
        this.lf.render(graph);
        modelerStore.setProcessInfo(processInfo);
        modelerStore.setElement(null);
        this.scheduleFit();
      } catch (err) {
        console.error("流程XML解析失败", err);
        this.newDiagram();
      }
    },
    // 等画布容器尺寸真正稳定后再居中（双 rAF），避免初始化时容器宽度尚未算好导致图被甩到左侧
    scheduleFit() {
      if (this._fitRaf) cancelAnimationFrame(this._fitRaf);
      this._fitRaf = requestAnimationFrame(() =>
        requestAnimationFrame(() => this.fitViewport())
      );
    },

    fitViewport() {
      if (!this.lf) return;
      const data = this.lf.getGraphData();
      // 空画布或仅单个节点时，不放大（保持 1:1 并居中），避免 fitView 把单个节点撑满整个画布
      const single = !data.nodes || data.nodes.length <= 1;
      if (single) {
        this.lf.zoom(1);
        if (typeof this.lf.translateCenter === "function") this.lf.translateCenter();
        return;
      }
      this.lf.fitView(20);
      // 极端情况下 fitView 仍放大（极少数节点），限制最大 1:1
      const s = this.lf.graphModel.transformModel.SCALE_X;
      if (s > 1) this.lf.zoom(1);
    },
    zoomViewport(zoomIn = true) {
      this.lf && this.lf.zoom(zoomIn);
    },

    getProcess() {
      const info = modelerStore.getProcessInfo();
      return {
        id: info.id,
        name: info.name,
        category: info.processType || "",
        processType: info.processType,
        processTypeName: info.processTypeName,
        formInfo: info.formInfo,
        mainText: info.mainText,
        attachment: info.attachment,
      };
    },
    async saveXML(download = false) {
      const xml = graphToBpmn(this.lf.getGraphData(), modelerStore.getProcessInfo());
      if (download) this.downloadFile(`${modelerStore.getProcessInfo().name || "process"}.bpmn20.xml`, xml, "application/xml");
      return xml;
    },
    async showXML() {
      const xml = await this.saveXML();
      this.$emit("showXML", xml);
    },
    async saveImg(download = false) {
      // 1.2.27 无 lf.getSnapshot，直接序列化画布 SVG
      const svgEl = document.querySelector(".canvas .lf-canvas-overlay");
      let svg = svgEl ? svgEl.outerHTML : "";
      if (svg && svg.indexOf("xmlns=") === -1) {
        svg = svg.replace("<svg", '<svg xmlns="http://www.w3.org/2000/svg"');
      }
      if (download) this.downloadFile(modelerStore.getProcessInfo().name || "process", svg, "image/svg+xml");
      return svg;
    },
    async save() {
      // 保存前校验：不允许存在完全未连接（无任何连入/连出）的游离节点
      const graph = this.lf.getGraphData();
      const linked = new Set();
      (graph.edges || []).forEach((e) => {
        if (e.sourceNodeId) linked.add(e.sourceNodeId);
        if (e.targetNodeId) linked.add(e.targetNodeId);
      });
      const orphans = (graph.nodes || []).filter((n) => !linked.has(n.id));
      if (orphans.length) {
        const names = orphans
          .map((n) => (n.properties && n.properties.name) || n.type || n.id)
          .join("、");
        this.$message.error("存在未连接的游离节点：" + names);
        return;
      }
      const xml = await this.saveXML();
      const svg = await this.saveImg();
      const result = { process: this.getProcess(), xml, svg };
      this.$emit("save", result);
      window.parent.postMessage(result, "*");
    },
    downloadFile(filename, data, type) {
      const a = document.createElement("a");
      const url = window.URL.createObjectURL(new Blob([data], { type }));
      a.href = url;
      a.download = filename;
      a.click();
      window.URL.revokeObjectURL(url);
    },
    goBack() {
      this.$tab.closeOpenPage({ path: "/workflow/flowable/definition", query: { t: Date.now() } });
    },
  },
};
</script>

<style lang="scss">
.flow-containers {
  width: 100%;
  height: 100%;
  .lf-header {
    background: #fff;
  }
  .lf-main {
    position: relative;
    padding: 0;
    overflow: hidden;
  }
  .canvas {
    width: 100%;
    height: 100%;
    position: relative;
    z-index: 1;
    background: radial-gradient(circle, #d8e0f0 1px, transparent 1px) 0 0 / 22px 22px, #fbfcfe !important;
  }
  .normalPanel {
    width: 460px;
    height: 100%;
    padding: 20px;
    position: relative;
    z-index: 2;
  }
  /* 左侧节点栏：独立列（与右侧属性栏同级），不悬浮在画布上 */
  .palettePanel {
    width: 200px;
    height: 100%;
    padding: 12px 8px;
    position: relative;
    z-index: 2;
    flex-shrink: 0;
  }

  /* 连线 + 号 */
  .lf-edge-plus {
    position: absolute;
    z-index: 6;
    width: 20px;
    height: 20px;
    margin: -10px 0 0 -10px;
    border-radius: 50%;
    background: #10b981;
    color: #fff;
    font-size: 16px;
    line-height: 20px;
    text-align: center;
    cursor: pointer;
    box-shadow: 0 1px 3px rgba(16, 24, 40, 0.2);
  }
  .lf-insert-menu {
    position: absolute;
    z-index: 7;
    min-width: 132px;
    background: #fff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(16, 24, 40, 0.16);
    padding: 4px;
    .lf-insert-opt {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 6px 8px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 13px;
      &:hover {
        background: #eff6ff;
      }
    }
  }
  .lf-node-tools {
    position: absolute;
    z-index: 6;
  }

  /* 选中节点：蓝色描边（LogicFlow 默认 selected 类） */
  .logicflow .selected rect,
  .lf-node-bg.selected {
    stroke: #2563eb !important;
    stroke-width: 2px !important;
  }

  .view-mode {
    .lf-header,
    .palettePanel,
    .normalPanel {
      display: none;
    }
  }
}
</style>

<!-- LogicFlow 官方右键 Menu 动态挂载到画布容器 DOM，scoped 样式不生效，需全局样式；左侧节点栏复用下方 .lf-dnd-* 全局样式 -->
<style>
.lf-dnd-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 132px;
  padding: 8px 10px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
  cursor: grab;
  box-sizing: border-box;
  user-select: none;
}
.lf-dnd-item:hover {
  background: #eff6ff;
  border-color: #93c5fd;
}
.lf-dnd-shape {
  flex: 0 0 auto;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
}
.lf-dnd-text {
  font-size: 13px;
  color: #1f2937;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}
.lf-dnd-approve .lf-dnd-shape { background: #2563EB; }
.lf-dnd-approve .lf-dnd-shape::before { content: "✓"; }
.lf-dnd-cond .lf-dnd-shape { background: #8B5CF6; }
.lf-dnd-cond .lf-dnd-shape::before { content: "◆"; }
.lf-dnd-parallel .lf-dnd-shape { background: #0EA5E9; }
.lf-dnd-parallel .lf-dnd-shape::before { content: "⇉"; }
.lf-dnd-end .lf-dnd-shape { background: #6B7280; }
.lf-dnd-end .lf-dnd-shape::before { content: "■"; }

/* LogicFlow 官方右键菜单兜底样式 */
.lf-menu {
  position: absolute;
  z-index: 40;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  box-shadow: 0 2px 10px rgba(16, 24, 40, 0.15);
  padding: 4px 0;
  min-width: 120px;
  list-style: none;
  margin: 0;
}
.lf-menu-item {
  padding: 8px 16px;
  font-size: 13px;
  color: #1f2937;
  cursor: pointer;
}
.lf-menu-item:hover {
  background: #f1f5f9;
}
</style>
