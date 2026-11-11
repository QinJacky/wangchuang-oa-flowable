<template>
  <div>
    <el-form label-width="82px" size="small" @submit.native.prevent>
      <el-form-item label="抄送对象">
        <el-radio-group v-model="formData.copyType" @change="onTypeChange">
          <el-radio label="fixedUser">固定人员</el-radio>
          <el-radio label="role">角色</el-radio>
          <el-radio label="dept">部门</el-radio>
          <el-radio label="formUserField">表单人员字段</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="对象值">
        <user-select
          v-if="formData.copyType === 'fixedUser'"
          :checkedUsers="fixedUsers"
          :multiple="true"
          placeholder="请选择抄送人"
          @input="onUserSelect"
        />
        <role-select
          v-else-if="formData.copyType === 'role'"
          :selectedRoles="roleUsers"
          :multiple="true"
          placeholder="请选择角色"
          @input="onRoleSelect"
        />
        <el-input
          v-else
          v-model="formData.copyValue"
          @change="update"
          :placeholder="formData.copyType === 'dept' ? '部门ID，逗号分隔' : '表单字段key，如 applyUser'"
        />
      </el-form-item>
      <el-form-item label="通知方式">
        <el-checkbox-group v-model="formData.notifyChannels" @change="update">
          <el-checkbox label="site">站内</el-checkbox>
          <el-checkbox label="email">邮件</el-checkbox>
          <el-checkbox label="mobile">短信</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";
import UserSelect from "./userSelect.vue";
import RoleSelect from "./roleSelect.vue";

export default {
  name: "CopyPanel",
  components: { UserSelect, RoleSelect },
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      formData: { copyType: "fixedUser", copyValue: "", copyLabel: "", notifyChannels: ["site"] },
    };
  },
  computed: {
    fixedUsers() {
      return this.buildPairs(this.formData.copyValue, this.formData.copyLabel).map((p) => ({
        userId: p.id,
        nickName: p.name,
      }));
    },
    roleUsers() {
      return this.buildPairs(this.formData.copyValue, this.formData.copyLabel).map((p) => ({
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
        copyType: p.copyType || "fixedUser",
        copyValue: p.copyValue || "",
        copyLabel: p.copyLabel || "",
        notifyChannels: p.notifyChannels && p.notifyChannels.length ? p.notifyChannels : ["site"],
      };
    },
    buildPairs(ids, names) {
      const idArr = (ids || "").split(",").filter(Boolean);
      const nameArr = (names || "").split(",").filter(Boolean);
      return idArr.map((id, i) => ({ id, name: nameArr[i] || id }));
    },
    onTypeChange() {
      this.formData.copyValue = "";
      this.formData.copyLabel = "";
      this.update();
    },
    onUserSelect(selection) {
      const list = selection || [];
      this.formData.copyValue = list.map((u) => u.userId).join(",");
      this.formData.copyLabel = list.map((u) => u.nickName).join(",");
      this.update();
    },
    onRoleSelect(selection) {
      const list = selection || [];
      this.formData.copyValue = list.map((r) => r.roleId).join(",");
      this.formData.copyLabel = list.map((r) => r.roleName).join(",");
      this.update();
    },
    update() {
      modelerStore.updateProperties(this.id, {
        copyType: this.formData.copyType,
        copyValue: this.formData.copyValue,
        copyLabel: this.formData.copyLabel,
        notifyChannels: this.formData.notifyChannels,
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
