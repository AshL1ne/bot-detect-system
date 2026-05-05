# Bot Detect System（恶意行为检测与可视分析）

基于 **Spring Boot** 后端与 **Vue 3** 前端的 B/S 系统：管理爬虫入库的微博用户与关注关系，展示用户画像（词云、活跃时段、博文列表），支持登录角色区分与管理员维护标签。

## 仓库结构

| 目录 | 说明 |
|------|------|
| `bds/` | Spring Boot 3、MyBatis-Plus、JWT、MySQL |
| `bds-web/` | Vue 3、Vue Router、Element Plus、ECharts |

## 后端快速启动

1. 创建数据库并导入业务表（`users`、`relations`、`tweets`、`auth_users` 等）。
2. 编辑 `bds/src/main/resources/application.properties`：数据源、`jwt.secret`（务必改为强随机字符串）。
3. 在项目根目录或 `bds` 下执行：

```bash
cd bds
mvn spring-boot:run
```

默认端口见配置（示例为 `8090`）。

## 前端快速启动

```bash
cd bds-web
npm install
npm run serve
```

通过环境变量 `VUE_APP_API_BASE` 指向后端 API 根路径（默认 `http://localhost:8090/api`）。生产构建：`npm run build`。

## 功能摘要

- **认证**：用户名 + 密码（BCrypt + Salt），JWT；角色 `USER` / `ADMIN`。禁用账号登录返回 **403**，前端提示「账号已被禁用」。
- **用户列表**：关键词搜索（需点击搜索）、**是否认证 / 是否恶意** 筛选（变更即刷新）；详情含词云、按时段活跃图、推文分页。
- **关系**：粉丝 / 关注列表接口与页面；顶部导航 **← 返回**；用户列表页 `keep-alive` 保留分页与筛选状态。
- **管理后台**：系统登录账号（角色、启用状态；不可禁用本人）、微博用户 **是否恶意** 与恶意概率维护。
- **国际化**：界面文案以中文为主；顶栏产品名保留英文 **Bot Detect System**；Element Plus 使用 `zh-cn` 语言包。

## 备注

- `bds-web` 对 ECharts 5.6 使用 `patch-package` 规避部分 ResizeObserver 告警，安装依赖后会自动打补丁。
- 爬虫与 GNN 训练在仓库外完成时，只需保证数据库字段与后端实体一致即可。
