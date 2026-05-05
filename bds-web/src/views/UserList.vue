<template>
  <section class="page">
    <h1>微博用户</h1>

    <div class="toolbar">
      <el-input
        v-model="keywordDraft"
        placeholder="按用户 ID 或昵称搜索"
        class="search-input"
        clearable
        @keyup.enter="onKeywordSearch"
      />
      <el-button type="primary" @click="onKeywordSearch">搜索</el-button>
      <el-select
        v-model="filterVerified"
        placeholder="是否认证"
        clearable
        class="filter-select"
        @change="onFilterChange"
      >
        <el-option label="已认证" :value="true" />
        <el-option label="未认证" :value="false" />
      </el-select>
      <el-select
        v-model="filterMalicious"
        placeholder="是否恶意"
        clearable
        class="filter-select"
        @change="onFilterChange"
      >
        <el-option label="恶意" :value="true" />
        <el-option label="正常" :value="false" />
      </el-select>
    </div>

    <el-table :data="records" v-loading="loading" border>
      <el-table-column label="用户 ID" min-width="160">
        <template #default="scope">
          <router-link :to="`/users/${scope.row.userId}`" class="link">
            {{ scope.row.userId }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="昵称" min-width="140">
        <template #default="scope">
          <router-link :to="`/users/${scope.row.userId}`" class="link">
            {{ scope.row.nickName }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="粉丝" width="110">
        <template #default="scope">
          <router-link :to="`/users/${scope.row.userId}/followers`" class="link">
            {{ scope.row.followersCount }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="关注" width="110">
        <template #default="scope">
          <router-link :to="`/users/${scope.row.userId}/following`" class="link">
            {{ scope.row.followCount }}
          </router-link>
        </template>
      </el-table-column>
      <el-table-column prop="statusesCount" label="微博数" width="110" />
      <el-table-column prop="malProb" label="恶意概率" width="110" />
      <el-table-column label="是否恶意" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.isMalicious ? 'danger' : 'success'" effect="plain">
            {{ scope.row.isMalicious ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否认证" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.verified ? 'primary' : 'info'" effect="plain">
            {{ scope.row.verified ? '是' : '否' }}
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
      filterVerified: undefined,
      filterMalicious: undefined,
      keywordDraft: '',
      /** Last keyword applied via Search; filters never infer from the draft input */
      appliedKeyword: '',
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
    /** Tag filters: own lifecycle; drops previously applied keyword until Search again */
    onFilterChange() {
      this.appliedKeyword = ''
      this.pageNum = 1
      this.fetchUsers()
    },
    /** Keyword: explicit Search only */
    onKeywordSearch() {
      this.appliedKeyword = (this.keywordDraft || '').trim()
      this.pageNum = 1
      this.fetchUsers()
    },
    buildPayload() {
      const payload = {
        pageNum: this.pageNum,
        pageSize: this.pageSize
      }
      const kw = this.appliedKeyword
      if (kw) {
        payload.keyword = kw
      }
      if (this.filterVerified === true || this.filterVerified === false) {
        payload.verified = this.filterVerified
      }
      if (this.filterMalicious === true || this.filterMalicious === false) {
        payload.isMalicious = this.filterMalicious
      }
      return payload
    },
    async fetchUsers() {
      this.loading = true
      this.error = ''
      try {
        const response = await searchUsers(this.buildPayload())
        const data = response.data.data || {}
        this.records = data.records || []
        this.total = data.total || 0
      } catch (err) {
        this.error = '加载用户列表失败。'
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
  flex-wrap: wrap;
}

.filter-select {
  width: 140px;
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
