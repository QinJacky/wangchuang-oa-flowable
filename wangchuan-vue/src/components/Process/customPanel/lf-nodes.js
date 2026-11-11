// LogicFlow 自定义节点：LogicFlow 风格彩色卡片（圆角矩形 + 彩色图标 + 标题）
// 节点类型沿用 BPMN 语义（startEvent/userTask/serviceTask/exclusiveGateway/endEvent），
// 业务字段统一存放在 properties 中（见 OA流程引擎节点详细设计）。
import { RectNode, RectNodeModel, h } from "@logicflow/core";

// 配色与图标（与 OA流程节点设计文档 对齐）
const COLORS = {
  start: "#3b82f6", // 发起 蓝
  end: "#64748b", // 结束 灰
  approve: "#f59e0b", // 审批 橙
  cc: "#10b981", // 抄送 绿
  cond: "#ec4899", // 条件 粉
  parallel: "#0EA5E9", // 并行 青（保留兼容导入）
  default: "#3b82f6",
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
const KIND = {
  startEvent: "start",
  endEvent: "end",
  userTask: "approve",
  serviceTask: "cc",
  exclusiveGateway: "cond",
  parallelGateway: "parallel",
  inclusiveGateway: "cond",
};

function defaultTitle(type) {
  return {
    startEvent: "发起",
    endEvent: "结束",
    userTask: "审批",
    serviceTask: "抄送",
    exclusiveGateway: "条件分支",
  }[type] || "节点";
}

function truncate(str, n) {
  if (!str) return "";
  const arr = Array.from(String(str));
  return arr.length > n ? arr.slice(0, n).join("") + "…" : arr.join("");
}

function sizeOf(type) {
  const isGw = (type || "").indexOf("Gateway") !== -1;
  return isGw ? { w: 56, h: 56 } : { w: 150, h: 54 };
}

class CardModel extends RectNodeModel {
  initNodeData(data) {
    super.initNodeData(data);
    this.radius = 10;
    this.text = { value: "" }; // 标题由自定义卡片绘制，关闭默认文本
    const s = sizeOf(data.type);
    // 无条件覆盖 LogicFlow 默认的 100x80，确保 sizeOf 始终生效
    this.width = s.w;
    this.height = s.h;
  }
  getNodeStyle() {
    const style = super.getNodeStyle();
    const kind = KIND[this.type] || "default";
    style.fill = "#ffffff";
    style.stroke = COLORS[kind];
    style.strokeWidth = 1.5;
    return style;
  }
}

class CardView extends RectNode {
  getShape() {
    const { model } = this.props;
    const { x, y, width, height, radius } = model;
    const left = x - width / 2;
    const top = y - height / 2;
    const kind = KIND[model.type] || "default";
    const color = COLORS[kind];
    // 审批进度高亮：completed=绿（已完成）、todo=蓝（当前）、其余保持类型色
    const status = model.properties && model.properties.status;
    let strokeColor = color;
    let fillColor = "#ffffff";
    let strokeW = 1.5;
    if (status === "completed") {
      strokeColor = "#10B981";
      fillColor = "#ECFDF5";
    } else if (status === "todo") {
      strokeColor = "#2563EB";
      fillColor = "#EFF6FF";
      strokeW = 2.5;
    }
    const iconFill = status ? strokeColor : color;
    const isGw = (model.type || "").indexOf("Gateway") !== -1;
    const icoSize = 22;
    const icoX = left + (isGw ? (width - icoSize) / 2 : 12);
    const icoY = top + (height - icoSize) / 2;
    return h("g", {}, [
      h("rect", {
        x: left,
        y: top,
        width,
        height,
        rx: radius,
        ry: radius,
        fill: fillColor,
        stroke: strokeColor,
        strokeWidth: strokeW,
        className: "lf-node-bg",
      }),
      h("rect", {
        x: icoX,
        y: icoY,
        width: icoSize,
        height: icoSize,
        rx: 6,
        ry: 6,
        fill: iconFill,
      }),
      h(
        "text",
        {
          x: icoX + icoSize / 2,
          y: icoY + icoSize / 2 + 4,
          textAnchor: "middle",
          fontSize: 12,
          fill: "#ffffff",
        },
        ICONS[kind]
      ),
      h(
        "text",
        {
          x: isGw ? left + width / 2 : left + 42,
          y: isGw ? top + height + 14 : top + height / 2 + 4,
          fontSize: 12,
          fontWeight: 700,
          fill: "#1F2937",
          fontFamily: "PingFang SC, Microsoft YaHei, sans-serif",
          textAnchor: isGw ? "middle" : "start",
        },
        truncate(model.properties.name || defaultTitle(model.type), isGw ? 6 : 9)
      ),
    ]);
  }
}

// 注册类型：保留 bpmn 语义，新增 serviceTask（抄送）；parallel/inclusive 仅用于兼容旧 XML 导入
const TYPES = ["startEvent", "endEvent", "userTask", "serviceTask", "exclusiveGateway", "parallelGateway", "inclusiveGateway"];

export function registerFlowNodes(lf) {
  TYPES.forEach((type) => {
    lf.register({
      type,
      view: CardView,
      model: CardModel,
    });
  });
}
