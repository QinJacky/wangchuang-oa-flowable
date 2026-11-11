<template>
  <div class="lf-designer">
    <!-- 未选中任何节点：流程基本信息 -->
    <template v-if="!elementType">
      <div class="card-header"><span>流程基本信息</span></div>
      <el-form label-width="84px" size="small" style="padding: 12px">
        <el-form-item label="流程标识">
          <el-input v-model="processInfo.id" placeholder="模块+业务，如：crm_contract" />
        </el-form-item>
        <el-form-item label="流程名称">
          <el-input v-model="processInfo.name" placeholder="中文名称" />
        </el-form-item>
        <el-form-item label="流程类型">
          <el-select v-model="processInfo.processType" placeholder="请选择流程类型" filterable clearable style="width: 100%" @change="onTypeChange">
            <el-option v-for="t in typeOptions" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-divider content-position="left">绑定表单</el-divider>
      <business-form-info :info="processInfo.formInfo" :form-type="processInfo.formInfo.formType" :dynamic-form="processInfo.formInfo.dynamicForm" />
      <el-divider content-position="left">正文配置</el-divider>
      <main-text :info="processInfo.mainText" />
      <el-divider content-position="left">附件配置</el-divider>
      <attachment-info :info="processInfo.attachment" />
      <el-alert
        type="info"
        :closable="false"
        style="margin-top: 8px"
        title="从左侧拖拽“审批/抄送/条件”节点到画布；鼠标移到连线上点击粉色 + 号可快速插入节点。条件通过连线属性配置（挂在边属性上）。"
      />
    </template>

    <template v-else>
      <div class="card-header"><span>{{ headerTitle }}</span></div>
      <el-collapse v-model="activeNames">
        <el-collapse-item title="基本信息" name="common">
          <common-panel :id="elementId" />
        </el-collapse-item>

        <!-- 审批节点业务面板 -->
        <template v-if="biz === 'approval'">
          <el-collapse-item title="流程参与者" name="task">
            <task-panel :id="elementId" />
          </el-collapse-item>
          <el-collapse-item title="多人关系" name="multi">
            <multi-instance :id="elementId" />
          </el-collapse-item>
          <el-collapse-item title="办理动作" name="actions">
            <approval-actions :id="elementId" />
          </el-collapse-item>
          <el-collapse-item title="字段权限" name="field">
            <field-permission :id="elementId" />
          </el-collapse-item>
          <el-collapse-item title="超时处理" name="timeout">
            <timeout-panel :id="elementId" />
          </el-collapse-item>
        </template>

        <!-- 抄送节点业务面板 -->
        <template v-if="biz === 'copy'">
          <el-collapse-item title="抄送配置" name="copy">
            <copy-panel :id="elementId" />
          </el-collapse-item>
        </template>

        <!-- 出口约束：所有节点通用 -->
        <el-collapse-item title="出口约束" name="outgoing">
          <outgoing-panel :id="elementId" />
        </el-collapse-item>

        <!-- 连线：流转条件 -->
        <el-collapse-item v-if="biz === 'edge'" title="流转条件" name="condition">
          <condition-panel :id="elementId" />
        </el-collapse-item>

        <!-- BPMN 映射预览：所有元素通用 -->
        <el-collapse-item title="BPMN 映射预览" name="mapping">
          <mapping-preview :id="elementId" />
        </el-collapse-item>
      </el-collapse>
    </template>
  </div>
</template>

<script>
import CommonPanel from "./panel/commonPanel";
import TaskPanel from "./panel/taskPanel";
import MultiInstance from "./panel/multiInstance";
import ConditionPanel from "./panel/conditionPanel";
import ApprovalActions from "./panel/approvalActions";
import FieldPermission from "./panel/fieldPermission";
import TimeoutPanel from "./panel/timeoutPanel";
import CopyPanel from "./panel/copyPanel";
import OutgoingPanel from "./panel/outgoingPanel";
import MappingPreview from "./panel/mappingPreview";
import BusinessFormInfo from "./businessFormInfo";
import MainText from "./mainText";
import AttachmentInfo from "./attachmentInfo";
import { listEnable } from "@/api/workflow/template";
import { modelerStore } from "./common/global";

const BIZ_MAP = {
  startEvent: "start",
  userTask: "approval",
  serviceTask: "copy",
  exclusiveGateway: "gateway",
  endEvent: "end",
  polyline: "edge",
};
const TITLE_MAP = {
  start: "发起节点",
  approval: "审批节点",
  copy: "抄送节点",
  gateway: "条件分支",
  end: "结束节点",
  edge: "流转连线",
};

export default {
  name: "Designer",
  components: {
    CommonPanel,
    TaskPanel,
    MultiInstance,
    ConditionPanel,
    ApprovalActions,
    FieldPermission,
    TimeoutPanel,
    CopyPanel,
    OutgoingPanel,
    MappingPreview,
    BusinessFormInfo,
    MainText,
    AttachmentInfo,
  },
  data() {
    return {
      activeNames: ["common", "task", "multi", "actions", "field", "timeout", "copy", "form", "outgoing", "condition", "mapping"],
      elementId: "",
      elementType: "",
      typeOptions: [],
    };
  },
  computed: {
    processInfo() {
      return modelerStore.processInfo;
    },
    biz() {
      return BIZ_MAP[this.elementType] || "";
    },
    headerTitle() {
      return TITLE_MAP[this.biz] || "节点属性";
    },
  },
  watch: {
    "modelerStore.element": {
      handler() {
        this.refresh();
      },
      immediate: true,
    },
  },
  created() {
    this.refresh();
    this.fetchTypes();
  },
  methods: {
    refresh() {
      const el = modelerStore.element;
      if (!el) {
        this.elementId = "";
        this.elementType = "";
        return;
      }
      this.elementId = el.id;
      this.elementType = el.type;
    },
    // 拉取流程类型列表（复用 /template/type 后端）
    fetchTypes() {
      listEnable().then((res) => {
        this.typeOptions = (res && res.data) || [];
      });
    },
    onTypeChange(val) {
      const t = this.typeOptions.find((x) => x.id === val);
      this.processInfo.processTypeName = t ? t.name : "";
    },
  },
};
</script>

<style lang="scss" scoped>
.lf-designer {
  .card-header {
    font-weight: 600;
    padding: 8px 4px;
    border-bottom: 1px solid #eef0f4;
    margin-bottom: 8px;
  }
  .form-tip {
    font-size: 12px;
    color: #909399;
    line-height: 1.4;
  }
}
</style>
