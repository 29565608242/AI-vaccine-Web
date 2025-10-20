package com.ruoyi.medical.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.medical.domain.Mutation;
import com.ruoyi.medical.service.IMutationService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.medical.domain.Peptide;
import com.ruoyi.medical.service.IPeptideService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 亲和度Controller
 * 
 * @author 唐宏伟
 * @date 2023-10-26
 */
@RestController
@RequestMapping("/medical/peptide")
public class PeptideController extends BaseController
{
    @Autowired
    private IPeptideService peptideService;

    @Autowired
    private IMutationService mutationService;

    @Autowired
    private RedisCache redisCache;

    @Value("${model.mutationUrl}")
    private String mutationUrl;

    @Value("${model.tcrMutationUrl}")
    private String tcrMutationUrl;

    @Value("${model.hlaTemplateFilePath}")
    private String hlaTemplateFilePath;

    @Value("${model.tcpTemplateFilePath}")
    private String tcpTemplateFilePath;

    @Value("${model.learnMoreFilePath}")
    private String learnMoreFilePath;

    final String CONTENT_TYPE_TEXT_JSON = "text/json";

    private static Gson gson = new Gson();

    /**
     * 查询亲和度列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Peptide peptide)
    {
        startPage();
        List<Peptide> list = peptideService.selectPeptideList(peptide);
        return getDataTable(list);
    }

    /**
     * 导出亲和度列表
     */
    @Log(title = "亲和度", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Peptide peptide)
    {
        List<Peptide> list = peptideService.selectPeptideList(peptide);
        ExcelUtil<Peptide> util = new ExcelUtil<Peptide>(Peptide.class);
        util.exportExcel(response, list, "亲和度数据");
    }

    /**
     * 获取亲和度详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(peptideService.selectPeptideById(id));
    }

    /**
     * 新增亲和度
     */
    @PreAuthorize("@ss.hasPermi('medical:peptide:add')")
    @Log(title = "亲和度", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Peptide peptide)
    {
        return toAjax(peptideService.insertPeptide(peptide));
    }

    /**
     * 修改亲和度
     */
    @Log(title = "亲和度", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Peptide peptide)
    {
        return toAjax(peptideService.updatePeptide(peptide));
    }

    /**
     * 删除亲和度
     */
    @Log(title = "亲和度", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
//        for(Long id : ids) {
//            String key = "data" + id;
//            redisCache.deleteObject(key);
//        }
        return toAjax(peptideService.deletePeptideByIds(ids));
    }

    @GetMapping(value = "/getData/{id}")
    public AjaxResult getData(@PathVariable("id") Long id)
    {
        Object data =  redisCache.getCacheObject("data" + id);
        System.out.println(data);
        return success(data);
    }

