<template>
  <section class="page">
    <h1>User Detail</h1>
    <p v-if="loading">Loading...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <div v-else>
      <el-card class="section" v-if="detail">
        <template #header>
          <span>Profile</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="User ID">{{ detail.userId }}</el-descriptions-item>
          <el-descriptions-item label="Nickname">{{ detail.nickName }}</el-descriptions-item>
          <el-descriptions-item label="Followers">{{ detail.followersCount }}</el-descriptions-item>
          <el-descriptions-item label="Following">{{ detail.followCount }}</el-descriptions-item>
          <el-descriptions-item label="Statuses">{{ detail.statusesCount }}</el-descriptions-item>
          <el-descriptions-item label="Verified">
            <el-tag :type="detail.verified ? 'primary' : 'info'" effect="plain">
              {{ detail.verified ? 'Verified' : 'Unverified' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Original">{{ detail.originalCount }}</el-descriptions-item>
          <el-descriptions-item label="Forward">{{ detail.forwardCount }}</el-descriptions-item>
          <el-descriptions-item label="Mal Prob">{{ detail.malProb }}</el-descriptions-item>
          <el-descriptions-item label="Malicious">
            <el-tag :type="detail.isMalicious ? 'danger' : 'success'" effect="plain">
              {{ detail.isMalicious ? 'Malicious' : 'Normal' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Description" :span="2">{{ detail.description }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card class="section">
        <template #header>
          <span>Word Cloud</span>
        </template>
        <el-table :data="wordCloud" border>
          <el-table-column prop="word" label="Word" />
          <el-table-column prop="count" label="Count" width="120" />
        </el-table>
      </el-card>

      <el-card class="section">
        <template #header>
          <span>Active Hours</span>
        </template>
        <el-table :data="activeHours" border>
          <el-table-column prop="hour" label="Hour" width="120" />
          <el-table-column prop="count" label="Count" width="120" />
        </el-table>
      </el-card>
    </div>
  </section>
</template>

<script>
import { getUserActiveHours, getUserDetail, getUserWordCloud } from '../api/users'

export default {
  name: 'UserDetail',
  props: {
    id: {
      type: String,
      required: false
    }
  },
  data() {
    return {
      loading: false,
      error: '',
      detail: null,
      wordCloud: [],
      activeHours: []
    }
  },
  mounted() {
    this.loadAll()
  },
  watch: {
    id() {
      this.loadAll()
    }
  },
  methods: {
    async loadAll() {
      const userId = this.id || this.$route.params.id
      if (!userId) {
        this.error = 'Missing user id.'
        return
      }
      this.loading = true
      this.error = ''
      try {
        const [detailRes, wordRes, hoursRes] = await Promise.all([
          getUserDetail(userId),
          getUserWordCloud(userId),
          getUserActiveHours(userId)
        ])
        this.detail = detailRes.data.data
        this.wordCloud = wordRes.data.data || []
        this.activeHours = hoursRes.data.data || []
      } catch (err) {
        this.error = 'Failed to load user detail.'
        this.detail = null
        this.wordCloud = []
        this.activeHours = []
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

.section {
  margin-top: 16px;
}

.error {
  color: #d14343;
}
</style>
