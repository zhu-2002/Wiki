<template>
  <a-layout-header class="header">
    <div class="logo">WIKI</div>
    <a-menu
        class="menu"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
        v-model:selectedKeys="selectedKey"
    >
      <a-menu-item key="home">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <template v-if="user.id">
        <a-menu-item key="/admin/user">
          <router-link to="/admin/user">用户管理</router-link>
        </a-menu-item>
        <a-menu-item key="admin/ebook">
          <router-link to="/admin/ebook">电子书管理</router-link>
        </a-menu-item>
        <a-menu-item key="admin/category">
          <router-link to="/admin/category">分类管理</router-link>
        </a-menu-item>
      </template>
      <a-menu-item key="about">
        <router-link to="/about">关于作者</router-link>
      </a-menu-item>
    </a-menu>
    <a class="login-menu" v-show="user.id">
      <span>您好：{{user.name}}</span>
    </a>
    <a-popconfirm
        title="确认退出登录?"
        ok-text="是"
        cancel-text="否"
        @confirm="logout()"
    >
      <a class="login-menu" v-show="user.id">
        <span>退出登录</span>
      </a>
    </a-popconfirm>
    <a class="login-menu" v-show="!user.id" @click="showLoginModal">
      <span>登录</span>
    </a>
    <a-modal
        title="登录"
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        @ok="login"
    >
      <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登录名">
          <a-input v-model:value="loginUser.loginName" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input v-model:value="loginUser.password" type="password" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import { computed, defineComponent, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { message } from 'ant-design-vue';
import store from "@/store";
import router from "@/router";

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'default-header',
  setup() {
    const route = useRoute();
    const selectedKey = ref<string[]>([]);
    // 登录后保存
    const user = computed(() => store.state.user);

    const loginUser = ref({
      loginName: "",
      password: ""
    });
    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);
    const showLoginModal = () => {
      // 清空登录框中的默认文本
      loginUser.value.loginName = "";
      loginUser.value.password = "";
      loginModalVisible.value = true;
    };

    // 登录
    const login = () => {
      console.log("开始登录");
      loginModalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post('/user/login', loginUser.value).then((response) => {
        loginModalLoading.value = false;
        const data = response.data;
        if (data.success) {
          loginModalVisible.value = false;
          message.success("登录成功！");
          store.commit("setUser", data.content);
        } else {
          loginUser.value.password = ""; // 清空密码框
          message.error(data.message);
        }
      });
    };

    // 退出登录
    const logout = () => {
      axios.get('/user/logout/' + user.value.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登录成功！");
          store.commit("setUser", {});
          router.push('/');
        } else {
          message.error(data.message);
        }
      });
    };

    const updateSelectedKey = () => {
      switch (route.path) {
        case '/admin/ebook':
          selectedKey.value = ['admin/ebook'];
          break;
        case '/admin/category':
          selectedKey.value = ['admin/category'];
          break;
        case '/admin/doc':
          selectedKey.value = ['admin/ebook'];
          break;
        case '/about':
          selectedKey.value = ['about'];
          break;
        default:
          selectedKey.value = ['home'];
          break;
      }
    };

    watch(route, updateSelectedKey, { immediate: true });

    return {
      selectedKey,
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      loginUser,
      login,
      logout,
      user,
    };
  },
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Lobster&display=swap');

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #001529;
}

.logo {
  font-family: 'Lobster', cursive;
  font-size: 32px;
  font-weight: bold;
  color: white;
  text-align: center;
  margin-right: 24px;
}

.menu {
  flex: 1;
}

.login-menu {
  color: white;
  padding-left: 50px;
}
</style>
