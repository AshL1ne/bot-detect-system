<template>
  <section class="page">
    <h1>Current User</h1>
    <p v-if="loading">Loading...</p>
    <p v-else-if="error">{{ error }}</p>
    <div v-else-if="profile" class="card">
      <div class="row"><span class="label">User ID:</span>{{ profile.id }}</div>
      <div class="row"><span class="label">Username:</span>{{ profile.username }}</div>
      <div class="row"><span class="label">Role:</span>{{ profile.role }}</div>
      <div class="row"><span class="label">Status:</span>{{ profile.status }}</div>
    </div>
    <p v-else>No profile data.</p>
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
        this.error = 'Failed to load profile. Please login again.'
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
  color: #6b7280;
}
</style>

