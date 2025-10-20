<template>
  <div class="app-container" :class="loading === false && mutationList.length >=9 ? 'bg-image' : ''">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label-width="120px" label="HLA氨基酸序列" prop="hla">
        <el-input
          v-model="queryParams.hla"
          placeholder="请输入HLA氨基酸序列"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="突变前肽序列" label-width="120px" prop="originalPeptide">
        <el-input
            v-model="queryParams.originalPeptide"
            placeholder="请输入突变前肽序列"
            clearable
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="突变后的肽序列" label-width="120px" prop="mutatedPeptide">
        <el-input
          v-model="queryParams.mutatedPeptide"
          placeholder="请输入突变后的肽序列"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label-width="100px" label="亲合度标签" prop="yPred">
        <el-input
          v-model="queryParams.yPred"
          placeholder="请输入亲合度标签"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label-width="120px" label="模型预测概率" prop="yProb">
        <el-input
          v-model="queryParams.yProb"
          placeholder="请输入模型预测概率"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="相似度" prop="similarity">
        <el-input
          v-model="queryParams.similarity"
          placeholder="请输入相似度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
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
        <el-button type="primary" plain icon="el-icon-upload" size="mini" @click="handleUpload">上传原始肽结构文件</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="mutationList" style="opacity:0.8;width:100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
<!--      <el-table-column fixed label="HLA氨基酸序列" width="400px" align="center" prop="hla" />-->
      <el-table-column fixed label="突变后的肽序列" width="200px" align="center" prop="mutatedPeptide" />
      <el-table-column label="突变前的肽序列" width="200px" align="center" prop="originalPeptide" />
      <el-table-column label="突变位点" width="200px" align="center" prop="mutationSite" />
      <el-table-column label="模型预测概率" sortable width="100px" align="center" prop="yProb" />
      <el-table-column label="相似度" width="100px" align="center" prop="similarity" />
      <el-table-column label="亲合度标签" width="100px" align="center" prop="yPred" />
      <el-table-column label="蛋白质链号" width="150px" align="center" prop="chainNum" />
      <el-table-column label="起始残基序号" width="150px" align="center" prop="startNum" />
      <el-table-column  fixed="right" :width="width" label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
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
          <el-button
              icon="el-icon-download"
              size="mini"
              type="text"
              @click="handleDownloadGraph(scope.row)"
              v-if="flag"
          >下载突变肽结构图</el-button>
          <el-button
              icon="el-icon-download"
              size="mini"
              type="text"
              @click="handleDownloadFile(scope.row)"
              v-if="flag"
          >下载突变肽结构文件</el-button>
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

    <!-- 添加或修改多肽变异对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="hla序列" prop="hla">
          <el-input v-model="form.hla" placeholder="请输入hla序列" />
        </el-form-item>
        <el-form-item label="突变后的肽序列" prop="mutatedPeptide">
          <el-input v-model="form.mutatedPeptide" placeholder="请输入突变后的肽序列" />
        </el-form-item>
        <el-form-item label="y_pred" prop="yPred">
          <el-input v-model="form.yPred" placeholder="请输入y_pred" />
        </el-form-item>
        <el-form-item label="概率" prop="yProb">
          <el-input v-model="form.yProb" placeholder="请输入概率" />
        </el-form-item>
        <el-form-item label="突变后肽序列" prop="originalPeptide">
          <el-input v-model="form.originalPeptide" placeholder="请输入突变后肽序列" />
        </el-form-item>
        <el-form-item label="相似度" prop="similarity">
          <el-input v-model="form.similarity" placeholder="请输入相似度" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="upload" width="500px" append-to-body>
      <el-form ref="uploadForm" :model="uploadForm" label-width="80px" >
        <el-form-item label="上传文件" label-width="90px">
          <file-upload v-model="uploadForm.originalStructure" ref="myModelUpload" :file-type="fileType" :file-size="fileSize" :limit="limit"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpload">确 定</el-button>
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
import { listMutation, getMutation, delMutation, addMutation, updateMutation, downloadGraph } from "@/api/medical/mutation";
import ThermodynamicChart from "@/views/dashboard/ThermodynamicChart.vue";
import { download } from "@/utils/request";
import MyFileUpload from "@/components/MyFileUpload/index.vue";
import {getPeptide, updatePeptide} from "@/api/medical/peptide";

export default {
  name: "Mutation",
  components: {MyFileUpload, ThermodynamicChart},
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
      // 多肽变异表格数据
      mutationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        hla: null,
        mutatedPeptide: null,
        yPred: null,
        yProb: null,
        originalPeptide: null,
        originalId: null,
        similarity: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
      fileType: ["cif", "pdb"],
      fileSize: 20,
      limit: 1,
      uploadForm: {
        id: null,
        changeFlag: 1,
        originalStructure: null
      },
      upload: false,
      chartData: {},
      drawer: false,
      direction: 'rtl',
      flag: false,
      width: "200px",
    };
  },
  created() {
    this.getList();
    this.getPeptide();
  },
  watch:{
    '$route.query': function(){
      var id = this.$route.query.id;
      this.queryParams.originalId = id
      this.getList()
     }
  },
  methods: {
    /** 查询多肽变异列表 */
    getList() {
      this.loading = true;
      var id = this.$route.query.id;
      this.queryParams.originalId = id
      listMutation(this.queryParams).then(response => {
        this.mutationList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    handleDownloadGraph(row) {
      var id = this.$route.query.id;
      var mutationId = row.id
      download("/medical/mutation/downGraph/" + id +"/" +mutationId, "","突变肽结构图.png");
    },
    handleDownloadFile(row) {
      var id = this.$route.query.id;
      var mutationId = row.id
      download("/medical/mutation/downFile/" + id +"/" +mutationId, "","MutPeptideStructure.pdb");
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        hla: null,
        mutatedPeptide: null,
        yPred: null,
        yProb: null,
        originalPeptide: null,
        similarity: null,
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
    handleClose(done) {
      this.drawer = false;
    },
    handleUpload () {
      this.upload = true;
      this.title = "上传原始肽结构文件"
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
      this.title = "添加多肽变异";
    },
    async getPeptide() {
      var id = this.uploadForm.id = this.$route.query.id;
      const {data} = await getPeptide(id);
      this.flag = data.originalStructure !== null
      if(this.flag) {
        this.width = "500px";
      }
    },
    cancelUpload() {
      this.upload = false;
      this.resetForm("uploadForm");
    },
    submitUpload() {
      this.uploadForm.id = this.$route.query.id;
      updatePeptide(this.uploadForm).then(response => {
        this.upload = false;
        this.resetForm("uploadForm");
        this.getPeptide();
        this.$modal.msgSuccess("上传原始肽结构文件成功");
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getMutation(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改多肽变异";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateMutation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMutation(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除该变异后的序列？').then(function() {
        return delMutation(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('medical/mutation/export', {
        ...this.queryParams
      }, `mutation_${new Date().getTime()}.xlsx`)
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
