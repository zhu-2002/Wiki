<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
          :selectedKeys="selectedKeys"
      >
        <a-menu-item key="welcome">
          <MailOutlined />
          <span>全部</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span><user-outlined />{{item.name}}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <MailOutlined /><span>{{child.name}}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content :style="{ padding: '24px',margin: 0 , minHeight: '280px' }">
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component v-bind:is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent,onMounted, ref } from 'vue';
import axios from 'axios' ;
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'Home',
  setup(){
    // 定义响应式变量
    const ebooks = ref() ;
    const level1 =  ref();
    let categoryId2 = 0;
    let categorys: any;
    const selectedKeys = ref(['welcome']); // 默认选中欢迎菜单项
    const isShowWelcome = ref(true);

    /**
     * 查询所有分类
     **/
    const handleQueryCategory = () => {
      axios.get("/category/list").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log("原始数组：", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构：", level1.value);
        } else {
          message.error(data.message);
        }
      });
    };

    const handleQueryEbook = () => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: categoryId2
        }
      }).then((response) => {
        const data = response.data;
        ebooks.value = data.content.list;
      });
    };

    const handleQueryAllEbook = () => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000
        }
      }).then((response) => {
        const data = response.data;
        ebooks.value = data.content.list;
      });
    };

    const handleClick = (value: any) => {
      selectedKeys.value = [value.key]; // 更新选中项
      if (value.key === 'welcome') {
        // isShowWelcome.value = true;
        handleQueryAllEbook();
      } else {
        categoryId2 = value.key ;
        // isShowWelcome.value = false;
        handleQueryEbook();
      }
    };

    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];

    onMounted(() => {
      handleQueryCategory();
      handleQueryAllEbook();
      isShowWelcome.value = false; // 隐藏欢迎页面，显示文档列表
    });

    return{
      ebooks,
      actions,
      pagination: {
        onChange: (page: any) =>{
          console.log(page);
        },
        pageSize: 2,
      },
      handleClick,
      level1,
      isShowWelcome,
      selectedKeys
    }
  }
});
</script>

<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>
