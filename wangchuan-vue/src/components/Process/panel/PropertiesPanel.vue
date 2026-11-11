<template>
  <div>
    <el-alert type="info" :closable="false" title="当前节点扩展属性（JSON）。修改后点击“应用”保存到流程数据。" />
    <el-input type="textarea" :rows="10" v-model="jsonText" style="margin-top: 8px; font-family: monospace" />
    <el-button size="mini" type="primary" style="margin-top: 8px" @click="apply">应用</el-button>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "PropertiesPanel",
  props: { id: { type: String, required: true } },
  data() {
    return { jsonText: "{}" };
  },
  watch: {
    id: { handler() { this.refresh(); }, immediate: true },
  },
  methods: {
    getProps() {
      const el = modelerStore.getElement(this.id);
      return (el && el.properties) || {};
    },
    refresh() {
      const p = this.getProps();
      const clone = Object.assign({}, p);
      delete clone.name;
      this.jsonText = JSON.stringify(clone, null, 2);
    },
    apply() {
      try {
        const obj = JSON.parse(this.jsonText);
        modelerStore.updateProperties(this.id, obj);
        this.$message.success("已保存");
      } catch (e) {
        this.$message.error("JSON 格式错误：" + e.message);
      }
    },
  },
};
</script>
