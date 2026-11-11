<template>
  <div>
    <el-empty v-if="!edges.length" description="该节点暂无出向连线" :image-size="50" />
    <div v-else>
      <div v-for="edge in edges" :key="edge.id" class="out-row">
        <div class="out-main">
          <span class="out-target">{{ edge.targetName || "未命名节点" }}</span>
          <el-tag v-if="edge.isDefault" size="mini" type="warning">默认分支</el-tag>
          <el-tag v-else-if="edge.condCount" size="mini" type="success">条件 ×{{ edge.condCount }}</el-tag>
        </div>
        <div class="out-actions">
          <el-button type="text" size="mini" @click="setDefault(edge.id)" v-if="canDefault">设默认</el-button>
          <el-button type="text" size="mini" @click="configCond(edge)">配置条件</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { modelerStore } from "../common/global";

export default {
  name: "OutgoingPanel",
  props: {
    id: { type: String, required: true },
  },
  computed: {
    edges() {
      const lf = modelerStore.lf;
      if (!lf) return [];
      const g = lf.getGraphData() || { nodes: [], edges: [] };
      const nodeName = {};
      (g.nodes || []).forEach((n) => (nodeName[n.id] = (n.properties && n.properties.name) || n.type));
      return (g.edges || [])
        .filter((e) => e.sourceNodeId === this.id)
        .map((e) => ({
          id: e.id,
          targetName: nodeName[e.targetNodeId],
          isDefault: !!(e.properties && e.properties.isDefault),
          condCount: (e.properties && e.properties.conditions && e.properties.conditions.length) || 0,
        }));
    },
    canDefault() {
      const el = modelerStore.getElement(this.id);
      return el && el.type === "exclusiveGateway";
    },
  },
  watch: {
    id: {
      handler() {},
      immediate: true,
    },
  },
  methods: {
    setDefault(edgeId) {
      const lf = modelerStore.lf;
      const g = lf.getGraphData() || { edges: [] };
      (g.edges || [])
        .filter((e) => e.sourceNodeId === this.id)
        .forEach((e) => {
          modelerStore.updateProperties(e.id, { isDefault: e.id === edgeId });
        });
      modelerStore.updateProperties(this.id, { default: edgeId });
    },
    configCond(edge) {
      const el = modelerStore.getElement(edge.id);
      const p = (el && el.properties) || {};
      // 首次配置：自动进入“条件流转”，让表达式编辑器立即出现
      if (!p.isDefault && !(p.conditions && p.conditions.length)) {
        modelerStore.updateProperties(edge.id, {
          conditions: [{ field: "", operator: "eq", value: "" }],
          conditionExpression: "",
          isDefault: false,
        });
      }
      // 重新读取最新图数据后再选中该边（避免拿到更新前的快照）
      modelerStore.setElement(modelerStore.getElement(edge.id));
    },
  },
};
</script>

<style lang="scss" scoped>
.out-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 8px;
  border: 1px solid #eef0f4;
  border-radius: 6px;
  margin-bottom: 6px;
}
.out-main {
  display: flex;
  align-items: center;
  gap: 6px;
}
.out-target {
  font-weight: 600;
  font-size: 13px;
}
</style>
