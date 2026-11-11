<template>
  <div>
    <el-form label-width="0" size="small" @submit.native.prevent>
      <div v-for="(row, idx) in rows" :key="idx" class="fp-row">
        <el-input v-model="row.field" placeholder="字段名" size="small" style="width: 130px" />
        <el-select v-model="row.permission" size="small" style="width: 110px">
          <el-option label="可编辑" value="edit" />
          <el-option label="只读" value="readonly" />
          <el-option label="隐藏" value="hidden" />
        </el-select>
        <i class="el-icon-remove-outline" @click="removeRow(idx)" />
      </div>
      <el-button type="text" icon="el-icon-plus" size="mini" @click="addRow">添加字段权限</el-button>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "FieldPermission",
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      rows: [],
    };
  },
  watch: {
    id: {
      handler() {
        this.reset();
      },
      immediate: true,
    },
    rows: {
      deep: true,
      handler() {
        this.sync();
      },
    },
  },
  methods: {
    reset() {
      const el = modelerStore.getElement(this.id);
      const p = (el && el.properties) || {};
      const obj = p.fieldPermissions || {};
      this.rows = Object.keys(obj).map((field) => ({ field, permission: obj[field] }));
    },
    addRow() {
      this.rows.push({ field: "", permission: "edit" });
      this.sync();
    },
    removeRow(idx) {
      this.rows.splice(idx, 1);
      this.sync();
    },
    sync() {
      const obj = {};
      this.rows.forEach((r) => {
        if (r.field) obj[r.field] = r.permission;
      });
      modelerStore.updateProperties(this.id, { fieldPermissions: obj });
    },
  },
};
</script>

<style lang="scss" scoped>
.fp-row {
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
</style>
