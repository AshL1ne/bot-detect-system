<template>
  <div id="app">
    <header class="app-header">
      <span class="title">BDS</span>
      <nav class="nav">
        <router-link v-if="isLoggedIn" to="/users" class="nav-link">Users</router-link>
        <router-link v-if="isLoggedIn" to="/me" class="nav-link">Me</router-link>
        <router-link v-if="canAccessAdmin" to="/admin/users" class="nav-link">Admin</router-link>
        <router-link v-if="!isLoggedIn" to="/login" class="nav-link">Login</router-link>
        <router-link v-if="!isLoggedIn" to="/register" class="nav-link">Register</router-link>
        <button v-if="isLoggedIn" class="nav-link nav-button" @click="logout">Logout</button>
      </nav>
    </header>
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script>
import { me } from './api/auth'

export default {
  name: 'App',
  data() {
    return {
      token: localStorage.getItem('token'),
      role: ''
    }
  },
  computed: {
    isLoggedIn() {
      return !!this.token
    },
    canAccessAdmin() {
      return this.isLoggedIn && this.role === 'ADMIN'
    }
  },
  mounted() {
    window.addEventListener('storage', this.syncToken)
    this.refreshProfile()
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.syncToken)
  },
  methods: {
    syncToken() {
      this.token = localStorage.getItem('token')
      if (this.token) {
        this.refreshProfile()
      } else {
        this.role = ''
      }
    },
    async refreshProfile() {
      if (!this.token) {
        return
      }
      try {
        const response = await me()
        this.role = response.data.data.role || ''
        if (['/login', '/register'].includes(this.$route.path)) {
          this.$router.push('/me')
        }
      } catch (err) {
        this.role = ''
        localStorage.removeItem('token')
      }
    },
    logout() {
      localStorage.removeItem('token')
      this.syncToken()
      this.$router.push('/login')
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.app-header {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #1f2d3d;
  color: #fff;
}

.title {
  font-weight: 600;
}

.nav {
  display: flex;
  gap: 12px;
}

.nav-link {
  color: #e5e7eb;
  text-decoration: none;
  font-size: 14px;
}

.nav-link.router-link-active {
  color: #fff;
  font-weight: 600;
}

.nav-button {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
}

.app-main {
  min-height: calc(100vh - 56px);
}
</style>
