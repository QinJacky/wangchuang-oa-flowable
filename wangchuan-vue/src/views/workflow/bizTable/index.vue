<template>
  <div class="app-container">
    <el-row :gutter="15">
      <!-- 左：业务表列表 -->
      <el-col :span="7">
        <el-card shadow="never" style="height: calc(100vh - 130px)">
          <div slot="header" class="clearfix">
            <span>业务表</span>
            <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-plus" @click="openAdd">新建业务表</el-button>
          </div>
          <el-table
            :data="tables"
            highlight-current-row
            @current-change="onSelectTable"
            height="calc(100% - 20px)"
            border
          >
            <el-table-column prop="tableName" label="表名" show-overflow-tooltip />
            <el-table-column prop="comment" label="说明" show-overflow-tooltip />
            <el-table-column label="字段数" width="64" align="center">
              <template slot-scope="s">{{ s.row.columnCount || 0 }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右：字段编辑器 -->
      <el-col :span="17">
        <el-card v-if="current" shadow="never" style="height: calc(100vh - 130px); overflow: auto">
          <div slot="header" class="clearfix">
            <span>
              <b style="font-family: monospace">{{ current.tableName }}</b>
              <small style="color: #909399; margin-left: 8px">{{ current.comment }}</small>
            </span>
            <span style="float: right">
              <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="delTable">删除表</el-button>
              <el-button size="mini" type="primary" icon="el-icon-check" @click="saveTable">保存元数据</el-button>
            </span>
          </div>

          <el-alert type="warning" :closable="false" show-icon style="margin-bottom: 12px">
            此处维护字段（类型 / 控件 / 选项），表单设计器将读取用于字段下拉绑定。<b>保存时会真正在数据库中创建/变更物理表（CREATE/ALTER TABLE）</b>，请确认无误后再保存。可先点「生成DDL预览」查看将要执行的语句。
          </el-alert>

          <el-button size="mini" icon="el-icon-plus" @click="addRow">添加字段</el-button>
          <el-button size="mini" icon="el-icon-document" @click="genDdl">生成DDL预览</el-button>

          <el-table :data="current.columns" border size="mini" style="margin-top: 10px">
            <el-table-column label="列名" width="150">
              <template slot-scope="s"><el-input v-model="s.row.columnName" /></template>
            </el-table-column>
            <el-table-column label="中文名" width="130">
              <template slot-scope="s"><el-input v-model="s.row.comment" /></template>
            </el-table-column>
            <el-table-column label="类型" width="130">
              <template slot-scope="s">
                <el-select v-model="s.row.dbType" @change="onDbTypeChange(s.row)">
                  <el-option v-for="t in dbTypes" :key="t" :label="t" :value="t" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="长度/精度" width="110">
              <template slot-scope="s"><el-input v-model="s.row.length" placeholder="50 / 18,2" /></template>
            </el-table-column>
            <el-table-column label="控件" width="130">
              <template slot-scope="s">
                <el-select v-model="s.row.formType">
                  <el-option v-for="f in formTypes" :key="f.v" :label="f.l" :value="f.v" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="选项(逗号分隔)" width="170">
              <template slot-scope="s">
                <el-input v-model="s.row.columnOptions" :disabled="!isOpt(s.row.formType)" placeholder="下拉/单选/多选填写" />
              </template>
            </el-table-column>
            <el-table-column label="可空" width="56" align="center">
              <template slot-scope="s"><el-checkbox v-model="s.row.isNullable" true-label="1" false-label="0" /></template>
            </el-table-column>
            <el-table-column label="索引" width="56" align="center">
              <template slot-scope="s"><el-checkbox v-model="s.row.isIndex" true-label="1" false-label="0" /></template>
            </el-table-column>
            <el-table-column label="主键" width="56" align="center">
              <template slot-scope="s"><el-checkbox v-model="s.row.isPk" true-label="1" false-label="0" @change="onPk(s.row)" /></template>
            </el-table-column>
            <el-table-column label="操作" width="60" align="center">
              <template slot-scope="s"><el-button type="text" icon="el-icon-delete" @click="delRow(s.$index)" /></template>
            </el-table-column>
          </el-table>
        </el-card>
        <el-card v-else shadow="never">
          <el-empty description="请选择左侧业务表，或点击「新建业务表」" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 新建业务表 -->
    <el-dialog title="新建业务表" :visible.sync="addVisible" width="420px">
      <el-form :model="newForm" label-width="90px">
        <el-form-item label="表名">
          <el-input v-model="newForm.tableName" placeholder="建议 oa_ 前缀，如 oa_meeting" />
        </el-form-item>
        <el-form-item label="中文说明">
          <el-input v-model="newForm.comment" placeholder="如：会议申请" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="createTable">创建</el-button>
      </div>
    </el-dialog>

    <!-- DDL 预览 -->
    <el-dialog title="物理表 DDL 预览（未执行）" :visible.sync="ddlVisible" width="640px">
      <pre style="background:#f6f8fa;padding:12px;border-radius:6px;overflow:auto;max-height:420px">{{ ddlText }}</pre>
    </el-dialog>
  </div>
</template>

<script>
import { listBizTable, getBizTable, addBizTable, updateBizTable, delBizTable, generateDdl } from '@/api/workflow/bizTable'

const DB_TYPES = ['VARCHAR', 'BIGINT', 'INT', 'DECIMAL', 'DATETIME', 'DATE', 'TEXT']
const FORM_TYPES = [
  { v: 'input', l: '单行文本' }, { v: 'textarea', l: '多行文本' }, { v: 'number', l: '数字' },
  { v: 'money', l: '金额' }, { v: 'date', l: '日期' }, { v: 'select', l: '下拉' },
  { v: 'radio', l: '单选' }, { v: 'checkbox', l: '多选' }, { v: 'switch', l: '开关' },
  { v: 'sys_user', l: '人员' }, { v: 'sys_dept', l: '部门' }, { v: 'image', l: '图片' },
  { v: 'upload', l: '附件' }, { v: 'subform', l: '子表单' }
]

export default {
  name: 'BizTable',
  data() {
    return {
      tables: [],
      current: null,
      dbTypes: DB_TYPES,
      formTypes: FORM_TYPES,
      addVisible: false,
      newForm: { tableName: '', comment: '' },
      ddlVisible: false,
      ddlText: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      listBizTable({}).then(res => { this.tables = res.rows || [] })
    },
    onSelectTable(row) {
      if (!row) return
      getBizTable(row.id).then(res => { this.current = res.data })
    },
    openAdd() {
      this.newForm = { tableName: '', comment: '' }
      this.addVisible = true
    },
    createTable() {
      const name = (this.newForm.tableName || '').trim()
      if (!name) { this.$message.warning('请输入表名'); return }
      if (!/^[a-zA-Z_][a-zA-Z0-9_]*$/.test(name)) { this.$message.warning('表名需以字母/下划线开头'); return }
      const data = {
        tableName: name,
        comment: this.newForm.comment || name,
        columns: [{ columnName: 'id', comment: '主键', dbType: 'BIGINT', length: '', formType: 'input', columnOptions: '', isPk: '1', isIndex: '1', isNullable: '0' }]
      }
      addBizTable(data).then(() => {
        this.$message.success('已创建')
        this.addVisible = false
        this.getList()
      })
    },
    saveTable() {
      if (!this.current) return
      updateBizTable(this.current).then(() => { this.$message.success('已保存元数据'); this.getList() })
    },
    delTable() {
      if (!this.current) return
      this.$confirm('确认删除业务表「' + this.current.tableName + '」？将同步删除其字段', '提示', { type: 'warning' }).then(() => {
        delBizTable(this.current.id).then(() => { this.$message.success('已删除'); this.current = null; this.getList() })
      })
    },
    addRow() {
      if (!this.current.columns) this.$set(this.current, 'columns', [])
      this.current.columns.push({ columnName: 'new_field', comment: '新字段', dbType: 'VARCHAR', length: '50', formType: 'input', columnOptions: '', isPk: '0', isIndex: '0', isNullable: '1' })
    },
    delRow(i) {
      this.current.columns.splice(i, 1)
    },
    onDbTypeChange(row) {
      // 类型联动控件建议
      if (row.dbType === 'BIGINT') {
        const s = (row.columnName + row.comment).toLowerCase()
        row.formType = s.includes('user') || s.includes('人') ? 'sys_user' : (s.includes('dept') || s.includes('部门') ? 'sys_dept' : 'number')
      } else if (row.dbType === 'DECIMAL') row.formType = 'money'
      else if (row.dbType === 'DATETIME' || row.dbType === 'DATE') row.formType = 'date'
      else if (row.dbType === 'TEXT') row.formType = 'textarea'
      else if (row.dbType === 'INT') row.formType = 'number'
      else row.formType = 'input'
    },
    onPk(row) {
      if (row.isPk === '1') row.isNullable = '0'
    },
    isOpt(t) { return ['select', 'radio', 'checkbox'].indexOf(t) > -1 },
    genDdl() {
      if (!this.current) return
      generateDdl(this.current.id).then(res => { this.ddlText = res.data; this.ddlVisible = true })
    }
  }
}
</script>
