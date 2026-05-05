<template>
  <div id="app">
    <header class="app-header">
      <div class="header-left">
        <button
          v-if="showHeaderBack"
          type="button"
          class="nav-back"
          @click="goBack"
        >
          ← 返回
        </button>
      </div>
      <div class="header-brand">
        <span class="title">Bot Detect System</span>
      </div>
      <nav class="nav header-right">
        <router-link v-if="isLoggedIn" to="/users" class="nav-link">用户</router-link>
        <router-link v-if="isLoggedIn" to="/me" class="nav-link">我的</router-link>
        <router-link v-if="canAccessAdmin" to="/admin/users" class="nav-link">管理</router-link>
        <router-link v-if="!isLoggedIn" to="/login" class="nav-link">登录</router-link>
        <router-link v-if="!isLoggedIn" to="/register" class="nav-link">注册</router-link>
        <button v-if="isLoggedIn" class="nav-link nav-button" @click="logout">退出</button>
      </nav>
    </header>
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <keep-alive :include="['UserList']">
          <component :is="Component" />
        </keep-alive>
      </router-view>
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
    },
    showHeaderBack() {
      if (!this.isLoggedIn) return false
      const p = this.$route.path
      if (p === '/login' || p === '/register') return false
      if (p === '/users') return false
      return true
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
        localStorage.removeItem('authRole')
      }
    },
    async refreshProfile() {
      if (!this.token) {
        return
      }
      try {
        const response = await me()
        const r = response.data.data.role || ''
        this.role = r
        if (r) {
          localStorage.setItem('authRole', r)
        }
        if (['/login', '/register'].includes(this.$route.path)) {
          this.$router.push('/me')
        }
      } catch (err) {
        this.role = ''
        localStorage.removeItem('token')
        localStorage.removeItem('authRole')
      }
    },
    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('authRole')
      this.syncToken()
      this.$router.push('/login')
    },
    goBack() {
      this.$router.back()
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
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  padding: 0 24px;
  background: #1f2d3d;
  color: #fff;
}

.header-left {
  justify-self: start;
  min-width: 0;
}

.header-brand {
  justify-self: center;
  text-align: center;
}

.header-right {
  justify-self: end;
}

.title {
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.02em;
  color: #fff;
}

.nav-back {
  background: transparent;
  border: none;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  padding: 6px 8px;
  border-radius: 4px;
}

.nav-back:hover {
  background: rgba(255, 255, 255, 0.12);
}

.nav {
  display: flex;
  gap: 12px;
  align-items: center;
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
