# 轻量 OA 工作流系统 - 前端

基于官方 RuoYi-Vue2（Vue 2 + Element UI）改造，集成 Flowable 工作流相关页面。

## 技术栈
- Vue 2.6 + Vuex + Vue Router
- Element UI 2.15
- Axios（统一请求拦截，自动携带 Bearer Token）
- 本地代理：`/dev-api` → 后端 `http://localhost:8080`（见 `vue.config.js`）

## 目录结构
```
src/
├─ api/                # 后端接口封装（login / menu / workflow/*）
├─ assets/             # 样式、图标(svg)、logo
├─ components/         # 公共组件（Pagination/RightToolbar/DictTag/TreeSelect/SvgIcon…）
├─ layout/             # 整体布局（侧边栏/顶栏/标签栏/设置）
├─ router/             # 路由（常量路由 + 后端动态路由）
├─ store/              # Vuex（user/app/settings/tagsView/permission）
├─ utils/              # request/auth/jsencrypt/ruoyi 等工具
├─ views/
│  ├─ login.vue        # 登录
│  ├─ dashboard/       # 首页仪表盘
│  ├─ error/           # 404 / 401
│  ├─ system/user/     # 个人中心
│  └─ workflow/        # 工作流页面
│     ├─ definition/   # 流程定义管理（导入/查看/发起/删除）
│     ├─ start/        # 发起申请
│     ├─ todo/         # 我的待办（办理/审批/流转记录）
│     ├─ done/         # 我的已办
│     ├─ template/     # 表单模板管理
│     └─ formDesigner/ # 动态表单设计器
```

## 运行前置条件
1. 安装 Node.js（建议 14.x / 16.x，与 vue-cli 4.x 兼容）。
2. 后端  已在 `8080` 端口启动，且 MySQL / Redis 已就绪（Flowable 自动建表）。
3. 数据库已执行 。

## 常用命令
```bash
# 安装依赖
npm install

# 本地开发（默认 80 端口，代理到 8080 后端）
npm run dev

# 生产构建
npm run build:prod
```
