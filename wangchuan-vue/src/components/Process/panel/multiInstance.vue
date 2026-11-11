<template>
  <div>
    <el-form label-width="82px" size="small" @submit.native.prevent>
      <el-form-item label="多人关系">
        <el-radio-group v-model="formData.multiMode" @change="update">
          <el-radio label="all">会签</el-radio>
          <el-radio label="any">或签</el-radio>
        </el-radio-group>
        <div class="tip">{{ formData.multiMode === "any" ? "或签：一人通过即可，不创建多实例" : "会签：需多人全部处理" }}</div>
      </el-form-item>
      <el-form-item label="执行方式" v-if="formData.multiMode === 'all'">
        <el-switch
          v-model="formData.sequential"
          active-text="顺序"
          inactive-text="并行"
          :active-value="true"
          :inactive-value="false"
          @change="update"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "MultiInstance",
  props: {
    id: { type: String, required: true },
  },
  data() {
    return {
      formData: { multiMode: "all", sequential: false },
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
        multiMode: p.multiMode || "all",
        sequential: !!p.sequential,
      };
    },
    update() {
      modelerStore.updateProperties(this.id, {
        multiMode: this.formData.multiMode,
        sequential: this.formData.sequential,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
::v-deep .el-form-item__label {
  font-weight: normal;
}
.tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}
</style>
