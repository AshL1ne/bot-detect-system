<template>
  <section class="page">
    <el-card class="card">
      <template #header>
        <span>Login</span>
      </template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="Username">
          <el-input v-model="form.username" placeholder="Enter username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" placeholder="Enter password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">Login</el-button>
        </el-form-item>
      </el-form>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="hint">
        No account?
        <router-link to="/register">Register</router-link>
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
        this.error = 'Please enter username and password.'
        return
      }
      this.loading = true
      this.error = ''
      try {
        const response = await login(this.form)
        const token = response.data.data?.token
        if (!token) {
          this.error = 'Login failed. Token missing.'
          return
        }
        localStorage.setItem('token', token)
        window.dispatchEvent(new Event('storage'))
        this.$router.push('/users')
      } catch (err) {
        this.error = 'Login failed. Please check your credentials.'
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
