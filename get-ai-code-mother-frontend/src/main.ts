import { createApp } from 'vue'
import { createPinia } from 'pinia'
import "@/access/index.ts"

import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';

const app = createApp(App)  //创建一个vue的应用实例，该应用实例挂在App组件上。

app.use(createPinia())
app.use(router)
app.use(Antd)

app.mount('#app')
