import router from '@/router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { message } from 'ant-design-vue'


//是否为首次登录?
let firstLoginUser = true
/**
 * 全局权限校验
 */
router.beforeEach(async  (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  if (firstLoginUser) {
    //首次登录 刷新数据
    loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstLoginUser = false
  }
  //判断我们访问的url是否为admin
  const toUrl = to.fullPath
  if (toUrl.startsWith('/admin') && (!loginUser || loginUser.userRole !== 'admin')) {
    message.error('没有权限')
    next(`/user/login?redirect=${to.fullPath}`)
    return
  }
  next()
})
