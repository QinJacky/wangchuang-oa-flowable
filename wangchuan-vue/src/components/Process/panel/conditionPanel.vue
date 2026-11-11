<template>
  <div>
    <el-form label-width="100px" size="small" @submit.native.prevent>
      <el-form-item>
        <template slot="label">
          <span>
            流转类型
            <el-tooltip placement="top">
              <template slot="content">
                <div>普通流转：流程沿出口继续执行。</div>
                <div>默认流转：仅当其它出口条件都不满足时才走（忽略条件）。</div>
                <div>条件流转：计算出口条件，为 true 时走该分支。</div>
              </template>
              <i class="el-icon-question" />
            </el-tooltip>
          </span>
        </template>
        <el-select v-model="formData.type" @change="changeType">
          <el-option label="普通流转路径" value="normal" />
          <el-option label="默认流转路径" value="default" />
          <el-option label="条件流转路径" value="condition" />
        </el-select>
      </el-form-item>

      <el-alert
        v-if="formData.type === 'condition' && !hasForm"
        type="warning"
        :closable="false"
        class="expr-alert"
        title="尚未绑定表单：请先在『流程基本信息』中绑定表单，才能选择表单字段配置条件。"
      />

      <template v-if="formData.type === 'condition'">
        <el-divider content-position="left">条件（全部满足才算通过）</el-divider>
        <div v-for="(cond, idx) in formData.conditions" :key="idx" class="cond-row">
          <el-select
            v-model="cond.field"
            filterable
            allow-create
            default-first-option
            placeholder="表单字段"
            size="small"
            style="width: 140px"
          >
            <el-option v-for="f in fieldOptions" :key="f.value" :label="f.label" :value="f.value" />
          </el-select>
          <el-select v-model="cond.operator" size="small" style="width: 80px">
            <el-option label="等于" value="eq" />
            <el-option label="不等于" value="ne" />
            <el-option label="大于" value="gt" />
            <el-option label="小于" value="lt" />
            <el-option label="≥" value="ge" />
            <el-option label="≤" value="le" />
            <el-option label="属于" value="in" />
          </el-select>
          <el-input v-model="cond.value" placeholder="值" size="small" style="width: 110px" />
          <i class="el-icon-remove-outline" @click="removeRow(idx)" />
        </div>
        <el-button type="text" icon="el-icon-plus" size="mini" @click="addRow">添加条件</el-button>
        <el-alert
          v-if="expr"
          type="success"
          :closable="false"
          class="expr-alert"
          :title="'生成表达式：' + expr"
        />
      </template>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";
import { buildConditionExpression } from "../common/flowableBpmn";
import { getDynamicForm } from "@/api/workflow/dynamicForm";

export default {
  name: "ConditionPanel",
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      formData: { type: "normal", conditions: [] },
      fieldOptions: [],
    };
  },
  computed: {
    expr() {
      return buildConditionExpression(this.formData.conditions);
    },
    sourceId() {
      const el = modelerStore.getElement(this.id);
      return el ? el.sourceNodeId : null;
    },
    hasForm() {
      const fi = (modelerStore.processInfo && modelerStore.processInfo.formInfo) || {};
      return !!fi.formId;
    },
  },
  watch: {
    id: {
      handler() {
        this.reset();
      },
      immediate: true,
    },
    "formData.conditions": {
      deep: true,
      handler() {
        this.syncCondition();
      },
    },
    // 绑定表单变化（或首次进入）时重新加载可选字段
    "modelerStore.processInfo.formInfo": {
      deep: true,
      handler() {
        this.loadFields();
      },
    },
  },
  created() {
    this.loadFields();
  },
  methods: {
    // 从绑定的动态表单中提取字段，供条件表达式选择
    async loadFields() {
      const fi = (modelerStore.processInfo && modelerStore.processInfo.formInfo) || {};
      this.fieldOptions = [];
      if (!fi.formId) return;
      try {
        const res = await getDynamicForm(fi.formId);
        const content = res && res.data && res.data.content;
        if (!content) return;
        const formConf = typeof content === "string" ? JSON.parse(content) : content;
        const fields = (formConf && formConf.fields) || [];
        const opts = [];
        const walk = (list) => {
          (list || []).forEach((f) => {
            if (f && f.__vModel__ != null) {
              opts.push({ value: f.__vModel__, label: (f.__config__ && f.__config__.label) || f.__vModel__ });
            }
            if (f && f.__config__ && f.__config__.children) walk(f.__config__.children);
            if (f && f.children) walk(f.children);
          });
        };
        walk(fields);
        this.fieldOptions = opts;
      } catch (e) {
        this.fieldOptions = [];
      }
    },
    reset() {
      const el = modelerStore.getElement(this.id);
      const p = (el && el.properties) || {};
      if (p.isDefault) {
        this.formData = { type: "default", conditions: [] };
      } else if (p.conditions && p.conditions.length) {
        this.formData = { type: "condition", conditions: JSON.parse(JSON.stringify(p.conditions)) };
      } else {
        this.formData = { type: "normal", conditions: [] };
      }
    },
    addRow() {
      this.formData.conditions.push({ field: "", operator: "eq", value: "" });
    },
    removeRow(idx) {
      this.formData.conditions.splice(idx, 1);
    },
    changeType(type) {
      if (type === "condition") {
        if (!this.formData.conditions.length) this.addRow();
        modelerStore.updateProperties(this.id, {
          conditions: this.formData.conditions,
          conditionExpression: this.expr,
          isDefault: false,
        });
        return;
      }
      if (type === "default") {
        if (this.sourceId) modelerStore.updateProperties(this.sourceId, { default: this.id });
        modelerStore.updateProperties(this.id, { conditions: [], conditionExpression: "", isDefault: true });
        return;
      }
      // normal
      if (this.sourceId) modelerStore.updateProperties(this.sourceId, { default: "" });
      modelerStore.updateProperties(this.id, { conditions: [], conditionExpression: "", isDefault: false });
    },
    // 条件行变化时实时同步
    syncCondition() {
      if (this.formData.type === "condition") {
        modelerStore.updateProperties(this.id, {
          conditions: this.formData.conditions,
          conditionExpression: this.expr,
        });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-form-item__label {
  font-weight: normal;
}
.cond-row {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
  .el-icon-remove-outline {
    cursor: pointer;
    color: #f56c6c;
    font-size: 16px;
  }
}
.expr-alert {
  margin-top: 6px;
  white-space: normal;
  word-break: break-all;
}
</style>
