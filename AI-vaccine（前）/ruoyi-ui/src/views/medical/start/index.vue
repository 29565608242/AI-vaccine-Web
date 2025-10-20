<template>
  <div>
    <div class="nav-top">
      <img src="../../../assets/logo/logo.png" alt="" />
      <ul>
        <li>
          <el-button
            href=""
            style="
              color: #014492;
              border-bottom: 3px solid #014492;
              background-color: rgb(4, 70, 147, 0.2);
            "
          >首页</el-button
          >
        </li>
        <li>
          <el-button type="text" class="active" style="color: black;">更新日志</el-button>
        </li>

        <li>
          <el-button type="text" class="active" style="color: black;" @click="handleDownload">资料下载</el-button>
        </li>
        <li>
          <el-button type="text" class="active" style="color: black;">关于我们</el-button>
        </li>
        <li>
          <el-button type="text" class="active" style="color: black;">加入我们</el-button>
        </li>
        <li>
          <el-button type="text" class="active" style="color: black;">联系我们</el-button>
        </li>
        <li>
          <el-button type="text" class="active" style="color: black;">在线讲座</el-button>
        </li>

      </ul>
      <div class="inpt"><input type="text" /><button>搜索</button></div>
    </div>
    <div class="particles-box">
      <vue-particles
        class="login-bg"
        color="#39AFFD"
        :particleOpacity="0.7"
        :particlesNumber="100"
        shapeType="circle"
        :particleSize="4"
        linesColor="#8DD1FE"
        :linesWidth="1"
        :lineLinked="true"
        :lineOpacity="0.4"
        :linesDistance="150"
        :moveSpeed="3"
        :hoverEffect="true"
        hoverMode="grab"
        :clickEffect="true"
        clickMode="push"
      >
      </vue-particles>
      <div class="main-box">
        <div class="title">
          <h1>功能范围</h1>
          <p>Function Scope</p>
        </div>
        <div class="main-core">
          <section class="one" @click="$router.push('/visitor/peptide')">
            <img src="../../../assets/icons/start-icon1.png" alt="" />
            <p>抗原肽和HLA的抗原免疫原性预测</p>
            <p>和疫苗突变优化设计</p>
          </section>
          <section class="two" @click="$router.push('/visitor/tcr')">
            <img src="../../../assets/icons/start-icon2.png" alt="" />
            <p>抗原肽和TCR的抗原免疫原性预测</p>
            <p>和疫苗突变优化设计</p>
          </section>

        </div>
      </div>
    </div>

    <!-- <div class="container">
      <div class="box" @click="$router.push('/visitor/peptide')">
        <svg-icon
          icon-class="fangdajing"
          class-name="card-panel-icon"
          class="myicon"
        />
        <div class="desc">
          <div style="padding-top: 50px; text-align: center">
            HLA-抗原肽的亲和度预测和突变
          </div>
        </div>
      </div>
      <div class="box" @click="$router.push('/visitor/tcr')">
        <svg-icon
          icon-class="medical"
          class-name="card-panel-icon"
          class="myicon"
        />
        <div class="desc">
          <div style="padding-top: 50px; text-align: center">
            抗原肽-TCR的亲和度预测和突变
          </div>
        </div>
      </div>
    </div> -->
  </div>
</template>

<script>
import {download} from "@/utils/request";

export default {
  name: "Index_v2",
  data() {
    return {};
  },
  methods: {
    handleDownload() {
      download("/medical/peptide/downloadLearnMore/", "","LearnMore.docx");
    },
  },
};
</script>

<style scoped lang="scss">
.nav-top {
  position: fixed;
  top: 0;
  width: 100%;
  height: 60px;
  background-color: #fff;
  z-index: 999;
  display: flex;
  justify-content: center;
  align-items: center;
  border-bottom: #f7f7f7;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.212);
  z-index: 999999999;
  ul {
    width: 60%;
    display: flex;
    justify-content: space-around;
    li {
      list-style: none;
      a {
        padding: 23px 12px 17px 12px;
      }
    }
    .active:hover {
      color: #014492;
      border-bottom: 3px solid #014492;
      background-color: rgb(4, 70, 147, 0.2);
    }
  }
  .inpt {
    width: 145px;
    height: 30px;
    display: flex;

    background-color: #f9f9f9;
    input {
      width: 100px;
    }
    button {
      width: 50px;
      height: 30px;
      background-color: #014492;
      border: 0;
      cursor: pointer;
      color: #fff;
      border-top-right-radius: 5px;
      border-bottom-right-radius: 5px;
    }
  }
}

.bg-image {
  height: 100%;
  // background-image: url("../../../assets/images/bg.jpg");
  background-size: cover;
  position: relative;
}
.main {
  height: 100vh;
  background-color: #fafbff;

  display: flex;
  .container {
    margin: auto;
    width: 65%;
    display: flex;
    .box {
      margin: auto;
      width: 400px;
      height: 400px;
      background-color: #41b3f9;
      border-radius: 20px;
      display: flex;
      position: relative;
      filter: opacity(90%);
      .myicon {
        height: 50px;
        width: 50px;
        position: absolute;
        top: -30px;
        left: 50%;
      }
      .desc {
        margin: auto;
        color: #556665;
      }
    }
    .box:hover {
      background-color: #77bc1f;
      color: #ffffff;
    }
  }
}
.main-box {
  width: 60%;

  position: absolute;
  top: 7%;
  left: 20%;
  margin-top: 60px;
  height: 600px;
  .title {
    width: 100%;
    height: 100px;
    display: flex;
    flex-direction: column;
    align-items: center;
    h2 {
      line-height: 0;
    }
    p {
      line-height: 0;
      color: #c0bbbe;
      font-size: 25px;
      margin-top: -5px;
    }
  }
  .main-core {
    width: 100%;
    height: 400px;
    display: flex;
    justify-content: space-between;
    margin-top: 50px;
    .one {
      width: 40%;
      height: 100%;
      background-color: #2b6bab;
      display: flex;
      flex-direction: column;
      justify-content: space-evenly;
      align-items: center;
      p {
        color: #fff;
        font-size: 20px;
      }
    }
    .two {
      width: 40%;
      height: 100%;
      background-color: #409c83;
      display: flex;
      flex-direction: column;
      justify-content: space-evenly;
      align-items: center;
      p {
        color: #fff;
        font-size: 20px;
      }
    }
    .three {
      width: 30%;
      height: 100%;
      background-color: #248daa;
      display: flex;
      flex-direction: column;
      justify-content: space-evenly;
      align-items: center;
      p {
        color: #fff;
        font-size: 20px;
      }
    }
  }
}

.particles-box {
  width: 100%;
  height: 100%;
  //color: #cccccc;

  /*如果想做背景图片 可以给标签一个class 直接添加背景图*/
  position: relative;
  // 如果屎全屏展示
  .login-bg {
    width: 100%;
    height: 100%;
    background-color: #fff;
  }
}
</style>
