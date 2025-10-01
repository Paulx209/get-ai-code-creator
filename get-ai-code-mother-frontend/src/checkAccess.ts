//用户权限 vs 当前登录用户权限
import accessEnum from '@/accessEnum.ts'
const checkAccess=(loginUser:any,needAccess=accessEnum.NOT_LOGIN) => {
  const loginUserAccess = loginUser ?.userRole ?? accessEnum.NOT_LOGIN
  //1.如果不需要权限
  if(needAccess == accessEnum.NOT_LOGIN){
    return true; //不需要登录就可以访问 , 放行
  }
  //2.如果需要用户权限
  if(needAccess === accessEnum.USER){
    if(loginUserAccess === accessEnum.NOT_LOGIN){
      return false;
    }
  }
  //3.如果需要管理员权限
  if(needAccess === accessEnum.ADMIN){
    if(loginUserAccess !== accessEnum.ADMIN){
      return false
    }
  }
  return true
}
export default  checkAccess
