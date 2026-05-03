<template>
  <section class="page">
    <h1>Users</h1>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="Search by user id or nickname" class="search-input" clearable />
      <el-button type="primary" @click="fetchUsers">Search</el-button>
    </div>
    <el-table :data="records" v-loading="loading" border>
      <el-table-column label="User ID" min-width="160">
        <template #default="scope">
          <router-link :to="`/users/${scope.row.userId}`" class="link">
            {{ scope.row.userId }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="Nickname" min-width="140">
        <template #default="scope">
          <router-link :to="`/users/${scope.row.userId}`" class="link">
            {{ scope.row.nickName }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column prop="followersCount" label="Followers" width="110" />
      <el-table-column prop="followCount" label="Following" width="110" />
      <el-table-column prop="statusesCount" label="Statuses" width="110" />
      <el-table-column prop="malProb" label="Mal Prob" width="110" />
      <el-table-column label="Malicious" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.isMalicious ? 'danger' : 'success'" effect="plain">
            {{ scope.row.isMalicious ? 'Malicious' : 'Normal' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Verified" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.verified ? 'primary' : 'info'" effect="plain">
            {{ scope.row.verified ? 'Verified' : 'Unverified' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        :page-sizes="[10, 20, 50]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
    <p v-if="error" class="error">{{ error }}</p>
  </section>
</template>

<script>
import { searchUsers } from '../api/users'

export default {
  name: 'UserList',
  data() {
    return {
      keyword: '',
      records: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      error: ''
    }
  },
  mounted() {
    this.fetchUsers()
  },
  methods: {
    async fetchUsers() {
      this.loading = true
      this.error = ''
      try {
        const response = await searchUsers({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          keyword: this.keyword
        })
        const data = response.data.data || {}
        this.records = data.records || []
        this.total = data.total || 0
      } catch (err) {
        this.error = 'Failed to load users.'
        this.records = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.pageNum = page
      this.fetchUsers()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.pageNum = 1
      this.fetchUsers()
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

.link {
  color: #2563eb;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}
</style>

