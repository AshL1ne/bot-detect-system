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

      <el-card class="section word-cloud-card">
        <template #header>
          <span>词云</span>
        </template>
        <div class="word-cloud-wrap">
          <div v-show="wordCloud.length" ref="wordCloudEl" class="word-cloud-el" />
          <el-empty v-show="!wordCloud.length" description="暂无词云数据" />
        </div>
      </el-card>

      <el-card class="section activity-card">
        <template #header>
          <span>活跃时段（每 2 小时）</span>
        </template>
        <div class="chart-block">
          <div class="chart-title">原创 / 转发</div>
          <div ref="activityOriginalRetweetEl" class="line-chart-el" />
        </div>
        <div class="chart-block chart-block--last">
          <div class="chart-title">全部微博</div>
          <div ref="activityTotalEl" class="line-chart-el" />
        </div>
      </el-card>

      <el-card class="section tweets-wrap">
        <template #header>
          <span>推文</span>
        </template>
        <div class="tweet-feed" v-loading="tweetsLoading">
          <template v-if="tweetRecords.length">
            <article
              v-for="t in tweetRecords"
              :key="t.tweetId"
              class="tweet-card"
            >
              <header class="tweet-head">
                <div class="tweet-name">{{ displayName }}</div>
                <div class="tweet-meta">{{ formatTweetTime(t.createdAt) }}</div>
              </header>
              <div class="tweet-body">
                <p v-if="t.content" class="tweet-text">{{ t.content }}</p>
                <div v-if="isRetweetPost(t)" class="retweet-box">
                  <span class="retweet-at">@{{ t.rtOriginUserId || 'unknown' }}</span>
                  <span class="retweet-text">{{ t.rtOriginContent || '' }}</span>
                </div>
              </div>
              <footer class="tweet-actions">
                <span class="action">
                  <span class="action-label">转发</span>
                  {{ formatCount(t.repostsCount) }}
                </span>
                <span class="action muted" title="评论数未采集">
                  <span class="action-label">评论</span>
                  —
                </span>
                <span class="action">
                  <span class="action-label">赞</span>
                  {{ formatCount(t.attitudesCount) }}
                </span>
              </footer>
            </article>
          </template>
          <el-empty v-else-if="!tweetsLoading" description="暂无推文" />
          <div class="tweet-pager" v-if="tweetTotal > 0">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="tweetTotal"
              :page-size="tweetPageSize"
              :current-page="tweetPageNum"
              @current-change="handleTweetPage"
            />
          </div>
        </div>
      </el-card>
    </div>
  </section>
</template>

<script>
import * as echarts from 'echarts'
import 'echarts-wordcloud'
import { getUserActiveHours, getUserDetail, getUserWordCloud } from '../api/users'
import { searchTweets } from '../api/tweets'

const WORD_PALETTE = ['#2dd4bf', '#4ade80', '#c084fc', '#fbbf24', '#38bdf8', '#34d399', '#a78bfa', '#f472b6']

