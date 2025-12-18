import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/checkin',
    name: 'PublicCheckin',
    component: () => import('@/views/CheckinPublic.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/checkout',
    name: 'PublicCheckout',
    component: () => import('@/views/CheckoutPublic.vue'),
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
        component: () => import('@/views/Home.vue'),
        meta: { role: 'admin' }
      },
      {
        path: 'my-trainings',
        name: 'MyTrainings',
        component: () => import('@/views/MyTrainings.vue')
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
        component: () => import('@/views/Trainings.vue'),
        meta: { role: 'admin' }
      },
      {
        path: 'trainings/create',
        name: 'TrainingCreate',
        component: () => import('@/views/TrainingForm.vue'),
        meta: { role: 'admin' }
      },
      {
        path: 'trainings/:id',
        name: 'TrainingDetail',
        component: () => import('@/views/TrainingDetail.vue')
      },
      {
        path: 'trainings/:id/edit',
        name: 'TrainingEdit',
        component: () => import('@/views/TrainingForm.vue'),
        meta: { role: 'admin' }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('@/views/Stats.vue'),
        meta: { role: 'admin' }
      },
      {
        path: 'rag',
        name: 'Rag',
        component: () => import('@/views/Rag.vue')
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
  
  // 处理根路径重定向：根据角色跳转
  if (to.path === '/' || to.path === '/home') {
    if (to.path === '/home' && authStore.user?.role !== 'admin') {
      // 员工访问首页，重定向到我的培训
      next('/my-trainings')
      return
    }
    if (to.path === '/' && authStore.user?.role === 'admin') {
      next('/home')
      return
    }
    if (to.path === '/' && authStore.user?.role !== 'admin') {
      next('/my-trainings')
      return
    }
  }
  
  // 角色权限检查
  if (to.meta.role && authStore.user?.role !== to.meta.role) {
    ElMessage.error('无权限访问')
    // 根据角色跳转到对应页面
    if (authStore.user?.role === 'admin') {
      next('/home')
    } else {
      next('/my-trainings')
    }
    return
  }
  
  next()
})

export default router


