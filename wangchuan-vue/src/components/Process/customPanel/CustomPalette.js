/**
 * 自定义左侧工具栏
 */
import { assign } from "min-dash";

export default function CustomPalette(
  palette,
  create,
  elementFactory,
  handTool,
  lassoTool,
  spaceTool,
  globalConnect,
  translate
) {
  this.create = create;
  this.elementFactory = elementFactory;
  this.handTool = handTool;
  this.lassoTool = lassoTool;
  this.spaceTool = spaceTool;
  this.globalConnect = globalConnect;
  this.translate = translate;

  palette.registerProvider(this);
}

CustomPalette.$inject = [
  "palette",
  "create",
  "elementFactory",
  "handTool",
  "lassoTool",
  "spaceTool",
  "globalConnect",
  "translate"
];

CustomPalette.prototype.getPaletteEntries = function (element) {
  const {
    create,
    elementFactory,
    handTool,
    lassoTool,
    spaceTool,
    globalConnect,
    translate
  } = this;

  // 图标 + 文字 的 HTML 片段（彩色图标方块 + 中文标签）
  function item(color, icon, label) {
    return (
      '<div class="lf-pal-item">' +
      '<span class="lf-pal-ico" style="background:' + color + '">' + icon + "</span>" +
      '<span class="lf-pal-label">' + label + "</span>" +
      "</div>"
    );
  }

  function createAction(type, group, className, title, options, html) {
    function createListener(event) {
      var shape = elementFactory.createShape(assign({ type: type }, options));

      if (options && options.isExpanded !== undefined) {
        shape.businessObject.di.isExpanded = options.isExpanded;
      }

      create.start(event, shape);
    }

    var shortType = type.replace(/^bpmn:/, "");

    return {
      group: group,
      className: (className ? className + " " : "") + "lf-pal",
      title: title || translate("Create {type}", { type: shortType }),
      html: html,
      action: {
        dragstart: createListener,
        click: createListener
      }
    };
  }

  function toolEntry(group, className, title, action, html) {
    return {
      group: group,
      className: (className ? className + " " : "") + "lf-pal",
      title: title,
      html: html,
      action: action
    };
  }

  const C = {
    start: "#10B981",
    end: "#6B7280",
    approve: "#2563EB",
    cond: "#8B5CF6",
    tool: "#64748B"
  };

  return {
    "hand-tool": toolEntry(
      "tools",
      "bpmn-icon-hand-tool",
      "移动画布",
      { click: function (event) { handTool.activateHand(event); } },
      item(C.tool, "✥", "移动")
    ),
    "lasso-tool": toolEntry(
      "tools",
      "bpmn-icon-lasso-tool",
      "框选",
      { click: function (event) { lassoTool.activateSelection(event); } },
      item(C.tool, "▦", "框选")
    ),
    "space-tool": toolEntry(
      "tools",
      "bpmn-icon-space-tool",
      "空间工具",
      { click: function (event) { spaceTool.activateSelection(event); } },
      item(C.tool, "↔", "空间")
    ),
    "global-connect-tool": toolEntry(
      "tools",
      "bpmn-icon-connection-multi",
      "全局连线",
      { click: function (event) { globalConnect.start(event); } },
      item(C.tool, "⤳", "连线")
    ),

    "tool-separator": {
      group: "tools",
      separator: true
    },

    "create.start-event": createAction(
      "bpmn:StartEvent",
      "event",
      "bpmn-icon-start-event-none",
      "创建开始节点",
      { width: 120, height: 52 },
      item(C.start, "▶", "开始")
    ),
    "create.end-event": createAction(
      "bpmn:EndEvent",
      "event",
      "bpmn-icon-end-event-none",
      "创建结束节点",
      { width: 120, height: 52 },
      item(C.end, "■", "结束")
    ),
    "create.user-task": createAction(
      "bpmn:UserTask",
      "activity",
      "bpmn-icon-user-task",
      "创建用户任务",
      { width: 170, height: 64 },
      item(C.approve, "✓", "审批")
    ),
    "create.exclusive-gateway": createAction(
      "bpmn:ExclusiveGateway",
      "gateway",
      "bpmn-icon-gateway-xor",
      "创建排他网关",
      { width: 64, height: 64 },
      item(C.cond, "◆", "条件")
    )
  };
};
