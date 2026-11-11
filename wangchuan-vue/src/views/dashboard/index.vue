<template>
  <div class="dashboard">
    <!-- 欢迎区 -->
    <el-card class="hero" shadow="never">
      <el-avatar class="hero-avatar" :size="60" :src="avatar || undefined">{{ name.charAt(0).toUpperCase() }}</el-avatar>
      <div class="hero-info">
        <div class="hero-title">{{ greeting }}，{{ name }}<span class="hero-wave">👋</span></div>
        <div class="hero-sub">欢迎使用轻量 OA 工作流系统 · {{ today }}</div>
      </div>
      <div class="hero-tags">
        <el-tag type="info" effect="plain" size="medium">待办 {{ todoTotal }}</el-tag>
        <el-tag type="success" effect="plain" size="medium">已办 {{ doneTotal }}</el-tag>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mt-16">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ background: item.color }">
            <svg-icon :icon-class="item.icon" />
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-title">{{ item.title }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办 + 任务概览 -->
    <el-row :gutter="16" class="mt-16">
      <el-col :span="16">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="card-header">
            <span>我的待办</span>
            <el-button type="text" @click="goPath('/workflow/todo')">查看更多</el-button>
          </div>
          <el-table :data="todoList" size="small" empty-text="暂无待办" :show-header="true">
            <el-table-column prop="title" label="事项" show-overflow-tooltip />
            <el-table-column prop="procDefName" label="流程" width="150" />
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column label="操作" width="90" align="center">
              <template slot-scope="scope">
                <el-button type="text" @click="goPath('/workflow/todo')">办理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="card-header">
            <span>任务概览</span>
          </div>
          <div ref="chart" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快速发起 + 常用入口 -->
    <el-row :gutter="16" class="mt-16">
      <el-col :span="16">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="card-header">
            <span>快速发起</span>
          </div>
          <div class="quick-list">
            <el-button
              v-for="p in processList"
              :key="p.id"
              type="primary"
              plain
              size="small"
              class="quick-item"
              @click="goStart(p)"
            >{{ p.name }}</el-button>
            <div v-if="processList.length === 0" class="empty-tip">暂无可发起流程</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="box-card" shadow="never">
          <div slot="header" class="card-header">
            <span>常用入口</span>
          </div>
          <div class="entry-grid">
            <div
              v-for="e in quickEntries"
              :key="e.name"
              class="entry-item"
              @click="goPath(e.path)"
            >
              <div class="entry-icon" :style="{ background: e.color }">
                <svg-icon :icon-class="e.icon" />
              </div>
              <div class="entry-name">{{ e.name }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { todoList } from '@/api/workflow/todo'
import { doneList } from '@/api/workflow/done'
import { optionSelect } from '@/api/workflow/definition'

export default {
  name: 'Dashboard',
  data() {
    return {
      name: '',
      avatar: '',
      greeting: '',
      today: '',
      todoTotal: 0,
      doneTotal: 0,
      initiated: 0,
      draft: 0,
      todoList: [],
      processList: [],
      chart: null,
      quickEntries: [
        { name: '待办事项', icon: 'todo', color: '#2f54eb', path: '/workflow/todo' },
        { name: '已办事项', icon: 'model', color: '#13c2c2', path: '/workflow/done' },
        { name: '发起流程', icon: 'workflow', color: '#fa8c16', path: '/workflow/start' },
        { name: '流程中心', icon: 'dashboard', color: '#722ed1', path: '/workflow/definition' }
      ]
    }
  },
  computed: {
    stats() {
      return [
        { title: '待办事项', value: this.todoTotal, icon: 'todo', color: '#2f54eb' },
        { title: '已办事项', value: this.doneTotal, icon: 'model', color: '#13c2c2' },
        { title: '我发起的', value: this.initiated, icon: 'workflow', color: '#fa8c16' },
        { title: '草稿箱', value: this.draft, icon: 'form', color: '#f5222d' }
      ]
    }
  },
  created() {
    const user = this.$store.state.user || {}
    this.name = user.name || '用户'
    this.avatar = user.avatar || ''
    this.greeting = this.getGreeting()
    this.today = this.formatDate(new Date())
    this.loadTodo()
    this.loadDone()
    this.loadProcess()
  },
  mounted() {
    this.$nextTick(() => {
      this.chart = echarts.init(this.$refs.chart)
      this.renderChart()
      window.addEventListener('resize', this.resizeChart)
    })
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart)
    if (this.chart) this.chart.dispose()
  },
  methods: {
    getGreeting() {
      const h = new Date().getHours()
      if (h < 6) return '凌晨好'
      if (h < 9) return '早上好'
      if (h < 12) return '上午好'
      if (h < 14) return '中午好'
      if (h < 18) return '下午好'
      return '晚上好'
    },
    formatDate(d) {
      const week = ['日', '一', '二', '三', '四', '五', '六'][d.getDay()]
      const p = n => (n < 10 ? '0' + n : '' + n)
      return `${d.getFullYear()}年${p(d.getMonth() + 1)}月${p(d.getDate())}日 星期${week}`
    },
    loadTodo() {
      todoList({ pageNum: 1, pageSize: 5 }).then(res => {
        this.todoList = res.rows || []
        this.todoTotal = res.total || 0
        this.renderChart()
      }).catch(() => {})
    },
    loadDone() {
      doneList({ pageNum: 1, pageSize: 1 }).then(res => {
        this.doneTotal = res.total || 0
        this.renderChart()
      }).catch(() => {})
    },
    loadProcess() {
      optionSelect().then(res => {
        this.processList = res.data || []
      }).catch(() => {})
    },
    renderChart() {
      if (!this.chart) return
      this.chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0, itemWidth: 10, itemHeight: 10, textStyle: { color: '#909399', fontSize: 12 } },
        series: [{
          type: 'pie',
          radius: ['52%', '72%'],
          center: ['50%', '44%'],
          avoidLabelOverlap: false,
          label: { show: true, formatter: '{b}\n{c}', color: '#606266', fontSize: 12, lineHeight: 18 },
          labelLine: { show: false },
          data: [
            { value: this.todoTotal, name: '待办', itemStyle: { color: '#2f54eb' } },
            { value: this.doneTotal, name: '已办', itemStyle: { color: '#13c2c2' } }
          ]
        }]
      })
    },
    resizeChart() {
      if (this.chart) this.chart.resize()
    },
    goPath(path) {
      this.$router.push(path)
    },
    goStart(p) {
      this.$router.push({ path: '/workflow/start', query: { procDefId: p.id || p.procDefId } })
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  padding: 4px;
}
.mt-16 {
  margin-top: 16px;
}

