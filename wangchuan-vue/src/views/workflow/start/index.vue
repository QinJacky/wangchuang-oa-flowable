<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="card-header">
            <span>可发起流程</span>
          </div>
          <el-menu :default-active="activeProc" @select="onSelectProc">
            <el-menu-item v-for="item in processList" :key="item.id" :index="item.id">
              <i class="el-icon-files"></i>
              <span slot="title">{{ item.name }}</span>
            </el-menu-item>
            <div v-if="processList.length === 0" class="empty-tip">暂无可用流程</div>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="card-header">
            <span>发起申请 - {{ currentProcName }}</span>
          </div>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px" v-if="activeProc">
            <el-form-item label="流程标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入流程标题" maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item label="申请人" prop="applyUser">
              <el-input v-model="form.applyUser" disabled :placeholder="userName" />
            </el-form-item>
            <el-form-item label="申请内容" prop="content">
              <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入申请内容" />
            </el-form-item>
            <el-form-item label="紧急程度" prop="urgent">
              <el-radio-group v-model="form.urgent">
                <el-radio label="0">普通</el-radio>
                <el-radio label="1">紧急</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="submitForm">提交申请</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
          <div v-else class="empty-tip">请选择左侧流程后发起申请</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { optionSelect } from '@/api/workflow/definition'
import { startFlow } from '@/api/workflow/handle'
import { mapGetters } from 'vuex'

export default {
  name: 'WorkflowStart',
  data() {
    return {
      processList: [],
      activeProc: '',
      currentProcName: '',
      submitting: false,
      form: {
        title: '',
        applyUser: '',
        content: '',
        urgent: '0'
      },
      rules: {
        title: [{ required: true, message: '请输入流程标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入申请内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapGetters(['name'])
  },
  created() {
    this.form.applyUser = this.name
    this.loadProcess()
    if (this.$route.query.procDefId) {
      this.activeProc = this.$route.query.procDefId
    }
  },
  methods: {
    loadProcess() {
      optionSelect().then(res => {
        this.processList = res.data || []
        if (!this.activeProc && this.processList.length > 0) {
          this.activeProc = this.processList[0].id
          this.currentProcName = this.processList[0].name
        }
      }).catch(() => {})
    },
    onSelectProc(id) {
      const item = this.processList.find(p => p.id === id)
      this.activeProc = id
      this.currentProcName = item ? item.name : ''
    },
    resetForm() {
      this.form.title = ''
      this.form.content = ''
      this.form.urgent = '0'
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitting = true
        const variables = {
          title: this.form.title,
          content: this.form.content,
          urgent: this.form.urgent,
          applyUser: this.name
        }
        const data = { procDefId: this.activeProc, variables: variables }
        startFlow(data).then(() => {
          this.$message.success('发起成功，已进入审批流程')
          this.submitting = false
          this.resetForm()
        }).catch(() => {
          this.submitting = false
        })
      })
    }
  }
}
</script>

<style scoped>
.card-header {
  font-weight: 600;
}
.empty-tip {
  color: #909399;
  font-size: 13px;
  padding: 20px 0;
  text-align: center;
}
</style>
