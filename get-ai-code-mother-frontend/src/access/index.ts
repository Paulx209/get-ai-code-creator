import router from '@/router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import checkAccess from '@/checkAccess.ts'
import ACCESS_ENUM from '@/accessEnum.ts'

router.beforeEach(async (to,from,next) =>{
  const loginUserStore=useLoginUserStore()
  //获取当前的用户 以及需要的权限
  let loginUser=loginUserStore.loginUser
  if(!loginUser ||!loginUser.userRole){
    //首次登录,先等待获取数据
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
  }
  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN
  if(needAccess != ACCESS_ENUM.NOT_LOGIN){
    //需要登录，但是用户
    if(!loginUser || !loginUser.userRole || loginUser.userRole === ACCESS_ENUM.NOT_LOGIN){
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
    if(!checkAccess(loginUser,needAccess)){
      next('/noAuth')
    }
  }
  next()

})
