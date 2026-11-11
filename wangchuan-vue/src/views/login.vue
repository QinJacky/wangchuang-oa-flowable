<template>
  <div class="login">
    <!-- 左侧品牌区 -->
    <div class="login-left">
      <div class="brand">
        <div class="brand-logo">OA</div>
        <div class="brand-name">轻量 OA</div>
        <div class="brand-sub">工作流协同办公平台</div>
      </div>
      <div class="login-slogan">
        <div class="slogan-line">高效流转</div>
        <div class="slogan-line">协同办公 · 一栈直达</div>
      </div>
      <div class="login-copyright">{{ footerContent }}</div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-right">
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <h3 class="title">账号登录</h3>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" type="text" auto-complete="off" placeholder="账号">
            <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" auto-complete="off" placeholder="密码" @keyup.enter.native="handleLogin" show-password>
            <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          </el-input>
        </el-form-item>
        <el-form-item prop="code" v-if="captchaEnabled">
          <el-input v-model="loginForm.code" auto-complete="off" placeholder="验证码" style="width: 63%" @keyup.enter.native="handleLogin">
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="login-code-img" />
          </div>
        </el-form-item>
        <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
        <el-form-item style="width:100%;">
          <el-button :loading="loading" size="medium" type="primary" style="width:100%;" @click.native.prevent="handleLogin">
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
          <div style="float: right;" v-if="register">
            <router-link class="link-type" :to="'/register'">立即注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from "@/utils/jsencrypt";
import { connectWs } from "@/utils/socket/handleMessage.js";
import defaultSettings from '@/settings'

export default {
  name: "Login",
  data() {
    return {
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: "",
      },
      loginRules: {
        username: [{ required: true, trigger: "blur", message: "请输入您的账号" }],
        password: [{ required: true, trigger: "blur", message: "请输入您的密码" }],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }],
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined,
    };
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true,
    },
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then((res) => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled;
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get("rememberMe");
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe),
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set("rememberMe", this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove("rememberMe");
          }
          this.$store
            .dispatch("Login", this.loginForm)
            .then(() => {
              connectWs(this.$wsApi, this.$store, this.$enums, this.$msgType, this.$eventBus, this.$alert, this.$message);
              this.$router.push({ path: this.redirect || "/" }).catch(() => {});
            })
            .catch(() => {
              this.loading = false;
              if (this.captchaEnabled) {
                this.getCode();
              }
            });
        }
      });
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  display: flex;
  height: 100%;
  background: #f0f2f5;
}

/* 左侧品牌区 */
.login-left {
  width: 46%;
  background: linear-gradient(135deg, #2f54eb 0%, #4c7ef3 100%);
  color: #fff;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 8%;
  overflow: hidden;
  &::after {
    content: "";
    position: absolute;
    right: -120px;
    bottom: -120px;
    width: 360px;
    height: 360px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.12);
  }
  &::before {
    content: "";
    position: absolute;
    right: 60px;
    top: -80px;
    width: 220px;
    height: 220px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.08);
  }
}
.brand {
  position: relative;
  z-index: 1;
  .brand-logo {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.18);
    border: 1px solid rgba(255, 255, 255, 0.35);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26px;
    font-weight: 700;
    letter-spacing: 1px;
  }
  .brand-name {
    margin-top: 20px;
    font-size: 32px;
    font-weight: 700;
    letter-spacing: 2px;
  }
  .brand-sub {
    margin-top: 8px;
    font-size: 15px;
    opacity: 0.85;
    letter-spacing: 1px;
  }
}
.login-slogan {
  position: relative;
  z-index: 1;
  margin-top: 60px;
  .slogan-line {
    font-size: 20px;
    line-height: 1.9;
    opacity: 0.92;
    letter-spacing: 1px;
  }
}
.login-copyright {
  position: absolute;
  bottom: 24px;
  left: 8%;
  z-index: 1;
  font-size: 12px;
  opacity: 0.7;
}

/* 右侧表单区 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-form {
  border-radius: 12px;
  background: #ffffff;
  width: 380px;
  padding: 36px 32px 12px 32px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.title {
  margin: 0px auto 28px auto;
  text-align: center;
  color: #303133;
  font-size: 22px;
  font-weight: 600;
}
.login-code {
  width: 33%;
  height: 40px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 4px;
  }
}
.login-code-img {
  height: 40px;
}
</style>
