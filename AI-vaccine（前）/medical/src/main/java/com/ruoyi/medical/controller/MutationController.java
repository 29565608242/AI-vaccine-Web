package com.ruoyi.medical.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.medical.domain.Peptide;
import com.ruoyi.medical.service.IPeptideService;
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
import org.springframework.http.MediaType;
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
import com.ruoyi.medical.domain.Mutation;
import com.ruoyi.medical.service.IMutationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 多肽变异Controller
 * 
 * @author 唐宏伟
 * @date 2023-11-04
 */
@RestController
@RequestMapping("/medical/mutation")
public class MutationController extends BaseController
{
    @Autowired
    private IMutationService mutationService;

    @Value("${model.downloadGraphUrl}")
    private String downloadGraphUrl;

    @Value("${model.uploadOriginalFileUrl}")
    private String uploadOriginalFileUrl;

    @Value("${model.uploadTcrOriginalFileUrl}")
    private String uploadTcrOriginalFileUrl;

    @Value("${model.downloadTcrGraphUrl}")
    private String downloadTcrGraphUrl;

    @Autowired
    private IPeptideService peptideService;

    private static Gson gson = new Gson();

    /**
     * 查询多肽变异列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Mutation mutation)
    {
        startPage();
        List<Mutation> list = mutationService.selectMutationList(mutation);
        return getDataTable(list);
    }

    /**
     * 导出多肽变异列表
     */
    @Log(title = "多肽变异", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Mutation mutation)
    {
        List<Mutation> list = mutationService.selectMutationList(mutation);
        ExcelUtil<Mutation> util = new ExcelUtil<Mutation>(Mutation.class);
        util.exportExcel(response, list, "多肽变异数据");
    }

    /**
     * 获取多肽变异详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(mutationService.selectMutationById(id));
    }

    /**
     * 新增多肽变异
     */
    @PreAuthorize("@ss.hasPermi('medical:mutation:add')")
    @Log(title = "多肽变异", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Mutation mutation)
    {
        return toAjax(mutationService.insertMutation(mutation));
    }

    /**
     * 修改多肽变异
     */
    @Log(title = "多肽变异", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Mutation mutation)
    {
        return toAjax(mutationService.updateMutation(mutation));
    }

    /**
     * 删除多肽变异
     */
    @Log(title = "多肽变异", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(mutationService.deleteMutationByIds(ids));
    }

    @PostMapping("/downGraph/{id}/{mutationId}")
    public void downGraph(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("mutationId") Long mutationId) throws Exception
    {
        // 上传文件路径
        String filePathConfig = RuoYiConfig.getUploadPath();
        Peptide peptide = peptideService.selectPeptideById(id);
        Mutation mutation = mutationService.selectMutationById(mutationId);
        String originalStructure = peptide.getOriginalStructure();
        FileBody bin = new FileBody(new File(filePathConfig + originalStructure.substring(15)));
        String result="";



        try {
            //传文件到模型
            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpSendFile = new HttpPost(uploadOriginalFileUrl);
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).build();//添加参数

            httpSendFile.setEntity(reqEntity);
            // 执行上传文件到模型操作
            CloseableHttpResponse sendRes = httpclient.execute(httpSendFile);
            sendRes.close();
            httpclient.close();
            int statusCode = sendRes.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }

            CloseableHttpClient httpclient1 = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(downloadGraphUrl);
            HttpEntity reqEntityDown = MultipartEntityBuilder.create()
                    .addTextBody("ori_pep",mutation.getOriginalPeptide())
                    .addTextBody("mut_pep", mutation.getMutatedPeptide())
                    .addTextBody("list_id", mutation.getChainNum())
                    .addTextBody("start_id", mutation.getStartNum()).build();//添加参数
            httppost.setEntity(reqEntityDown);
            CloseableHttpResponse downRes = httpclient1.execute(httppost);



            int downCode = downRes.getStatusLine().getStatusCode();
            if (downCode == HttpStatus.SC_OK) {

            } else if (downCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "生成肽结构图异常，请检查模型端异常";
            }

            HttpEntity resEntity = downRes.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonObject json = gson.fromJson(responseJson, JsonObject.class);
            String filePath = json.get("png_path").getAsString();
            // 创建一个字节输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 创建一个字节数组，并设置大小为文件长度
            byte[] imageBytes = new byte[fis.available()];
            // 将文件内容读入字节数组
            fis.read(imageBytes);
            // 关闭输入流
            fis.close();
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            OutputStream os = response.getOutputStream();
            os.write(imageBytes);
            os.flush();
            os.close();


