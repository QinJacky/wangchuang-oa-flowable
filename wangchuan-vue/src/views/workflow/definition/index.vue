<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="流程名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入流程名称" clearable size="small" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-upload" size="mini" @click="handleImport">导入流程</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="definitionList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流程编号" align="center" prop="id" />
      <el-table-column label="流程名称" align="center" prop="name" />
      <el-table-column label="流程分类" align="center" prop="category" />
      <el-table-column label="版本" align="center" prop="version" />
      <el-table-column label="部署时间" align="center" prop="deploymentTime" width="160" />
      <el-table-column label="挂起状态" align="center" prop="suspensionState">
        <template slot-scope="scope">
          <dict-tag :options="stateOptions" :value="scope.row.suspensionState" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleReadImage(scope.row.deploymentId)">流程图</el-button>
          <el-button size="mini" type="text" icon="el-icon-tickets" @click="handleReadXml(scope.row.deploymentId)">XML</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-play" @click="handleStart(scope.row)">发起</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 导入流程 -->
    <el-dialog title="导入流程" :visible.sync="uploadOpen" width="500px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".bpmn, .xml"
        :headers="headers"
        :action="uploadAction"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
      >
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <el-button style="margin-left: 10px" size="small" type="success" :loading="upload.isUploading" @click="submitFileForm">上传</el-button>
        <div slot="tip" class="el-upload__tip">支持 *.bpmn / *.xml 流程文件</div>
      </el-upload>
    </el-dialog>

    <!-- 流程图 -->
    <el-dialog title="流程图" :visible.sync="imgOpen" width="80%" append-to-body>
      <img :src="imgSrc" alt="流程图" style="width:100%" />
    </el-dialog>

    <!-- XML -->
    <el-dialog title="流程 XML" :visible.sync="xmlOpen" width="80%" append-to-body>
      <pre class="xml-pre">{{ xmlContent }}</pre>
    </el-dialog>
  </div>
</template>

<script>
import { definitionList, readXml, readImage, delDefinition, updateState, optionSelect } from '@/api/workflow/definition'
import { getToken } from '@/utils/auth'

export default {
  name: 'ProcessDefinition',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      definitionList: [],
      queryParams: { pageNum: 1, pageSize: 10, name: '' },
      stateOptions: [
        { label: '已激活', value: '1', listClass: 'success' },
        { label: '已挂起', value: '2', listClass: 'warning' }
      ],
      uploadOpen: false,
      upload: { isUploading: false },
      uploadAction: process.env.VUE_APP_BASE_API + '/flowable/definition/import',
      headers: { Authorization: 'Bearer ' + getToken() },
      imgOpen: false,
      imgSrc: '',
      xmlOpen: false,
      xmlContent: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      definitionList(this.queryParams).then(res => {
        this.definitionList = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    resetForm(ref) {
      if (this.$refs[ref]) this.$refs[ref].resetFields()
    },
    handleImport() {
      this.uploadOpen = true
    },
    handleFileUploadProgress() {
      this.upload.isUploading = true
    },
    handleFileSuccess() {
      this.uploadOpen = false
      this.upload.isUploading = false
      this.getList()
      this.msgSuccess('导入成功')
    },
    submitFileForm() {
      this.$refs.upload.submit()
    },
    handleReadImage(deployId) {
      readImage(deployId).then(res => {
        this.imgSrc = 'data:image/png;base64,' + res
        this.imgOpen = true
      }).catch(() => {})
    },
    handleReadXml(deployId) {
      readXml(deployId).then(res => {
        this.xmlContent = res.data || res
        this.xmlOpen = true
      }).catch(() => {})
    },
    handleStart(row) {
      optionSelect().then(res => {
        const list = res.data || []
        const found = list.find(p => p.id === row.id || p.deploymentId === row.deploymentId)
        this.$router.push({ path: '/workflow/start', query: { procDefId: found ? found.id : row.id } })
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该流程定义吗？', '提示', { type: 'warning' }).then(() => {
        delDefinition(row.key || row.id).then(() => {
          this.msgSuccess('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    msgSuccess(msg) {
      this.$message.success(msg)
    }
  }
}
</script>

<style scoped>
.xml-pre {
  background: #2d2d2d;
  color: #ccc;
  padding: 12px;
  border-radius: 4px;
  max-height: 500px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
