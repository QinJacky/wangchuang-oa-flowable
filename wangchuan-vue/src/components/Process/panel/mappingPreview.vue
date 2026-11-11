<template>
  <div>
    <el-alert type="info" :closable="false" class="hint" title="以下为当前节点/连线导出 Flowable BPMN 时的映射片段（只读）" />
    <pre class="preview">{{ snippet }}</pre>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";
import { previewElementBpmn } from "../common/flowableBpmn";

export default {
  name: "MappingPreview",
  props: {
    id: { type: String, required: true },
  },
  computed: {
    snippet() {
      const el = modelerStore.getElement(this.id);
      if (!el) return "";
      try {
        return previewElementBpmn(el);
      } catch (e) {
        return "（生成预览失败：" + e.message + "）";
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.hint {
  margin-bottom: 8px;
}
.preview {
  background: #0f172a;
  color: #e2e8f0;
  padding: 10px;
  border-radius: 6px;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 260px;
  overflow: auto;
}
</style>
