import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import UserList from '../views/UserList.vue'
import UserDetail from '../views/UserDetail.vue'
import UserRelations from '../views/UserRelations.vue'
import AdminUsers from '../views/AdminUsers.vue'
import CurrentUser from '../views/CurrentUser.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/users', component: UserList },
  {
    path: '/users/:id/followers',
    component: UserRelations,
    props: (route) => ({ id: route.params.id, relType: 'followers' })
  },
  {
    path: '/users/:id/following',
    component: UserRelations,
    props: (route) => ({ id: route.params.id, relType: 'following' })
  },
  { path: '/users/:id', component: UserDetail, props: true },
  { path: '/admin/users', component: AdminUsers },
  { path: '/me', component: CurrentUser }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const publicPaths = ['/login', '/register']
  if (!token && !publicPaths.includes(to.path)) {
    next('/login')
    return
  }
  if (token && publicPaths.includes(to.path)) {
    next('/users')
    return
  }
  if (to.path.startsWith('/admin')) {
    const role = localStorage.getItem('authRole')
    if (role !== 'ADMIN') {
      next('/users')
      return
    }
  }
  next()
})

export default router
