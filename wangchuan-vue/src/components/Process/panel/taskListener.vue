<template>
  <div>
    <el-button size="mini" type="primary" icon="el-icon-plus" @click="addRow">添加任务监听器</el-button>
    <el-table :data="list" size="mini" border style="margin-top: 8px">
      <el-table-column label="事件" width="90">
        <template slot-scope="s">
          <el-select v-model="s.row.event" @change="persist">
            <el-option label="create" value="create" />
            <el-option label="assignment" value="assignment" />
            <el-option label="complete" value="complete" />
            <el-option label="delete" value="delete" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="类型" width="150">
        <template slot-scope="s">
          <el-select v-model="s.row.type" @change="persist">
            <el-option label="类" value="class" />
            <el-option label="表达式" value="expression" />
            <el-option label="委托表达式" value="delegateExpression" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="值">
        <template slot-scope="s">
          <el-input v-model="s.row.value" @change="persist" placeholder="类全名 / ${expr} / ${delegateExpr}" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="60">
        <template slot-scope="s">
          <el-button type="text" icon="el-icon-delete" @click="removeRow(s.$index)" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "TaskListener",
  props: { id: { type: String, required: true } },
  data() {
    return { list: [] };
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
      this.list = (this.getProps().taskListeners || []).map((i) => ({ ...i }));
    },
    persist() {
      modelerStore.updateProperties(this.id, { taskListeners: this.list });
    },
    addRow() {
      this.list.push({ event: "create", type: "class", value: "" });
      this.persist();
    },
    removeRow(idx) {
      this.list.splice(idx, 1);
      this.persist();
    },
  },
};
</script>
