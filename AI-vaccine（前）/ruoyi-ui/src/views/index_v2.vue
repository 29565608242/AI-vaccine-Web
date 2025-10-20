<template>
  <div style="height: 100vh; background-color: #fafbff">
    <div class="nav-top">
      <img src="../assets/logo/logo.png" alt="" />
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
    <div class="banner" @click="backButtom()">TransPed-Mutant</div>
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
      <div class="container">
        <div style="margin: auto">
          <div
            style="
              padding-left: 100px;
              width: 100%;
              margin-top: -100px;
              text-align: left;
            "
          >
            <div style="font-weight: 700">
              TransPed-Mutant
            </div>
            <div
              style="
                font-weight: 700;
                font-size: 20px;
                padding: 10px 10px 10px 0;
              "
            >
              (Antigen Immunogenicity  & Vaccine Design)
            </div>
            <div style="font-weight: 700; font-size: 20px">
              基于深度强化学习的抗原免疫原性预测和肿瘤疫苗设计框架
            </div>
            <div style="padding: 10px 10px 10px 0; font-weight: 700">
              它能够预测HLA-抗原肽和抗原肽-TCR的亲和度,
            </div>
            <div style="text-align: left; font-weight: 700">
              并对非亲和的抗原肽进行相应的位点突变使之亲和
            </div>
          </div>
          <div style="width: 100%; margin-top: 100px; padding-left: 100px">
            <el-button
              type="primary"
              style="width: 150px; height: 50px"
              round
              @click="$router.push('/start')"
              >Get Started</el-button
            >
            <el-button
              type="success"
              style="width: 150px; height: 50px"
              round
              @click="handleDownload"
              >Learn More</el-button
            >
          </div>
        </div>
        <div style="padding-top: 110px">
          <transition name="rotate" style="display: flex">
            <img
              src="@/assets/images/dna.png"
              :style="{ transform: 'rotate(' + angle + 'deg)' }"
              class="imgHW"
            />
          </transition>
        </div>
      </div>
    </div>



  </div>
</template>

<script>
import {download} from "@/utils/request";

export default {
  name: "Index_v2",
  data() {
    return {
      angle: 0,
      scrollButtomNum: "", //页面滚动的高度
    };
  },
  mounted() {
    window.addEventListener("scroll", this.handleScroll, true);
    setInterval(() => {
      this.angle += 5;
    }, 100);
  },
  methods: {
    handleDownload() {
      download("/medical/peptide/downloadLearnMore/", "","LearnMore.docx");
    },
    handleScroll() {
      let Buttom =
        document.documentElement.scrollButtom ||
        document.body.scrollButtom ||
        window.pageYOffset;

      this.scrollButtomNum = Buttom;
    },

    backButtom() {
      const goToButtom = () => window.scrollTo(0, 3380); //滚动到页面的位置3380

      goToButtom();
    },
  },
};
</script>

<style scoped lang="scss">
//.bg-image {
//  height: 100%;
//  background-image: url('../assets/images/bg.jpg');
//  filter:opacity(90%);
//  background-size: cover;
//  position: relative;
//}
li {
  list-style: none;
}
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
.banner {
  width: 100%;
  height: 96vh;
  margin-top: 60px;
  background: url(../assets/images/banner.jpg) no-repeat;
  background-size: 100% 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 50px;
  color: #fff;
}
.wrap {
  position: relative;
}
.imgHW {
  width: 70%;
  //height: 70%;
  margin: 30px;
}
.rotate-enter-active,
.rotate-leave-active {
  transition: transform 0.1s;
}
.rotate-enter,
.rotate-leave-to {
  transform: rotate(45deg);
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
.container {
  width: 100%;
  height: 100%;

  position: absolute;
  z-index: 9999;
  top: 0;
  display: flex;
}
</style>
