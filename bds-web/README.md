# bds-web（Vue 前端）

**Bot Detect System** 的 Web 界面：用户列表与详情可视分析、登录注册、管理后台等。界面文案以中文为主，顶栏标题为英文产品名。

## 环境要求

- Node.js（与 Vue CLI 5 兼容的版本）
- 后端 API 已启动（默认 `http://localhost:8090/api`）

## 安装与运行

```bash
npm install
npm run serve
```

生产打包：

```bash
npm run build
```

代码检查：

```bash
npm run lint
```

## 配置说明

- **`VUE_APP_API_BASE`**：axios 请求前缀，未设置时为 `http://localhost:8090/api`（见 `src/api/http.js`）。
- **浏览器标题**：在 `vue.config.js` 中通过 `html-webpack-plugin` 设置为「恶意行为检测与可视分析 - Bot Detect System」。
- **Element Plus**：在 `src/main.js` 中注入 `zh-cn` 语言包（分页、表格等组件为中文）。

## 与其它模块的对应关系

- 用户检索：`POST /users/search`（含可选 `keyword`、`verified`、`isMalicious`）。
- 用户详情与画像：`GET /users/{id}`、`wordcloud`、`active-hours`；推文分页：`POST /tweets/search`。
- 关注关系：`GET /relations/{id}/followers`、`followees`。

详见仓库根目录 `README.md`。