    @PostMapping(value = "/dnaChange")
    public AjaxResult dnaChange(@RequestBody Peptide peptide) throws Exception {
        if(peptide.getChangeFlag() == 1) {
            return AjaxResult.warn("该抗原氨基酸序列已经突变过！");
        }
        try {
            String result="";
            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(mutationUrl);
//            httppost.setHeader("Content-Type", "application/json;charset=UTF-8");
//            StringEntity se = new StringEntity(peptide.toString());
//            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addTextBody("hla", peptide.getHla())
                    .addTextBody("pep", peptide.getPeptide())
                    .addTextBody("modelType", peptide.getModelType().toString()).build();//添加参数
            httppost.setEntity(reqEntity);

            // 执行上传文件到模型操作
            CloseableHttpResponse response = httpclient.execute(httppost);

            HttpEntity resEntity = response.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonArray json = gson.fromJson(responseJson, JsonArray.class);
            System.out.println(gson.toJson(json));
            for (JsonElement element : json) {
                System.out.println(gson.toJson(element));
                JsonObject jsonObject = element.getAsJsonObject();
                String hla = jsonObject.get("HLA").getAsString();
                String mutatedPeptide = jsonObject.get("mutated_peptide").getAsString();
                String originalPeptide = jsonObject.get("original_peptide").getAsString();
                JsonArray xAxis = jsonObject.get("xAxis").getAsJsonArray();
                JsonArray yAxis = jsonObject.get("yAxis").getAsJsonArray();
                Float max = jsonObject.get("max").getAsFloat();
                Float min = jsonObject.get("min").getAsFloat();
                String aminoAcid = jsonObject.get("Mutation amino-acid site").getAsString();
                Float yPred = jsonObject.get("y_pred").getAsFloat();
                Float yProb = Float.valueOf(jsonObject.get("y_prob").getAsString());
                Float similarity = jsonObject.get("similarity").getAsFloat();
                JsonArray data = jsonObject.get("data").getAsJsonArray();

                Mutation mutation = new Mutation();
                mutation.setHla(hla);
                mutation.setMutatedPeptide(mutatedPeptide);
                mutation.setOriginalPeptide(originalPeptide);
                mutation.setOriginalId(peptide.getId());
                mutation.setSimilarity(similarity);
                mutation.setMaxValue(max);
                mutation.setMinValue(min);
                mutation.setxAxis(xAxis.toString());
                mutation.setyAxis(yAxis.toString());
                mutation.setThermodynamic(data.toString().substring(1,data.toString().length()-1));
                mutation.setyPred(yPred);
                mutation.setyProb(yProb);
                mutation.setMutationSite(aminoAcid);
                mutation.setChainNum(peptide.getChainNum());
                mutation.setStartNum(peptide.getStartNum());
                mutation.setIsTcr(0);

                mutationService.insertMutation(mutation);

            }

            peptide.setChangeFlag(1);
            peptideService.updatePeptide(peptide);

            int statusCode = response.getStatusLine().getStatusCode();
            response.close();
            httpclient.close();

            if (statusCode == HttpStatus.SC_OK) {

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error(475,"抗原氨基酸序列长度范围为8-14");
        }

    }

    @PostMapping(value = "/tcrChange")
    public AjaxResult tcrChange(@RequestBody Peptide peptide) throws Exception {
        if(peptide.getChangeFlag() == 1) {
            return AjaxResult.warn("该TCR氨基酸序列已经突变过！");
        }
        try {
            String result="";
            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(tcrMutationUrl);
//            httppost.setHeader("Content-Type", "application/json;charset=UTF-8");
//            StringEntity se = new StringEntity(peptide.toString());
//            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addTextBody("tcr", peptide.getHla())
                    .addTextBody("pep", peptide.getPeptide())
                    .addTextBody("modelType", peptide.getModelType().toString()).build();//添加参数
            httppost.setEntity(reqEntity);

            // 执行上传文件到模型操作
            CloseableHttpResponse response = httpclient.execute(httppost);

            HttpEntity resEntity = response.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonArray json = gson.fromJson(responseJson, JsonArray.class);

            for (JsonElement element : json) {
                JsonObject jsonObject = element.getAsJsonObject();
                String hla = jsonObject.get("TCR").getAsString();
                String mutatedPeptide = jsonObject.get("mutated_peptide").getAsString();
                String originalPeptide = jsonObject.get("original_peptide").getAsString();
                JsonArray xAxis = jsonObject.get("xAxis").getAsJsonArray();
                JsonArray yAxis = jsonObject.get("yAxis").getAsJsonArray();
                Float max = jsonObject.get("max").getAsFloat();
                Float min = jsonObject.get("min").getAsFloat();
                String aminoAcid = jsonObject.get("Mutation amino-acid site").getAsString();
                Float yPred = jsonObject.get("y_pred").getAsFloat();
                Float yProb = Float.valueOf(jsonObject.get("y_prob").getAsString());
                Float similarity = jsonObject.get("similarity").getAsFloat();
                JsonArray data = jsonObject.get("data").getAsJsonArray();

                Mutation mutation = new Mutation();
                mutation.setHla(hla);
                mutation.setMutatedPeptide(mutatedPeptide);
                mutation.setOriginalPeptide(originalPeptide);
                mutation.setOriginalId(peptide.getId());
                mutation.setSimilarity(similarity);
                mutation.setMaxValue(max);
                mutation.setMinValue(min);
                mutation.setxAxis(xAxis.toString());
                mutation.setyAxis(yAxis.toString());
                mutation.setThermodynamic(data.toString().substring(1,data.toString().length()-1));
                mutation.setyPred(yPred);
                mutation.setyProb(yProb);
                mutation.setMutationSite(aminoAcid);
                mutation.setChainNum(peptide.getChainNum());
                mutation.setStartNum(peptide.getStartNum());
                mutation.setIsTcr(1);

                mutationService.insertMutation(mutation);

            }

            peptide.setChangeFlag(1);
            peptideService.updatePeptide(peptide);

            int statusCode = response.getStatusLine().getStatusCode();
            response.close();
            httpclient.close();

            if (statusCode == HttpStatus.SC_OK) {

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error(475,"抗原氨基酸序列长度范围为8-14");
        }

    }

    @PostMapping("/downloadLearnMore")
    public void downLearnMoreFile(HttpServletResponse response) throws Exception
    {
        // 创建一个字节输入流
        FileInputStream fis = new FileInputStream(learnMoreFilePath);
        // 创建一个字节数组，并设置大小为文件长度
        byte[] imageBytes = new byte[fis.available()];
        // 将文件内容读入字节数组
        fis.read(imageBytes);
        // 关闭输入流
        fis.close();
        response.setContentType("docx");
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.flush();
        os.close();
    }

    @PostMapping("/downloadHlaTemplate")
    public void downFile(HttpServletResponse response) throws Exception
    {
        // 创建一个字节输入流
        FileInputStream fis = new FileInputStream(hlaTemplateFilePath);
        // 创建一个字节数组，并设置大小为文件长度
        byte[] imageBytes = new byte[fis.available()];
        // 将文件内容读入字节数组
        fis.read(imageBytes);
        // 关闭输入流
        fis.close();
        response.setContentType("text/csv");
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.flush();
        os.close();
    }

    @PostMapping("/downloadTcpTemplate")
    public void downTcpFile(HttpServletResponse response) throws Exception
    {
        // 创建一个字节输入流
        FileInputStream fis = new FileInputStream(tcpTemplateFilePath);
        // 创建一个字节数组，并设置大小为文件长度
        byte[] imageBytes = new byte[fis.available()];
        // 将文件内容读入字节数组
        fis.read(imageBytes);
        // 关闭输入流
        fis.close();
        response.setContentType("text/csv");
        OutputStream os = response.getOutputStream();
        os.write(imageBytes);
        os.flush();
        os.close();
    }
}
