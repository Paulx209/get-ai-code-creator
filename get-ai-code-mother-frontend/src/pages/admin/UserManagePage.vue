<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-divider />

    <a-table
      :columns="columns"
      :data-source="userData"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #headerCell="{ column }">
        <template v-if="column.key === 'name'">
          <span>
            <smile-outlined />
            Name
          </span>
        </template>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="deleteRecord(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { SmileOutlined } from '@ant-design/icons-vue'
import { computed, ref } from 'vue'
import { deleteUserById, getUserVoPage } from '@/api/yonghuxiangguanjiekou.ts'
import { onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const userData = ref<API.UserVO[]>([])
const total = ref(0)
//搜索条件
const searchParams = ref<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

const pagination = computed(() => {
  return {
    current: searchParams.value.pageNum ?? 1,
    pageSize: searchParams.value.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

const fetchData = async () => {
  const res = await getUserVoPage(searchParams.value)
  if (res.data.code === 0 && res.data.data) {
    //返回了数据
    userData.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败：', res.data.message)
  }
}

const doTableChange = async (page: any) => {
  searchParams.value.pageNum = page.current
  searchParams.value.pageSize = page.pageSize
  fetchData()
}

const doSearch = async ()=>{
  //重置页码
  searchParams.value.pageNum=1
  fetchData()
}

const deleteRecord=async (id:string)=>{
  if(!id){
    return
  }
 const res= await deleteUserById({id})
  if(res.data.code ===0){
    message.success("删除成功")
    fetchData()
  }else{
    message.error('删除失败')
  }
}
onMounted(() => {
  fetchData()
})

//表格列
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]
</script>
