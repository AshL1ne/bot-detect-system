<template>
  <section class="page">
    <el-card class="card">
      <template #header>
        <span>登录</span>
      </template>
      <el-form :model="form" label-width="88px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="hint">
        没有账号？
        <router-link to="/register">去注册</router-link>
      </p>
    </el-card>
  </section>
</template>

<script>
import { login } from '../api/auth'

export default {
  name: 'LoginView',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      loading: false,
      error: ''
    }
  },
  methods: {
    async handleLogin() {
      if (!this.form.username || !this.form.password) {
        this.error = '请输入用户名和密码。'
        return
      }
      this.loading = true
      this.error = ''
      try {
        const response = await login(this.form)
        const data = response.data.data || {}
        const token = data.token
        if (!token) {
          this.error = '登录失败：未返回令牌。'
          return
        }
        localStorage.setItem('token', token)
        if (data.role) {
          localStorage.setItem('authRole', data.role)
        }
        window.dispatchEvent(new Event('storage'))
        this.$router.push('/users')
      } catch (err) {
        const status = err.response?.status
        if (status === 403) {
          this.error = '该账号已被禁用，请联系管理员。'
        } else {
          this.error = '登录失败，请检查用户名或密码。'
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.page {
  min-height: calc(100vh - 56px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.card {
  width: 360px;
}

.hint {
  margin-top: 12px;
  font-size: 12px;
  color: #6b7280;
}

.error {
  color: #d14343;
  margin-top: 8px;
}
</style>
