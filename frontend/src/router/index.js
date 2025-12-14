import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/Users.vue'),
        meta: { role: 'admin' }
      },
      {
        path: 'trainings',
        name: 'Trainings',
        component: () => import('@/views/Trainings.vue')
      },
      {
        path: 'trainings/create',
        name: 'TrainingCreate',
        component: () => import('@/views/TrainingForm.vue')
      },
      {
        path: 'trainings/:id',
        name: 'TrainingDetail',
        component: () => import('@/views/TrainingDetail.vue')
      },
      {
        path: 'trainings/:id/edit',
        name: 'TrainingEdit',
        component: () => import('@/views/TrainingForm.vue')
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('@/views/Stats.vue'),
        meta: { role: 'admin' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth === false) {
    next()
    return
  }
  
  if (!authStore.token) {
    next('/login')
    return
  }
  
  // 角色权限检查
  if (to.meta.role && authStore.user?.role !== to.meta.role) {
    ElMessage.error('无权限访问')
    next('/home')
    return
  }
  
  next()
})

export default router


