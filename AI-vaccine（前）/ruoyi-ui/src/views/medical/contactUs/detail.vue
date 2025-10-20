<template>
  <div>
    <div class="member"><i class="el-icon-message" style="color: #00afff;margin-right: 10px"/>联系我们</div>
    <div class="article_content">
      <div class="wrapper">
        <div class="main">
          <div class="box" v-for="item in contactUsList" :key="item">
            <div class="left">
              <img :src="item.image" style="height: 100%;width: 200px;border-radius: 10px"/>
            </div>
            <div class="right">
              <div style="margin-top: 10px;">姓名：{{item.name}}</div>
              <div>工作单位：{{item.school}}</div>
              <div>年龄：{{item.age}}</div>
              <div style="display: flex">
                性别: <dict-tag :options="dict.type.sys_user_sex" :value="item.sex"/>
              </div>
              <div>联系电话：{{item.phone}}</div>
              <div>邮箱：{{item.email}}</div>
              <div>详细描述：<span style="font-size: 14px;font-weight: 400" v-html="item.detail"></span></div>

            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { listContactUs } from "@/api/medical/contactUs";
export default {
  dicts: ['sys_user_sex'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 联系我们表格数据
      contactUsList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        sex: null,
        phone: null,
        image: null,
        detail: null,
        email: null,
        age: null,
        school: null,
      },
    };
  },
  // watch: {
  //   "$route.query": {
  //     handler() {
  //       this.id = this.$route.query.id;
  //       this.getDetail();
  //     },
  //     deep: true,
  //   },
  // },
  methods: {
    getDetail() {
      this.loading = true;
      listContactUs(this.queryParams).then(response => {
        this.contactUsList = response.rows.map((r) => {
          var item = {};
          item = {...r};
          item.image = process.env.VUE_APP_BASE_API + r.image;
          return item;
        });
        this.loading = false;
      });
    }
  },
  created() {
    this.getDetail();
  },

};
</script>

<style lang="scss" scoped>
.member {
  width: 100%;
  text-align: center;
  font-size: 20px;
  font-family: 宋体;
  font-weight: 700;
  padding: 20px;
  //margin-left: -20%;
  background-color: #fafbff;
}
.article_content {
  background-color: #fafbff;
  height: 100vh;
  .wrapper {
    width: 50%;
    margin: 0 auto;
    .main {
      width: 100%;
      font-size: 18px;
      font-family: 宋体;
      font-weight: 700;
      .box {
        display: flex;
        align-items: center;
        height: 200px;
        border: #00afff 1px solid;
        border-radius: 10px;
        margin-bottom: 10px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
        .left {
          width: 30%;
        }
        .right {
          width: 70%;

        }
      }
    }

  }
}
</style>
