import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUser } from '@/api/yonghuxiangguanjiekou.ts'

export const useLoginUserStore=defineStore('loginUser',()=>{
  const loginUser=ref<API.LoginUserVO>({
    userName: 'nothing'
  })
  //获取登录用户信息
  async function fetchLoginUser(){
    const res=await getLoginUser()
    if(res.data.code ===0 && res.data.data){
      loginUser.value=res.data.data
    }
  }

  //更新登录用户信息
  function setLoginUser(newLoginUser:any){
    loginUser.value=newLoginUser
  }


  return {loginUser,fetchLoginUser,setLoginUser}
})
