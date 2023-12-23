<template>
  <a-layout-header class="header">
<!--    <div class="logo" />-->
    <a-menu
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px'}"
        class="menu-container"
    >
      <a-menu-item key="/">
        <router-link to="/">首页</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/admin-user">
        <router-link to="/admin/admin-user">用户管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/admin-ebook">
        <router-link to="/admin/admin-ebook">电子书管理</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/admin-category">
        <router-link to="/admin/admin-category">分类管理</router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">关于我们</router-link>
      </a-menu-item>
    </a-menu>
    <div class="right-menu">
      <a  v-show="user.id">您好:{{user.name}}</a>
      <a-popconfirm
          title="确认退出登录?"
          ok-text="是"
          cancel-text="否"
          @confirm="logout()"
      >
        <a  v-show="user.id" >
          <span>退出登录</span>
        </a>
      </a-popconfirm>
      <a  @click="showLoginModal" v-show="!user.id">登录</a>
    </div>
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
import store from '@/store';
import { message } from 'ant-design-vue';
import axios from 'axios';
import {computed, defineComponent, ref } from 'vue';
declare let hexMd5: any;
declare let KEY: any;
export default defineComponent({
  name: 'TheHeader',
  setup () {
    // 登录后保存
    const user = computed(() => store.state.user);
    // 用来登录
    const loginUser = ref({
      loginName: "test",
      password: "test123"
    });
    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);
    const showLoginModal = () => {
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
          store.commit("setUser",data.content);
        } else {
          message.error(data.message);
        }
        loginUser.value.password="test123";
      });
    };

    // 退出登录
    const logout = () => {
      console.log("退出登录开始");
      axios.get('/user/logout/' + user.value.token).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出登录成功！");
          store.commit("setUser", {});
        } else {
          message.error(data.message);
        }
      });
    };
    return{
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      loginUser,
      login,
      logout,
      user
    }
  }
});
</script>

<style>
  .logo {
    width: 120px;
    height: 31px;
    /*background: rgba(255, 255, 255, 0.2);*/
    /*margin: 16px 28px 16px 0;*/
    //float: left;
    color: white;
    font-size: 18px;
  }
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #001529;
  }
  .right-menu {
    display: flex;
    align-items: center;
  }
  .right-menu a {
    margin-left: 10px;
    color: white;
  }
  .menu-container {
    margin-left: 150px !important;/* 整体向左移动的距离 */
  }

</style>