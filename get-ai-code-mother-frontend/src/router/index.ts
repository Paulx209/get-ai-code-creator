import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import NoAuthPage from '@/pages/NoAuthPage.vue'
import accessEnum from '@/accessEnum.ts'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path:'/user/login',
      name:'用户登录页面',
      component: UserLoginPage,
    },
    {
      path:'/user/register',
      name:'用户注册页面',
      component: UserRegisterPage,
    },
    {
      path:'/admin/userManage',
      name:'用户管理页面',
      component: UserManagePage,
      meta:{
        access: accessEnum.ADMIN
      }
    },
    {
      path:'/noAuth',
      name:'无权限页面',
      component: NoAuthPage,
    }
  ],
})

export default router