            downRes.close();
            httpclient.close();

        }
        catch (Exception e)
        {

        }
    }

    @PostMapping("/downFile/{id}/{mutationId}")
    public void downFile(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("mutationId") Long mutationId) throws Exception
    {
        // 上传文件路径
        String filePathConfig = RuoYiConfig.getUploadPath();
        Peptide peptide = peptideService.selectPeptideById(id);
        Mutation mutation = mutationService.selectMutationById(mutationId);
        String originalStructure = peptide.getOriginalStructure();
        FileBody bin = new FileBody(new File(filePathConfig + originalStructure.substring(15)));
        String result="";



        try {
            //传文件到模型
            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpSendFile = new HttpPost(uploadOriginalFileUrl);
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).build();//添加参数

            httpSendFile.setEntity(reqEntity);
            // 执行上传文件到模型操作
            CloseableHttpResponse sendRes = httpclient.execute(httpSendFile);
            sendRes.close();
            httpclient.close();
            int statusCode = sendRes.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }

            CloseableHttpClient httpclient1 = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(downloadGraphUrl);
            HttpEntity reqEntityDown = MultipartEntityBuilder.create()
                    .addTextBody("ori_pep",mutation.getOriginalPeptide())
                    .addTextBody("mut_pep", mutation.getMutatedPeptide())
                    .addTextBody("list_id", mutation.getChainNum())
                    .addTextBody("start_id", mutation.getStartNum()).build();//添加参数
            httppost.setEntity(reqEntityDown);
            CloseableHttpResponse downRes = httpclient1.execute(httppost);



            int downCode = downRes.getStatusLine().getStatusCode();
            if (downCode == HttpStatus.SC_OK) {

            } else if (downCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "生成肽结构图异常，请检查模型端异常";
            }

            HttpEntity resEntity = downRes.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonObject json = gson.fromJson(responseJson, JsonObject.class);
            String filePath = json.get("pdb_path").getAsString();
            // 创建一个字节输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 创建一个字节数组，并设置大小为文件长度
            byte[] imageBytes = new byte[fis.available()];
            // 将文件内容读入字节数组
            fis.read(imageBytes);
            // 关闭输入流
            fis.close();
            response.setContentType("chemical/x-pdb");
            OutputStream os = response.getOutputStream();
            os.write(imageBytes);
            os.flush();
            os.close();


            downRes.close();
            httpclient.close();

        }
        catch (Exception e)
        {

        }
    }

    @PostMapping("/downTcrGraph/{id}/{mutationId}")
    public void downTcrGraph(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("mutationId") Long mutationId) throws Exception
    {
        // 上传文件路径
        String filePathConfig = RuoYiConfig.getUploadPath();
        Peptide peptide = peptideService.selectPeptideById(id);
        Mutation mutation = mutationService.selectMutationById(mutationId);
        String originalStructure = peptide.getOriginalStructure();
        FileBody bin = new FileBody(new File(filePathConfig + originalStructure.substring(15)));
        String result="";



        try {
            //传文件到模型
            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpSendFile = new HttpPost(uploadTcrOriginalFileUrl);
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).build();//添加参数

            httpSendFile.setEntity(reqEntity);
            // 执行上传文件到模型操作
            CloseableHttpResponse sendRes = httpclient.execute(httpSendFile);
            sendRes.close();
            httpclient.close();
            int statusCode = sendRes.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }

            CloseableHttpClient httpclient1 = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(downloadTcrGraphUrl);
            HttpEntity reqEntityDown = MultipartEntityBuilder.create()
                    .addTextBody("ori_pep",mutation.getOriginalPeptide())
                    .addTextBody("mut_pep", mutation.getMutatedPeptide())
                    .addTextBody("list_id", mutation.getChainNum())
                    .addTextBody("start_id", mutation.getStartNum()).build();//添加参数
            httppost.setEntity(reqEntityDown);
            CloseableHttpResponse downRes = httpclient1.execute(httppost);



            int downCode = downRes.getStatusLine().getStatusCode();
            if (downCode == HttpStatus.SC_OK) {

            } else if (downCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "生成肽结构图异常，请检查模型端异常";
            }

            HttpEntity resEntity = downRes.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonObject json = gson.fromJson(responseJson, JsonObject.class);
            String filePath = json.get("png_path").getAsString();
            // 创建一个字节输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 创建一个字节数组，并设置大小为文件长度
            byte[] imageBytes = new byte[fis.available()];
            // 将文件内容读入字节数组
            fis.read(imageBytes);
            // 关闭输入流
            fis.close();
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            OutputStream os = response.getOutputStream();
            os.write(imageBytes);
            os.flush();
            os.close();


            downRes.close();
            httpclient.close();

        }
        catch (Exception e)
        {

        }
    }

    @PostMapping("/downTcrFile/{id}/{mutationId}")
    public void downTcrFile(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("mutationId") Long mutationId) throws Exception
    {
        // 上传文件路径
        String filePathConfig = RuoYiConfig.getUploadPath();
        Peptide peptide = peptideService.selectPeptideById(id);
        Mutation mutation = mutationService.selectMutationById(mutationId);
        String originalStructure = peptide.getOriginalStructure();
        FileBody bin = new FileBody(new File(filePathConfig + originalStructure.substring(15)));
        String result="";



        try {
            //传文件到模型
            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpSendFile = new HttpPost(uploadTcrOriginalFileUrl);
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).build();//添加参数

            httpSendFile.setEntity(reqEntity);
            // 执行上传文件到模型操作
            CloseableHttpResponse sendRes = httpclient.execute(httpSendFile);
            sendRes.close();
            httpclient.close();
            int statusCode = sendRes.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }

            CloseableHttpClient httpclient1 = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(downloadTcrGraphUrl);
            HttpEntity reqEntityDown = MultipartEntityBuilder.create()
                    .addTextBody("ori_pep",mutation.getOriginalPeptide())
                    .addTextBody("mut_pep", mutation.getMutatedPeptide())
                    .addTextBody("list_id", mutation.getChainNum())
                    .addTextBody("start_id", mutation.getStartNum()).build();//添加参数
            httppost.setEntity(reqEntityDown);
            CloseableHttpResponse downRes = httpclient1.execute(httppost);



            int downCode = downRes.getStatusLine().getStatusCode();
            if (downCode == HttpStatus.SC_OK) {

            } else if (downCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "生成肽结构图异常，请检查模型端异常";
            }

            HttpEntity resEntity = downRes.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonObject json = gson.fromJson(responseJson, JsonObject.class);
            String filePath = json.get("pdb_path").getAsString();
            // 创建一个字节输入流
            FileInputStream fis = new FileInputStream(filePath);
            // 创建一个字节数组，并设置大小为文件长度
            byte[] imageBytes = new byte[fis.available()];
            // 将文件内容读入字节数组
            fis.read(imageBytes);
            // 关闭输入流
            fis.close();
            response.setContentType("chemical/x-pdb");
            OutputStream os = response.getOutputStream();
            os.write(imageBytes);
            os.flush();
            os.close();


            downRes.close();
            httpclient.close();

        }
        catch (Exception e)
        {

        }
    }



