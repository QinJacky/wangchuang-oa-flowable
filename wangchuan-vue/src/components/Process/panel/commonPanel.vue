<template>
  <div>
    <el-form :model="formData" label-width="80px" :rules="rules" size="small">
      <el-form-item label="节点ID" prop="id">
        <el-input v-model="formData.id" :disabled="true" />
      </el-form-item>
      <el-form-item label="节点类型">
        <el-tag :color="typeColor" effect="dark" style="border: none">{{ typeLabel }}</el-tag>
      </el-form-item>
      <el-form-item label="节点名称" prop="name">
        <el-input v-model="formData.name" @change="updateProp('name')" placeholder="中文名称，在流程过程中显示" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

const TYPE_META = {
  startEvent: { label: "发起", color: "#3b82f6" },
  userTask: { label: "审批", color: "#f59e0b" },
  serviceTask: { label: "抄送", color: "#10b981" },
  exclusiveGateway: { label: "条件分支", color: "#ec4899" },
  endEvent: { label: "结束", color: "#64748b" },
  polyline: { label: "流转连线", color: "#94a3b8" },
};

export default {
  name: "CommonPanel",
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      rules: {
        name: [{ required: true, message: "节点名称不能为空", trigger: "blur" }],
      },
      formData: { id: "", name: "" },
    };
  },
  computed: {
    typeLabel() {
      const el = modelerStore.getElement(this.id);
      const t = el ? el.type : "";
      return (TYPE_META[t] && TYPE_META[t].label) || t || "节点";
    },
    typeColor() {
      const el = modelerStore.getElement(this.id);
      const t = el ? el.type : "";
      return (TYPE_META[t] && TYPE_META[t].color) || "#3b82f6";
    },
  },
  watch: {
    id: {
      handler() {
        this.resetForm();
      },
      immediate: true,
    },
  },
  methods: {
    resetForm() {
      const el = modelerStore.getElement(this.id);
      const p = (el && el.properties) || {};
      this.formData = { id: this.id, name: p.name || "" };
    },
    updateProp(key) {
      modelerStore.updateProperties(this.id, { [key]: this.formData[key] || "" });
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-form-item__label {
  font-weight: normal;
}
</style>