function stableColorForWord(word) {
  let h = 2166136261
  const s = String(word)
  for (let i = 0; i < s.length; i++) {
    h ^= s.charCodeAt(i)
    h = Math.imul(h, 16777619)
  }
  return WORD_PALETTE[Math.abs(h) % WORD_PALETTE.length]
}

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
      activeHours: [],
      tweetsLoading: false,
      tweetRecords: [],
      tweetTotal: 0,
      tweetPageNum: 1,
      tweetPageSize: 10,
      wordCloudChart: null,
      activityOriginalRetweetChart: null,
      activityTotalChart: null,
      resizeTimer: null,
      resizeRaf: null
    }
  },
  computed: {
    displayName() {
      return this.detail?.nickName || this.detail?.userId || '用户'
    },
    sortedActiveHours() {
      const list = this.activeHours || []
      return [...list].sort(
        (a, b) => (Number(a.periodStartHour) || 0) - (Number(b.periodStartHour) || 0)
      )
    }
  },
  mounted() {
    this.loadAll()
    window.addEventListener('resize', this.onWindowResize)
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.onWindowResize)
    if (this.resizeTimer) {
      clearTimeout(this.resizeTimer)
      this.resizeTimer = null
    }
    if (this.resizeRaf) {
      cancelAnimationFrame(this.resizeRaf)
      this.resizeRaf = null
    }
    this.disposeCharts()
  },
  watch: {
    id() {
      this.loadAll()
    }
  },
  methods: {
    onWindowResize() {
      if (this.resizeTimer) clearTimeout(this.resizeTimer)
      this.resizeTimer = setTimeout(() => {
        if (this.resizeRaf) cancelAnimationFrame(this.resizeRaf)
        this.resizeRaf = requestAnimationFrame(() => {
          this.wordCloudChart?.resize()
          this.activityOriginalRetweetChart?.resize()
          this.activityTotalChart?.resize()
        })
      }, 120)
    },
    disposeCharts() {
      if (this.wordCloudChart) {
        this.wordCloudChart.dispose()
        this.wordCloudChart = null
      }
      if (this.activityOriginalRetweetChart) {
        this.activityOriginalRetweetChart.dispose()
        this.activityOriginalRetweetChart = null
      }
      if (this.activityTotalChart) {
        this.activityTotalChart.dispose()
        this.activityTotalChart = null
      }
    },
    refreshCharts() {
      this.$nextTick(() => {
        this.initWordCloudChart()
        this.initActivityCharts()
      })
    },
    initWordCloudChart() {
      const el = this.$refs.wordCloudEl
      if (!el) return
      if (!this.wordCloud.length) {
        this.wordCloudChart?.clear()
        return
      }
      this.wordCloudChart =
        this.wordCloudChart || echarts.getInstanceByDom(el) || echarts.init(el)
      const ranked = [...this.wordCloud].sort(
        (a, b) => (Number(b.count) || 0) - (Number(a.count) || 0)
      )
      const capped = ranked.slice(0, 88)
      const data = capped.map((w) => {
        const raw = String(w.word)
        return {
          name: raw,
          value: Math.max(1, Number(w.count) || 1),
          rawWord: raw
        }
      })
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          show: true,
          formatter(p) {
            const raw = p.data.rawWord || String(p.name)
            return `${raw}<br/>频次：<b>${p.value}</b>`
          }
        },
        series: [
          {
            type: 'wordCloud',
            shape: 'circle',
            left: 'center',
            top: 'center',
            width: '92%',
            height: '92%',
            sizeRange: [11, 46],
            rotationRange: [0, 0],
            rotationStep: 90,
            gridSize: 14,
            drawOutOfBound: false,
            shrinkToFit: true,
            layoutAnimation: true,
            textStyle: {
              fontFamily: '"Microsoft YaHei","PingFang SC","Noto Sans SC",sans-serif',
              fontWeight: 'bold',
              color: (params) =>
                stableColorForWord(params.data.rawWord || String(params.name))
            },
            emphasis: {
              focus: 'self',
              textStyle: { textShadowBlur: 8, textShadowColor: 'rgba(0,0,0,0.35)' }
            },
            data
          }
        ]
      }
      this.wordCloudChart.setOption(option, { notMerge: true })
    },
    activityPeriodLabels() {
      return Array.from({ length: 12 }, (_, i) => `${i * 2}–${i * 2 + 2}时`)
    },
    activityYAxisMax() {
      let peak = 0
      for (const row of this.sortedActiveHours) {
        const o = Number(row.originalCount) || 0
        const r = Number(row.retweetCount) || 0
        const t = Number(row.totalCount) || 0
        peak = Math.max(peak, o, r, t)
      }
      return Math.max(20, Math.ceil(peak / 10) * 10)
    },
    activityYInterval(yMax) {
      return yMax <= 20 ? 5 : 10
    },
    /** 关闭 sampling，避免单系列折线图例显隐时触发 dataSample 报错 */
    lineSeriesCommon(color, seriesData, name) {
      const safeData = (Array.isArray(seriesData) ? seriesData : []).map((v) =>
        Number.isFinite(Number(v)) ? Number(v) : 0
      )
      return {
        id: name,
        name,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 12,
        showSymbol: true,
        sampling: false,
        large: false,
        lineStyle: { width: 2.5, color },
        itemStyle: { color },
        label: { show: false },
        emphasis: { scale: 1.2, focus: 'series' },
        connectNulls: true,
        data: safeData
      }
    },
    activityChartAxes(labels, yMax, yInterval) {
      return {
        grid: { left: 52, right: 28, top: 40, bottom: 72 },
        xAxis: {
          type: 'category',
          boundaryGap: true,
          data: labels,
          name: '时间段',
          nameLocation: 'middle',
          nameGap: 38,
          axisTick: { alignWithLabel: true, interval: 0 },
          axisLabel: {
            interval: 0,
            rotate: 0,
            margin: 16,
            fontSize: 12,
            color: '#475569',
            width: 72,
            overflow: 'break',
            lineHeight: 16
          },
          axisLine: { lineStyle: { color: '#94a3b8' } },
          triggerEvent: true
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: yMax,
          interval: yInterval,
          name: '微博数量',
          nameTextStyle: { color: '#334155' },
          axisLabel: { color: '#475569' },
          splitLine: { lineStyle: { color: '#cbd5e1', type: 'dashed' } }
        }
      }
    },
    /** 仅悬停在折线圆点上显示数量，不出现坐标轴交叉线 */
    pointTooltip(labels) {
      return {
        trigger: 'item',
        confine: true,
        appendToBody: true,
        axisPointer: { type: 'none' },
        formatter(params) {
          if (!params || params.dataIndex == null) return ''
          const idx = params.dataIndex
          const label = labels[idx] ?? ''
          const v = params.value
          return `${label}<br/>${params.marker}${params.seriesName}：<b>${v ?? 0}</b>`
        }
      }
    },
    initActivityCharts() {
      const splitEl = this.$refs.activityOriginalRetweetEl
      const totalEl = this.$refs.activityTotalEl
      if (!splitEl || !totalEl) return
      const rows = this.sortedActiveHours
      const labels = this.activityPeriodLabels()
      const slotVal = (pick) =>
        Array.from({ length: 12 }, (_, slot) => {
          const row = rows.find((r) => Number(r.periodStartHour) === slot * 2)
          return row ? Number(pick(row)) || 0 : 0
        })
      const originals = slotVal((r) => r.originalCount)
      const retweets = slotVal((r) => r.retweetCount)
      const totals = slotVal((r) => r.totalCount)
      const yMax = this.activityYAxisMax()
      const yInterval = this.activityYInterval(yMax)

      const splitSeries = [
        this.lineSeriesCommon('#22c55e', originals, '原创微博'),
        this.lineSeriesCommon('#eab308', retweets, '转发微博')
      ]

      this.activityOriginalRetweetChart =
        this.activityOriginalRetweetChart ||
        echarts.getInstanceByDom(splitEl) ||
        echarts.init(splitEl)
      this.activityOriginalRetweetChart.setOption(
        {
          backgroundColor: 'transparent',
          tooltip: this.pointTooltip(labels),
          legend: {
            data: splitSeries.map((s) => s.name),
            bottom: 4,
            padding: [4, 8, 0, 8],
            textStyle: { color: '#334155' },
            selectedMode: false
          },
          ...this.activityChartAxes(labels, yMax, yInterval),
          series: splitSeries
        },
        { notMerge: true }
      )

      const totalSeries = [this.lineSeriesCommon('#2563eb', totals, '全部微博')]

      this.activityTotalChart =
        this.activityTotalChart ||
        echarts.getInstanceByDom(totalEl) ||
        echarts.init(totalEl)
      this.activityTotalChart.setOption(
        {
          backgroundColor: 'transparent',
          tooltip: this.pointTooltip(labels),
          legend: {
            data: totalSeries.map((s) => s.name),
            bottom: 4,
            padding: [4, 8, 0, 8],
            textStyle: { color: '#334155' },
            selectedMode: false
          },
          ...this.activityChartAxes(labels, yMax, yInterval),
          series: totalSeries
        },
        { notMerge: true }
      )
    },
    async loadAll() {
      const userId = this.id || this.$route.params.id
      if (!userId) {
        this.error = 'Missing user id.'
        return
      }
      this.loading = true
      this.error = ''
      this.tweetPageNum = 1
      try {
        const [detailRes, wordRes, hoursRes] = await Promise.all([
          getUserDetail(userId),
          getUserWordCloud(userId),
          getUserActiveHours(userId)
        ])
        this.detail = detailRes.data.data
        this.wordCloud = wordRes.data.data || []
        this.activeHours = hoursRes.data.data || []
        await this.loadTweets(userId)
      } catch (err) {
        this.error = 'Failed to load user detail.'
        this.detail = null
        this.wordCloud = []
        this.activeHours = []
        this.tweetRecords = []
        this.tweetTotal = 0
      } finally {
        this.loading = false
        this.$nextTick(() => this.refreshCharts())
      }
    },
    async loadTweets(userId) {
      const uid = userId || this.id || this.$route.params.id
      if (!uid) return
      this.tweetsLoading = true
      try {
        const res = await searchTweets({
          userId: uid,
          pageNum: this.tweetPageNum,
          pageSize: this.tweetPageSize
        })
        const page = res.data.data || {}
        this.tweetRecords = page.records || []
        this.tweetTotal = page.total ?? 0
      } catch (e) {
        this.tweetRecords = []
        this.tweetTotal = 0
      } finally {
        this.tweetsLoading = false
      }
    },
    handleTweetPage(p) {
      this.tweetPageNum = p
      this.loadTweets()
    },
    isRetweetPost(t) {
      if (!t) return false
      return t.isRetweet === true || t.retweet === true
    },
    formatCount(n) {
      if (n === null || n === undefined) return '0'
      const v = Number(n)
      if (Number.isNaN(v)) return '0'
      return String(v)
    },
    formatTweetTime(iso) {
      if (!iso) return ''
      const t = new Date(iso).getTime()
      if (Number.isNaN(t)) return iso
      const diff = Date.now() - t
      const sec = Math.floor(diff / 1000)
      if (sec < 60) return '刚刚'
      const min = Math.floor(sec / 60)
      if (min < 60) return `${min} 分钟前`
      const hr = Math.floor(min / 60)
      if (hr < 24) return `${hr} 小时前`
      const day = Math.floor(hr / 24)
      if (day < 7) return `${day} 天前`
      const d = new Date(t)
      const pad = (x) => String(x).padStart(2, '0')
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
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

.word-cloud-card :deep(.el-card__body) {
  padding: 12px;
  background: linear-gradient(180deg, #e0f2fe 0%, #dbeafe 100%);
}

.word-cloud-wrap {
  min-height: 420px;
  position: relative;
}

.word-cloud-el {
  width: 100%;
  height: 420px;
}

.activity-card :deep(.el-card__body) {
  padding: 16px 16px 8px;
  background: #f0f9ff;
}

.chart-block {
  margin-bottom: 20px;
}

.chart-block--last {
  margin-bottom: 8px;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 8px;
}

.line-chart-el {
  width: 100%;
  height: 300px;
}

.tweets-wrap :deep(.el-card__body) {
  padding: 0;
  background: #dbeafe;
}

.tweet-feed {
  min-height: 120px;
}

.tweet-card {
  padding: 16px 18px 12px;
  border-bottom: 1px solid #93c5fd;
  color: #0f172a;
  background: #e8f4fc;
}

.tweet-card:last-of-type {
  border-bottom: none;
}

.tweet-head {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 10px;
}

.tweet-name {
  font-weight: 600;
  color: #0f172a;
  font-size: 15px;
}

.tweet-meta {
  font-size: 12px;
  color: #64748b;
}

.tweet-body {
  font-size: 15px;
  line-height: 1.55;
}

.tweet-text {
  margin: 0 0 10px;
  white-space: pre-wrap;
  word-break: break-word;
}

.retweet-box {
  background: #cfe8fc;
  border-radius: 8px;
  padding: 12px 14px;
  font-size: 14px;
  line-height: 1.5;
  color: #1e293b;
  border: 1px solid #bfdbfe;
}

.retweet-at {
  color: #2563eb;
  margin-right: 6px;
}

.retweet-text {
  white-space: pre-wrap;
  word-break: break-word;
}

.tweet-actions {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-top: 14px;
  padding-top: 10px;
  border-top: 1px solid #93c5fd;
  font-size: 13px;
  color: #475569;
}

.action {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.action.muted {
  opacity: 0.55;
}

.action-label {
  opacity: 0.75;
  margin-right: 4px;
}

.tweet-pager {
  padding: 16px;
  display: flex;
  justify-content: center;
  background: #dbeafe;
}

.tweet-pager :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #409eff;
}

.tweets-wrap :deep(.el-loading-mask) {
  background-color: rgba(219, 234, 254, 0.75);
}
</style>
