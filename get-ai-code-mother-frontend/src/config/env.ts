/**
 * 环境变量配置
 */
import { CodeGenTypeEnum } from '@/utils/codeGenTypes.ts'

// 应用部署域名
export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://localhost'

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'

// 静态资源地址
export const STATIC_BASE_URL = `${API_BASE_URL}/static`  // http://localhost:8123/api/static

// 获取部署应用的完整URL
export const getDeployUrl = (deployKey: string) => {
  return `${DEPLOY_DOMAIN}/${deployKey}`
}

// 获取静态资源预览URL
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  const baseUrl = `${STATIC_BASE_URL}/${codeGenType}_${appId}/`  // http://localhost:8123/api/static/multi_file_262622/
  //如果是vue项目的话
  if(codeGenType === CodeGenTypeEnum.VUE_PROJECT){
    return `${baseUrl}dist/index.html` // http://localhost:8123/api/static/vue_project_262622/dist/index.html
  }
  return baseUrl
}
