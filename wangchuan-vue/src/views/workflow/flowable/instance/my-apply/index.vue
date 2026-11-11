<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="流程名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入流程名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="applyList" @selection-change="handleSelectionChange">
      <el-table-column label="序号" type="index" align="center" width="55">
        <template slot-scope="scope">
          <span>{{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="业务标题" align="left" prop="title" show-overflow-tooltip />
      <el-table-column label="流程名称" align="left" prop="name" width="200" show-overflow-tooltip />
      <el-table-column label="发起人" align="center" width="120">
        <template slot-scope="scope">{{ scope.row.startUserName || startUserName }}</template>
      </el-table-column>
      <el-table-column label="当前节点" align="left" prop="currentTask" width="120" show-overflow-tooltip />
      <el-table-column label="当前处理人" align="left" prop="assignee" width="100" show-overflow-tooltip />
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag type="success" v-if="!scope.row.ended">审批中</el-tag>
          <el-tag type="info" v-if="scope.row.ended">已结束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-s-operation" @click="showProcess(scope.row)">流程进度</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 流程图 -->
    <el-dialog :title="readImage.title" :visible.sync="readImage.open" width="70%" append-to-body @opened="handleDialogOpened">
      <flow-view ref="flowView" />
    </el-dialog>
  </div>
</template>

<script>
import FlowView from "@/views/workflow/flowable/definition/flow-view";
import { getListHistoryProcess } from "@/api/workflow/flowable/monitor";

export default {
  name: "MyApply",
  components: {
    FlowView,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 我发起的流程列表
      applyList: [],
      // 发起人名称（当前登录人）
      startUserName: "",
      // 查询参数（startUser 由当前登录人过滤，详见 created）
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        startUser: null,
      },
      // 流程图
      readImage: {
        open: false,
        src: "",
      },
      processInstanceId: "",
      deployId: "",
    };
  },
  created() {
    // 仅查询“我发起的”流程：按当前登录人过滤
    this.queryParams.startUser = this.$store.getters.id;
    this.startUserName = this.$store.getters.nickName;
    this.getList();
  },
  methods: {
    /** 查询我发起的流程实例列表 */
    getList() {
      this.loading = true;
      getListHistoryProcess(this.queryParams)
        .then((response) => {
          this.applyList = response.rows;
          this.total = response.total;
          this.loading = false;
          if (this.$refs.flowView) {
            this.$refs.flowView.resetFlowData();
          }
        })
        .catch(() => {
          this.loading = false;
        });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleDialogOpened() {
      this.$nextTick(() => {
        this.$refs.flowView.getFlowXmlAndNode(this.processInstanceId, this.deployId);
      });
    },
    // 查看流程图
    showProcess(row) {
      this.processInstanceId = row.processInstanceId;
      this.deployId = row.deployId;
      this.readImage.title = "流程图";
      this.readImage.open = true;
    },
  },
};
</script>
