<template>
  <div>
    <el-form label-width="92px" size="small" @submit.native.prevent>
      <el-form-item label="超时时长">
        <el-input v-model="formData.dueDuration" @change="update" placeholder="如 3d（3天）/ 24h（24小时）">
          <template slot="append">后触发</template>
        </el-input>
      </el-form-item>
      <el-form-item label="超时动作">
        <el-select v-model="formData.overdueAction" @change="update" placeholder="不处理" clearable>
          <el-option label="提醒" value="remind" />
          <el-option label="催办" value="urge" />
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "TimeoutPanel",
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      formData: { dueDuration: "", overdueAction: "" },
    };
  },
  watch: {
    id: {
      handler() {
        this.reset();
      },
      immediate: true,
    },
  },
  methods: {
    reset() {
      const el = modelerStore.getElement(this.id);
      const p = (el && el.properties) || {};
      this.formData = {
        dueDuration: p.dueDuration || "",
        overdueAction: p.overdueAction || "",
      };
    },
    update() {
      modelerStore.updateProperties(this.id, {
        dueDuration: this.formData.dueDuration,
        overdueAction: this.formData.overdueAction,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-form-item__label {
  font-weight: normal;
}
</style>
