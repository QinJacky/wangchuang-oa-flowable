// 全局流程设计器共享状态（LogicFlow 版）
// 用 Vue.observable 保证响应式，designer 右侧面板可随选中元素变化刷新
import Vue from "vue";

const modelerStore = Vue.observable({
  // 基础数据
  userList: [],
  roleList: [],
  expList: [],

  // LogicFlow 实例与当前选中元素
  lf: null,
  element: null,
  processInfo: {
    id: "",
    name: "",
    processType: "",
    processTypeName: "",
    formInfo: { formType: "", formCode: "", formId: "", dynamicForm: {} },
    mainText: { mainTextFlag: "0", type: "0", limitSize: 200, limitType: "", fileId: "", fileName: "", extendName: "", tipContent: "" },
    attachment: { attachFlag: "0", name: "", limitSize: 500, limitType: "", tipContent: "" },
  },

  // ---- 生命周期 ----
  setLf(lf) {
    this.lf = lf;
  },
  setProcessInfo(info) {
    const base = {
      id: "",
      name: "",
      processType: "",
      processTypeName: "",
      formInfo: { formType: "", formCode: "", formId: "", dynamicForm: {} },
      mainText: { mainTextFlag: "0", type: "0", limitSize: 200, limitType: "", fileId: "", fileName: "", extendName: "", tipContent: "" },
      attachment: { attachFlag: "0", name: "", limitSize: 500, limitType: "", tipContent: "" },
    };
    const src = info || {};
    this.processInfo = Object.assign(base, src);
    this.processInfo.formInfo = Object.assign({ formType: "", formCode: "", formId: "", dynamicForm: {} }, src.formInfo || {});
    this.processInfo.mainText = Object.assign(
      { mainTextFlag: "0", type: "0", limitSize: 200, limitType: "", fileId: "", fileName: "", extendName: "", tipContent: "" },
      src.mainText || {}
    );
    this.processInfo.attachment = Object.assign(
      { attachFlag: "0", name: "", limitSize: 500, limitType: "", tipContent: "" },
      src.attachment || {}
    );
  },
  getProcessInfo() {
    return this.processInfo;
  },

  // ---- 选中元素 ----
  setElement(el) {
    this.element = el || null;
  },
  // 1.2.27 无 lf.getElement / lf.getNodeData / lf.getEdgeData，统一从图数据查找
  getElement(id) {
    if (!this.lf) return null;
    const g = this.lf.getGraphData() || { nodes: [], edges: [] };
    return (
      (g.nodes || []).find((n) => n.id === id) ||
      (g.edges || []).find((e) => e.id === id) ||
      null
    );
  },

  // ---- 属性读写（统一挂载到节点/连线的 properties 上）----
  // 兼容旧面板习惯：this.modelerStore.element.businessObject[key]
  get businessObject() {
    return this.element ? this.element.properties || {} : {};
  },
  updateProperties(id, partial) {
    if (!this.lf) return;
    const el = this.getElement(id);
    if (!el) return;
    const merged = Object.assign({}, el.properties || {}, partial);
    this.lf.setProperties(id, merged);
    // 同步触发选中元素响应式更新
    if (this.element && this.element.id === id) {
      this.element = Object.assign({}, this.element, { properties: merged });
    }
  },
  // 兼容旧属性面板直接删除 businessObject 上的键
  deleteProperty(id, key) {
    if (!this.lf) return;
    const el = this.getElement(id);
    if (!el) return;
    const props = Object.assign({}, el.properties || {});
    delete props[key];
    this.lf.setProperties(id, props);
    if (this.element && this.element.id === id) {
      this.element = Object.assign({}, this.element, { properties: props });
    }
  },
});

export { modelerStore };
// main.js: import modelerStore from '@/components/Process/common/global' 后挂到 Vue.prototype，
// 因此默认导出必须是 store 本身（this.modelerStore.element / this.modelerStore.userList 才能生效）
export default modelerStore;
