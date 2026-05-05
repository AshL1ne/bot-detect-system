<template>
  <section class="page">
    <h1>我的账户</h1>
    <p v-if="loading">加载中…</p>
    <p v-else-if="error">{{ error }}</p>
    <div v-else-if="profile" class="card">
      <div class="row"><span class="label">账户 ID</span>{{ profile.id }}</div>
      <div class="row"><span class="label">用户名</span>{{ profile.username }}</div>
      <div class="row"><span class="label">角色</span>{{ roleText }}</div>
      <div class="row"><span class="label">状态</span>{{ statusText }}</div>
    </div>
    <p v-else>暂无账户信息。</p>
  </section>
</template>

<script>
import { me } from '../api/auth'

export default {
  name: 'CurrentUser',
  data() {
    return {
      loading: false,
      error: '',
      profile: null
    }
  },
  computed: {
    roleText() {
      const r = this.profile?.role
      if (r === 'ADMIN') return '管理员'
      if (r === 'USER') return '普通用户'
      return r || '—'
    },
    statusText() {
      const s = this.profile?.status
      if (s === 1) return '启用'
      if (s === 0) return '禁用'
      return s != null ? String(s) : '—'
    }
  },
  mounted() {
    this.loadProfile()
  },
  methods: {
    async loadProfile() {
      this.loading = true
      this.error = ''
      try {
        const response = await me()
        this.profile = response.data.data
      } catch (err) {
        this.profile = null
        this.error = '加载账户信息失败，请重新登录。'
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.page {
  padding: 24px;
}

.card {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  max-width: 420px;
}

.row {
  display: flex;
  gap: 8px;
  padding: 6px 0;
}

.label {
  width: 90px;
  flex-shrink: 0;
  color: #6b7280;
}
</style>
