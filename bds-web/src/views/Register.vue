<template>
  <section class="page">
    <el-card class="card">
      <template #header>
        <span>注册</span>
      </template>
      <el-form :model="form" label-width="88px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="设置用户名" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="设置密码"
            autocomplete="new-password"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">注册</el-button>
        </el-form-item>
      </el-form>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="hint">
        已有账号？
        <router-link to="/login">去登录</router-link>
      </p>
    </el-card>
  </section>
</template>

<script>
import { register } from '../api/auth'

export default {
  name: 'RegisterView',
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
    async handleRegister() {
      if (!this.form.username || !this.form.password) {
        this.error = '请输入用户名和密码。'
        return
      }
      this.loading = true
      this.error = ''
      try {
        await register(this.form)
        this.$router.push('/login')
      } catch (err) {
        this.error = '注册失败，请更换用户名重试。'
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
