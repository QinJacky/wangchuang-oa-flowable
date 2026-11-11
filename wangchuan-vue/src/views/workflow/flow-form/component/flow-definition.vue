<template>
  <!-- 流程定义 -->
  <div class="containers main-box" v-loading="loading" element-loading-text="正在加载中..." element-loading-spinner="el-icon-loading">
    <el-button size="small" icon="el-icon-zoom-in" plain @click="zoomViewport(true)">放大</el-button>
    <el-button size="small" icon="el-icon-zoom-out" plain @click="zoomViewport(false)">缩小</el-button>
    <el-button size="small" icon="el-icon-rank" plain @click="fitViewport">适中</el-button>
    <div class="canvas" ref="flowCanvas"></div>
  </div>
</template>
<script>
import LogicFlow from "@logicflow/core";
import "@logicflow/core/dist/style/index.css";
import { registerFlowNodes } from "@/components/Process/customPanel/lf-nodes";
import { bpmnToGraph } from "@/components/Process/common/flowableBpmn";
import { flowXmlAndNode } from "@/api/workflow/task";
export default {
  name: "FlowDefView",
  data() {
    return {
      loading: true,
      flowData: {},
      lf: null,
    };
  },
  methods: {
    /** 获取流程定义 */
    async getFlowXmlAndNode(procInsId, deployId, businessId) {
      if (Object.keys(this.flowData).length != 0) return;
      flowXmlAndNode({
        procInsId: procInsId,
        deployId: deployId,
        businessId: businessId,
      })
        .then((res) => {
          this.flowData = res.data || {};
          const xmlData = res.data && res.data.xmlData;
          if (!xmlData) {
            this.$modal.msg("暂无流程图");
            this.loading = false;
            return;
          }
          if (!this.lf) {
            this.lf = new LogicFlow({
              container: this.$refs.flowCanvas,
              grid: { size: 12, type: "mesh", config: { color: "#e6e9f0" } },
              keyboard: { enabled: false },
            });
            registerFlowNodes(this.lf);
          }
          const { graph } = bpmnToGraph(xmlData, res.data.nodeData);
          this.lf.render(graph);
          this.$nextTick(() => this.fitViewport());
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 让图能自适应屏幕
    fitViewport() {
      if (!this.lf) return;
      const data = this.lf.getGraphData();
      const single = !data.nodes || data.nodes.length <= 1;
      if (single) {
        this.lf.zoom(1);
        if (typeof this.lf.translateCenter === "function") this.lf.translateCenter();
        return;
      }
      this.lf.fitView(20);
      const s = this.lf.graphModel.transformModel.SCALE_X;
      if (s > 1) this.lf.zoom(1);
    },
    // 放大缩小
    zoomViewport(zoomIn = true) {
      if (this.lf) this.lf.zoom(zoomIn);
    },
  },
};
</script>
<style lang="scss">
.containers {
  width: 100%;
  height: 100%;
  .canvas {
    width: 100%;
    height: calc(100vh - 220px);
    min-height: 480px;
    background: radial-gradient(circle, #d8e0f0 1px, transparent 1px) 0 0 / 22px 22px, #fbfcfe;
  }
  .lf-node-bg {
    cursor: default;
  }
}
</style>
