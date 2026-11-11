<template>
  <div>
    <el-form label-width="82px" size="small" @submit.native.prevent>
      <el-form-item label="处理人类型">
        <el-radio-group v-model="formData.assigneeType" @change="onTypeChange">
          <el-radio label="fixedUser">固定人员</el-radio>
          <el-radio label="role">角色</el-radio>
          <el-radio label="deptRole">部门角色</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="处理人">
        <user-select
          v-if="formData.assigneeType === 'fixedUser'"
          ref="users"
          :checkedUsers="fixedUsers"
          :multiple="true"
          placeholder="请选择审批人"
          @input="onUserSelect"
        />
        <role-select
          v-else
          ref="roles"
          :selectedRoles="roleUsers"
          :multiple="true"
          placeholder="请选择角色"
          @input="onRoleSelect"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";
import UserSelect from "./userSelect.vue";
import RoleSelect from "./roleSelect.vue";

export default {
  name: "TaskPanel",
  components: { UserSelect, RoleSelect },
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      formData: { assigneeType: "fixedUser", assigneeValue: "", assigneeLabel: "" },
    };
  },
  computed: {
    fixedUsers() {
      return this.buildPairs(this.formData.assigneeValue, this.formData.assigneeLabel).map((p) => ({
        userId: p.id,
        nickName: p.name,
      }));
    },
    roleUsers() {
      return this.buildPairs(this.formData.assigneeValue, this.formData.assigneeLabel).map((p) => ({
        roleId: p.id,
        roleName: p.name,
      }));
    },
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
        assigneeType: p.assigneeType || "fixedUser",
        assigneeValue: p.assigneeValue || "",
        assigneeLabel: p.assigneeLabel || "",
      };
    },
    buildPairs(ids, names) {
      const idArr = (ids || "").split(",").filter(Boolean);
      const nameArr = (names || "").split(",").filter(Boolean);
      return idArr.map((id, i) => ({ id, name: nameArr[i] || id }));
    },
    onTypeChange() {
      this.formData.assigneeValue = "";
      this.formData.assigneeLabel = "";
      this.update();
    },
    onUserSelect(selection) {
      const list = selection || [];
      this.formData.assigneeValue = list.map((u) => u.userId).join(",");
      this.formData.assigneeLabel = list.map((u) => u.nickName).join(",");
      this.update();
    },
    onRoleSelect(selection) {
      const list = selection || [];
      this.formData.assigneeValue = list.map((r) => r.roleId).join(",");
      this.formData.assigneeLabel = list.map((r) => r.roleName).join(",");
      this.update();
    },
    update() {
      modelerStore.updateProperties(this.id, {
        assigneeType: this.formData.assigneeType,
        assigneeValue: this.formData.assigneeValue,
        assigneeLabel: this.formData.assigneeLabel,
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