/* 欢迎区 */
.hero {
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #2f54eb 0%, #4c7ef3 100%);
  color: #fff;
  ::v-deep .el-card__body {
    display: flex;
    align-items: center;
  }
  .hero-avatar {
    background: rgba(255, 255, 255, 0.25);
    color: #fff;
    font-size: 22px;
    flex-shrink: 0;
  }
  .hero-info {
    flex: 1;
    margin-left: 16px;
  }
  .hero-title {
    font-size: 20px;
    font-weight: 600;
    line-height: 1.4;
  }
  .hero-wave {
    margin-left: 6px;
  }
  .hero-sub {
    margin-top: 4px;
    font-size: 13px;
    opacity: 0.85;
  }
  .hero-tags {
    .el-tag {
      background: rgba(255, 255, 255, 0.18);
      border-color: rgba(255, 255, 255, 0.3);
      color: #fff;
      margin-left: 8px;
    }
  }
}

/* 统计卡片 */
.stat-card {
  border-radius: 12px;
  border: 1px solid #ebeef5;
  ::v-deep .el-card__body {
    display: flex;
    align-items: center;
  }
  .stat-icon {
    width: 52px;
    height: 52px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 24px;
    flex-shrink: 0;
  }
  .stat-body {
    margin-left: 16px;
    .stat-value {
      font-size: 26px;
      font-weight: 700;
      color: #303133;
      line-height: 1.2;
    }
    .stat-title {
      margin-top: 2px;
      font-size: 13px;
      color: #909399;
    }
  }
}

/* 通用卡片 */
.box-card {
  border-radius: 12px;
  border: 1px solid #ebeef5;
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    color: #303133;
  }
}

.chart-box {
  height: 240px;
}

.quick-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  min-height: 32px;
  align-items: center;
}
.empty-tip {
  color: #909399;
  font-size: 13px;
}

/* 常用入口 */
.entry-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  padding: 6px 2px;
}
.entry-item {
  width: calc(50% - 7px);
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 10px;
  background: #f7f9fc;
  cursor: pointer;
  transition: all 0.2s;
  &:hover {
    background: #eef3ff;
  }
  .entry-icon {
    width: 38px;
    height: 38px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    flex-shrink: 0;
  }
  .entry-name {
    margin-left: 12px;
    font-size: 14px;
    color: #303133;
  }
}
</style>
