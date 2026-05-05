<template>
  <section class="page">
    <h1>管理后台</h1>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="系统登录账号" name="auth">
        <div class="toolbar">
          <el-input
            v-model="authKeyword"
            placeholder="登录名（模糊）"
            class="search-input"
            clearable
            @keyup.enter="fetchAuthUsers"
          />
          <el-button type="primary" @click="fetchAuthUsers">搜索</el-button>
        </div>
        <el-table :data="authRecords" v-loading="authLoading" border>
          <el-table-column prop="username" label="用户名" min-width="140" />
          <el-table-column prop="id" label="账户 ID" min-width="220" show-overflow-tooltip />
          <el-table-column label="角色" width="160">
            <template #default="scope">
              <el-select
                v-model="scope.row.role"
                size="small"
                style="width: 120px"
                @change="onAuthRoleChange(scope.row)"
              >
                <el-option label="普通用户" value="USER" />
                <el-option label="管理员" value="ADMIN" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="scope">
              <el-switch
                :model-value="scope.row.status === 1"
                :disabled="scope.row.id === currentOperatorId"
                active-text="启用"
                inactive-text="禁用"
                @change="(v) => onAuthStatusChange(scope.row, v)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" min-width="170" />
          <el-table-column prop="updatedAt" label="更新时间" min-width="170" />
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :total="authTotal"
            :page-size="authPageSize"
            :current-page="authPageNum"
            :page-sizes="[10, 20, 50]"
            @current-change="(p) => { authPageNum = p; fetchAuthUsers() }"
            @size-change="(s) => { authPageSize = s; authPageNum = 1; fetchAuthUsers() }"
          />
        </div>
        <p v-if="authError" class="error">{{ authError }}</p>
      </el-tab-pane>

      <el-tab-pane label="微博用户标签" name="weibo">
        <div class="toolbar">
          <el-input
            v-model="wbKeyword"
            placeholder="用户 ID / 昵称"
            class="search-input"
            clearable
            @keyup.enter="fetchWeiboUsers"
          />
          <el-button type="primary" @click="fetchWeiboUsers">搜索</el-button>
        </div>
        <el-table :data="wbRecords" v-loading="wbLoading" border>
          <el-table-column prop="userId" label="用户 ID" min-width="160" />
          <el-table-column prop="nickName" label="昵称" min-width="120" />
          <el-table-column label="是否恶意" width="120">
            <template #default="scope">
              <el-switch
                :model-value="!!scope.row.isMalicious"
                @change="(v) => { scope.row.isMalicious = v }"
              />
            </template>
          </el-table-column>
          <el-table-column label="恶意概率" width="160">
            <template #default="scope">
              <el-input-number
                v-model="scope.row._malProbEdit"
                :min="0"
                :max="1"
                :step="0.05"
                :precision="4"
                size="small"
                controls-position="right"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button type="primary" link size="small" @click="saveWeiboLabel(scope.row)">
                保存
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            layout="total, sizes, prev, pager, next"
            :total="wbTotal"
            :page-size="wbPageSize"
            :current-page="wbPageNum"
            :page-sizes="[10, 20, 50]"
            @current-change="(p) => { wbPageNum = p; fetchWeiboUsers() }"
            @size-change="(s) => { wbPageSize = s; wbPageNum = 1; fetchWeiboUsers() }"
          />
        </div>
        <p v-if="wbError" class="error">{{ wbError }}</p>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>

<script>
import {
  searchAuthUsers,
  updateAuthUserRole,
  updateAuthUserStatus,
  updateUserLabel
} from '../api/admin'
import { searchUsers } from '../api/users'
import { me } from '../api/auth'

export default {
  name: 'AdminUsers',
  data() {
    return {
      currentOperatorId: '',
      activeTab: 'auth',
      authKeyword: '',
      authRecords: [],
      authTotal: 0,
      authPageNum: 1,
      authPageSize: 10,
      authLoading: false,
      authError: '',
      wbKeyword: '',
      wbRecords: [],
      wbTotal: 0,
      wbPageNum: 1,
      wbPageSize: 10,
      wbLoading: false,
      wbError: ''
    }
  },
  mounted() {
    this.loadCurrentOperator()
    this.fetchAuthUsers()
    this.fetchWeiboUsers()
  },
  methods: {
    async loadCurrentOperator() {
      try {
        const res = await me()
        this.currentOperatorId = res.data.data?.id || ''
      } catch {
        this.currentOperatorId = ''
      }
    },
    async fetchAuthUsers() {
      this.authLoading = true
      this.authError = ''
      try {
        const res = await searchAuthUsers({
          pageNum: this.authPageNum,
          pageSize: this.authPageSize,
          keyword: this.authKeyword || undefined
        })
        const data = res.data.data || {}
        this.authRecords = data.records || []
        this.authTotal = data.total || 0
      } catch (e) {
        this.authError = '加载系统账号失败'
        this.authRecords = []
        this.authTotal = 0
      } finally {
        this.authLoading = false
      }
    },
    async fetchWeiboUsers() {
      this.wbLoading = true
      this.wbError = ''
      try {
        const res = await searchUsers({
          pageNum: this.wbPageNum,
          pageSize: this.wbPageSize,
          keyword: this.wbKeyword || undefined
        })
        const data = res.data.data || {}
        const rows = data.records || []
        this.wbRecords = rows.map((r) => ({
          ...r,
          _malProbEdit: r.malProb != null ? Number(r.malProb) : null
        }))
        this.wbTotal = data.total || 0
      } catch (e) {
        this.wbError = '加载微博用户失败'
        this.wbRecords = []
        this.wbTotal = 0
      } finally {
        this.wbLoading = false
      }
    },
    async onAuthRoleChange(row) {
      try {
        await updateAuthUserRole(row.id, { role: row.role })
        this.$message.success('角色已更新')
      } catch (e) {
        this.$message.error('更新角色失败')
        this.fetchAuthUsers()
      }
    },
    async onAuthStatusChange(row, enabled) {
      const status = enabled ? 1 : 0
      try {
        await updateAuthUserStatus(row.id, { status })
        row.status = status
        this.$message.success('状态已更新')
      } catch (e) {
        const msg = e.response?.data?.message
        this.$message.error(msg || '更新状态失败')
        this.fetchAuthUsers()
      }
    },
    async saveWeiboLabel(row) {
      const malProb = row._malProbEdit
      try {
        await updateUserLabel(row.userId, {
          isMalicious: !!row.isMalicious,
          malProb: malProb === null || malProb === undefined || malProb === '' ? null : Number(malProb)
        })
        this.$message.success('标签已保存')
      } catch (e) {
        this.$message.error('保存失败')
      }
    }
  }
}
</script>

<style scoped>
.page {
  padding: 24px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin: 12px 0 16px;
  align-items: center;
}

.search-input {
  max-width: 320px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.error {
  margin-top: 12px;
  color: #d14343;
}
</style>
