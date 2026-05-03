<template>
  <section class="page">
    <el-card class="card">
      <template #header>
        <span>Register</span>
      </template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="Username">
          <el-input v-model="form.username" placeholder="Choose username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input v-model="form.password" type="password" placeholder="Choose password" autocomplete="new-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">Register</el-button>
        </el-form-item>
      </el-form>
      <p v-if="error" class="error">{{ error }}</p>
      <p class="hint">
        Already have an account?
        <router-link to="/login">Login</router-link>
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
        this.error = 'Please enter username and password.'
        return
      }
      this.loading = true
      this.error = ''
      try {
        await register(this.form)
        this.$router.push('/login')
      } catch (err) {
        this.error = 'Registration failed. Try another username.'
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
