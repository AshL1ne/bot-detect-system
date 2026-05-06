<template>
  <section class="page">
    <h1>管理后台</h1>

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
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="scope">
          <el-button
            v-if="canDeleteAuthRow(scope.row)"
            type="danger"
            link
            size="small"
            :loading="deletingAuthId === scope.row.id"
            @click="confirmDeleteAuthUser(scope.row)"
          >
            删除
          </el-button>
          <span v-else class="op-muted">—</span>
        </template>
      </el-table-column>
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
  </section>
</template>

<script>
import { ElMessageBox } from 'element-plus'
import { deleteAuthUser, searchAuthUsers, updateAuthUserRole, updateAuthUserStatus } from '../api/admin'
import { me } from '../api/auth'

export default {
  name: 'AdminUsers',
  data() {
    return {
      deletingAuthId: '',
      currentOperatorId: '',
      authKeyword: '',
      authRecords: [],
      authTotal: 0,
      authPageNum: 1,
      authPageSize: 10,
      authLoading: false,
      authError: ''
    }
  },
  mounted() {
    this.loadCurrentOperator()
    this.fetchAuthUsers()
  },
  methods: {
    canDeleteAuthRow(row) {
      if (!row || row.id === this.currentOperatorId) return false
      const role = (row.role || '').toString().trim().toUpperCase()
      return role === 'USER'
    },
    async confirmDeleteAuthUser(row) {
      try {
        await ElMessageBox.confirm(
          `确定删除普通用户「${row.username}」？删除后无法恢复。`,
          '删除确认',
          {
            type: 'warning',
            confirmButtonText: '确定删除',
            cancelButtonText: '取消'
          }
        )
      } catch {
        return
      }
      this.deletingAuthId = row.id
      try {
        await deleteAuthUser(row.id)
        this.$message.success('已删除')
        await this.fetchAuthUsers()
      } catch (e) {
        const msg = e.response?.data?.message
        this.$message.error(msg || '删除失败')
      } finally {
        this.deletingAuthId = ''
      }
    },
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
        const records = data.records || []
        const total = data.total || 0
        if (records.length === 0 && total > 0 && this.authPageNum > 1) {
          this.authPageNum -= 1
          this.authLoading = false
          return this.fetchAuthUsers()
        }
        this.authRecords = records
        this.authTotal = total
      } catch (e) {
        this.authError = '加载系统账号失败'
        this.authRecords = []
        this.authTotal = 0
      } finally {
        this.authLoading = false
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

.op-muted {
  color: #94a3b8;
  font-size: 13px;
}
</style>
