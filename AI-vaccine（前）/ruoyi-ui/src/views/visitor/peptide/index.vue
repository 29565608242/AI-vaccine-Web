<template>
  <div class="app-container" :class="loading === false && peptideList.length >=9 ? 'bg-image' : ''">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="HLA氨基酸序列" label-width="120px" prop="hla">
        <el-input
            v-model="queryParams.hla"
            placeholder="请输入HLA氨基酸序列"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="多肽" prop="peptide">
        <el-input
            v-model="queryParams.peptide"
            placeholder="请输入多肽序列"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预测模型类型" label-width="100px" prop="modelType">
        <el-select style="width: 200px" v-model="queryParams.modelType" placeholder="请选择预测模型类型" clearable>
          <el-option
              v-for="dict in dict.type.sys_model"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否突变" prop="changeFlag">
        <el-select style="width: 200px" v-model="queryParams.changeFlag" placeholder="请选择是否突变" clearable>
          <el-option
              v-for="dict in dict.type.sys_change"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="info" :icon="iconName" plain size="mini" :class="className">运行状态</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['medical:peptide:edit']"-->
<!--        >修改</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-download" size="mini" @click="handleDownload">下载模板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-upload" size="mini" @click="handleUpload">上传</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="peptideList" style="opacity:0.8;width:100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column fixed label="HLA氨基酸序列" width="400px" align="center" prop="hla" />
      <el-table-column fixed label="抗原氨基酸序列" width="300px" align="center" prop="peptide" />
      <el-table-column label="亲合度标签" width="150px" align="center" prop="yPred" />
      <el-table-column label="蛋白质链号" width="150px" align="center" prop="chainNum" />
      <el-table-column label="起始残基序号" width="150px" align="center" prop="startNum" />
      <el-table-column label="预测模型类型" width="150px" align="center" prop="modelType">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_model" :value="scope.row.modelType"/>
        </template>
      </el-table-column>
      <el-table-column label="模型预测概率" sortable width="150px" align="center" prop="yProb" />
      <el-table-column label="是否已突变" width="150px" align="center" prop="changeFlag">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_change" :value="scope.row.changeFlag"/>
        </template>
      </el-table-column>
<!--      <el-table-column label="得分" align="center" prop="score" />-->
      <el-table-column fixed="right" width="300px" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleSeeChange(scope.row)"
            v-if="scope.row.changeFlag === 1"
          >查看突变序列</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="viewGraph(scope.row)"
          >查看热力图</el-button>
          <el-button size="mini" type="text" v-if="scope.row.changeFlag !== 1" @click="dnaChange(scope.row)"><svg-icon icon-class="DNA"/>突变</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改亲和度对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="HLA" prop="hla">
          <el-input v-model="form.hla" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="多肽" prop="peptide">
          <el-input v-model="form.peptide" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="亲和力" prop="binding">
          <el-input v-model="form.binding" placeholder="请输入亲和力" />
        </el-form-item>
        <el-form-item label="预测值" prop="prediction">
          <el-input v-model="form.prediction" placeholder="请输入预测值" />
        </el-form-item>
        <el-form-item label="得分" prop="score">
          <el-input v-model="form.score" placeholder="请输入得分" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改亲和度对话框 -->
    <el-dialog :title="title" :visible.sync="upload" width="500px" append-to-body>
      <el-form ref="uploadForm" :model="uploadForm" label-width="80px" >
        <el-form-item label="上传文件">
          <my-file-upload ref="myFileUpload" @processing="processing" @processSuccess="getList"
                          :file-type="fileType" :file-size="fileSize" :limit="limit" :model-id="form.modelId"/>
        </el-form-item>
        <el-form-item label="选择模型">
          <el-select v-model="form.modelId" placeholder="请选择模型">
            <el-option
                v-for="dict in dict.type.sys_model"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpload">预 测</el-button>
        <el-button @click="cancelUpload">取 消</el-button>
      </div>
    </el-dialog>

    <el-drawer
        :title="title"
        :visible.sync="drawer"
        :direction="direction"
        size="40%"
        :before-close="handleClose">
      <div style="padding: 20px">
        <thermodynamic-chart width="100%" height="600px" :chart-data="chartData"/>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { listPeptide, getPeptide, delPeptide, addPeptide, updatePeptide, getData, dnaChange } from "@/api/medical/peptide";
