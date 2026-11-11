<template>
  <div>
    <el-form label-width="92px" size="small" @submit.native.prevent>
      <el-form-item label="允许通过">
        <el-switch v-model="formData.allowApprove" @change="update" />
      </el-form-item>
      <el-form-item label="允许驳回">
        <el-switch v-model="formData.allowReject" @change="update" />
      </el-form-item>
      <el-form-item label="驳回去向" v-if="formData.allowReject">
        <el-checkbox-group v-model="formData.rejectTargets" @change="update">
          <el-checkbox label="initiator">退回发起人</el-checkbox>
          <el-checkbox label="prev">退回上一步</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="必填意见">
        <el-switch v-model="formData.commentRequired" @change="update" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "ApprovalActions",
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      formData: { allowApprove: true, allowReject: true, rejectTargets: ["initiator"], commentRequired: false },
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
        allowApprove: p.allowApprove !== false,
        allowReject: p.allowReject !== false,
        rejectTargets: p.rejectTargets && p.rejectTargets.length ? p.rejectTargets : ["initiator"],
        commentRequired: !!p.commentRequired,
      };
    },
    update() {
      modelerStore.updateProperties(this.id, {
        allowApprove: this.formData.allowApprove,
        allowReject: this.formData.allowReject,
        rejectTargets: this.formData.rejectTargets,
        commentRequired: this.formData.commentRequired,
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
