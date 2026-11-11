<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
      <el-form-item label="表单名称" prop="formName">
        <el-input v-model="queryParams.formName" placeholder="请输入表单名称" clearable size="small" style="width:200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增表单</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="表单编号" prop="id" width="120" />
      <el-table-column label="表单名称" prop="formName" show-overflow-tooltip />
      <el-table-column label="字段数" width="100" align="center">
        <template slot-scope="scope">{{ (parseSchema(scope.row.formItems).length) }}</template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handlePreview(scope.row)">预览</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">设计</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 设计弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="表单名称" prop="formName">
          <el-input v-model="form.formName" placeholder="请输入表单名称" />
        </el-form-item>
        <el-form-item label="表单配置" prop="formItems">
          <el-input v-model="form.formItems" type="textarea" :rows="12" placeholder='字段 JSON，例如：[{"label":"姓名","field":"name","type":"input","required":true}]' />
          <div class="tip">支持类型：input / textarea / number / select / date</div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="btnLoading" @click="submitForm">保 存</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 预览弹窗 -->
    <el-dialog title="表单预览" :visible.sync="previewOpen" width="640px" append-to-body>
      <el-form label-width="100px" size="small">
        <el-form-item v-for="(f, idx) in previewSchema" :key="idx" :label="f.label" :required="f.required">
          <el-input v-if="f.type === 'input'" v-model="previewData[f.field]" :placeholder="'请输入' + f.label" />
          <el-input v-else-if="f.type === 'textarea'" type="textarea" v-model="previewData[f.field]" />
          <el-input-number v-else-if="f.type === 'number'" v-model="previewData[f.field]" />
          <el-date-picker v-else-if="f.type === 'date'" v-model="previewData[f.field]" type="date" />
          <el-select v-else-if="f.type === 'select'" v-model="previewData[f.field]" placeholder="请选择">
            <el-option v-for="o in (f.options || [])" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
          <el-input v-else v-model="previewData[f.field]" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="previewOpen = false">提交（演示）</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { dynamicFormList, dynamicFormInfo, addDynamicForm, updateDynamicForm, delDynamicForm } from '@/api/workflow/template'

export default {
  name: 'FormDesigner',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      list: [],
      queryParams: { pageNum: 1, pageSize: 10, formName: '' },
      title: '',
      open: false,
      btnLoading: false,
      previewOpen: false,
      previewSchema: [],
      previewData: {},
      form: { id: undefined, formName: '', formItems: '', remark: '' },
      rules: {
        formName: [{ required: true, message: '请输入表单名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseSchema(str) {
      if (!str) return []
      try { return JSON.parse(str) || [] } catch (e) { return [] }
    },
    getList() {
      this.loading = true
      dynamicFormList(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams.formName = ''
      this.handleQuery()
    },
    reset() {
      this.form = { id: undefined, formName: '', formItems: '[]', remark: '' }
    },
    handleAdd() {
      this.reset()
      this.title = '新增动态表单'
      this.open = true
    },
    handleUpdate(row) {
      this.reset()
      dynamicFormInfo(row.id).then(res => {
        const d = res.data || {}
        this.form = { id: d.id, formName: d.formName, formItems: d.formItems || '[]', remark: d.remark }
        this.title = '设计动态表单'
        this.open = true
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.btnLoading = true
        const method = this.form.id ? updateDynamicForm : addDynamicForm
        method(this.form).then(() => {
          this.$message.success('保存成功')
          this.open = false
          this.getList()
        }).finally(() => { this.btnLoading = false })
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该表单吗？', '提示', { type: 'warning' }).then(() => {
        delDynamicForm(row.id).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      }).catch(() => {})
    },
    handlePreview(row) {
      this.previewSchema = this.parseSchema(row.formItems)
      this.previewData = {}
      this.previewOpen = true
    }
  }
}
</script>

<style scoped>
.tip {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}
</style>