import MyFileUpload from "@/components/MyFileUpload"
import ThermodynamicChart from "@/views/dashboard/ThermodynamicChart.vue";
import {download} from "@/utils/request";
export default {
  name: "Peptide",
  dicts: ['sys_model', 'sys_change'],
  components: {ThermodynamicChart, MyFileUpload },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 亲和度表格数据
      peptideList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      upload: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        hla: null,
        peptide: null,
        binding: null,
        isTcr: 0,
        prediction: null,
        score: null,
        thermodynamic: null,
      },
      fileType: ["csv"],
      fileSize: 5,
      limit: 1,
      // 表单参数
      form: {
        modelId: 1,
      },
      // 表单校验
      rules: {

      },
      uploadForm: {
        filePath: null
      },
      drawer: false,
      direction: 'rtl',
      chartData: {},
      className: '',
      iconName: ''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询亲和度列表 */ getList() {
      this.loading = true;
      this.upload = false;
      this.className = '';
      this.iconName = 'el-icon-video-play';
      listPeptide(this.queryParams).then(response => {
        this.peptideList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        hla: null,
        peptide: null,
        binding: null,
        prediction: null,
        score: null,
        thermodynamic: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    handleUpload () {
      this.upload = true;
      this.title = "上传文件"
    },
    cancelUpload() {
      this.upload = false;
      this.resetForm("uploadForm");
      this.form.modelId = ''
      this.form.modelPath = ''
      this.$refs.myFileUpload.reset();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加记录";
    },
    /** 修改按钮操作 */
    handleSeeChange(row) {
      this.$router.push({
        path: '/visitor/mutation?id=' + row.id,
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["uploadForm"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updatePeptide(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addPeptide(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    submitUpload() {
      this.$refs.myFileUpload.submit(this.form.modelId);
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除该抗原氨基酸序列').then(function() {
        return delPeptide(ids);
      }).then(() => {
        this.getList();
        this.$notify({
          title: '成功',
          message: '删除成功',
          type: 'success'
        });
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$set(this.queryParams, "isTcr" , 0);
      this.download('/medical/peptide/export', {
        ...this.queryParams
      }, `peptide_${new Date().getTime()}.xlsx`)
    },
    handleDownload() {
      download("/medical/peptide/downloadHlaTemplate/", "","HLA_Template.csv");
    },
    viewGraph(row) {
      console.log(row);
      const xdata = row.xAxis.substring(1, row.xAxis.length - 1).split(',');
      const ydata = row.yAxis.substring(1, row.yAxis.length - 1).split(',');
      // const { data } = await getData(row.id);
      const data = row.thermodynamic;
      let processData = data.split('],');
      let realData = []
      for( var i = 0; i<processData.length; i++) {
        var temp =  processData[i].replaceAll('[','').replaceAll(' ','').split(',');
        var item = [parseInt(temp[0]),parseInt(temp[1]),parseFloat(temp[2])];
        realData.push(item)
      }
      this.$set(this.chartData, "data", realData);
      this.$set(this.chartData, "xdata", xdata);
      this.$set(this.chartData, "ydata", ydata);
      this.$set(this.chartData, "max", row.maxValue);
      this.$set(this.chartData, "min", row.minValue);
      this.drawer = true;
      this.title = '热力图'
    },
    handleClose(done) {
      this.drawer = false;
    },
    processing () {
      this.upload = false;
      this.className = "blue-button"
      this.iconName = "el-icon-video-pause"
      // this.$modal.loading("模型处理中，请稍候...");
    },
    async dnaChange(row) {
      const id = row.id
      this.$modal.loading("模型处理中，请稍候...");
      const {data} = await getPeptide(id);
      this.form = data;
      dnaChange(this.form).then(res=>{
        this.getList();
        this.$modal.closeLoading();
        this.$notify({
          title: '成功',
          message: '抗原氨基酸序列已突变',
          type: 'success'
        });
      }).catch(res=>{
        this.$modal.closeLoading();
        if(res.code === 475){
          this.$message({
            message: res.msg,
            type: 'warning'
          });
        }
      })
    }
  }
};
</script>
<style scoped lang="scss">
.bg-image {
  height: 100%;
  background-image: url('../../../assets/images/bg.jpg');
  background-size: cover;
  position: relative;
}

.blue-button {
  background-color: #67C23A;
}

</style>