//    @PostMapping("/downGraph/{id}")
//    public void downGraph(HttpServletResponse response, @PathVariable("id") Long id) throws Exception
//    {
//        // 上传文件路径
//        String filePathConfig = RuoYiConfig.getUploadPath();
//        Peptide peptide = peptideService.selectPeptideById(id);
//        String originalStructure = peptide.getOriginalStructure();
//        FileBody bin = new FileBody(new File(filePathConfig + originalStructure.substring(15)));
//
//        Mutation mutation = new Mutation();
//        mutation.setOriginalId(id);
//        List<Mutation> list =  mutationService.selectMutationList(mutation);
//        String originalPeptideName = list.get(0).getOriginalPeptide();
//        JsonObject jsonObjectT = new JsonObject();
//        for(Mutation item : list) {
//            JsonObject jsonObjectF = new JsonObject();
//            jsonObjectF.addProperty("mutationSite",item.getChainNum());
//            jsonObjectT.add(item.getMutatedPeptide(), jsonObjectF);
//        }
//        JsonObject jsonObjectS = new JsonObject();
//        jsonObjectS.add(originalPeptideName,jsonObjectT);
//
//        try {
//            String result="";
//            // 创建http客户端
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//            HttpPost httpSendFile = new HttpPost(uploadOriginalFileUrl);
//            HttpPost httppost = new HttpPost(downloadGraphUrl);
//            HttpEntity reqEntity = MultipartEntityBuilder.create()
//                    .addTextBody("params", String.valueOf(jsonObjectS)).build();//添加参数
//            httppost.setEntity(reqEntity);
//
//            // 执行上传文件到模型操作
//            CloseableHttpResponse res = httpclient.execute(httppost);
//
//            HttpEntity resEntity = res.getEntity();
//            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
//            JsonObject json = gson.fromJson(responseJson, JsonObject.class);
//            int statusCode = res.getStatusLine().getStatusCode();
//            res.close();
//            httpclient.close();
//            if (statusCode == HttpStatus.SC_OK) {
//            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
//                result = "下载突变肽结构图出现异常，请检查模型端异常";
//            }
//            String filePath = json.get("filePath").getAsString();
//
//            // 创建一个字节输入流
//            FileInputStream fis = new FileInputStream(filePath);
//            // 创建一个字节数组，并设置大小为文件长度
//            byte[] imageBytes = new byte[fis.available()];
//            // 将文件内容读入字节数组
//            fis.read(imageBytes);
//            // 关闭输入流
//            fis.close();
//            response.setContentType(MediaType.IMAGE_PNG_VALUE);
//            OutputStream os = response.getOutputStream();
//            os.write(imageBytes);
//            os.flush();
//            os.close();
//        }
//        catch (Exception e)
//        {
//
//        }
//
//    }
}
