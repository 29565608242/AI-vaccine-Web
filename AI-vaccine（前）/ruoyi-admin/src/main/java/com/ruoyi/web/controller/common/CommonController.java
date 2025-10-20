package com.ruoyi.web.controller.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.medical.domain.Peptide;
import com.ruoyi.medical.service.IPeptideService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import org.apache.http.entity.mime.content.FileBody;
import com.google.gson.*;
/**
 * 通用请求处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;

    private static final String FILE_DELIMETER = ",";

    @Value("${model.uploadUrl}")
    private String modelUploadUrl;

    @Value("${model.tcrUploadUrl}")
    private String tcrUploadUrl;

    private static Gson gson = new Gson();

    @Autowired
    private IPeptideService peptideService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/myUpload/{modelId}")
    public AjaxResult myUploadFile(MultipartFile file,@PathVariable("modelId") Long modelId) throws Exception
    {
        try
        {
            String result="";
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传文件到本地并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;

            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(modelUploadUrl + modelId);

            FileBody bin = new FileBody(new File(filePath + fileName.substring(15)));

            //添加附件参数，一定要要是文件格式，否则字符串文件流Python无法解析
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).build();//添加参数
            httppost.setEntity(reqEntity);
            // 执行上传文件到模型操作
            CloseableHttpResponse response = httpclient.execute(httppost);

            HttpEntity resEntity = response.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonArray json = gson.fromJson(responseJson, JsonArray.class);
            System.out.println(json);

            for (JsonElement element : json) {
                JsonObject jsonObject = element.getAsJsonObject();
                String hla = jsonObject.get("HLA").getAsString();
                String peptide = jsonObject.get("peptide").getAsString();
                JsonArray xAxis = jsonObject.get("xAxis").getAsJsonArray();
                JsonArray yAxis = jsonObject.get("yAxis").getAsJsonArray();
                Float max = jsonObject.get("max").getAsFloat();
                Float min = jsonObject.get("min").getAsFloat();
                Float yPred = jsonObject.get("y_pred").getAsFloat();
                Float yProb = Float.valueOf(jsonObject.get("y_prob").getAsString());
                String chainNum = jsonObject.get("listid").getAsString();
                String startNum = jsonObject.get("startid").getAsString();
                JsonArray data = jsonObject.get("data").getAsJsonArray();
                Peptide peptideObj = new Peptide();
                peptideObj.setHla(hla);
                peptideObj.setPeptide(peptide);
                peptideObj.setxAxis(xAxis.toString());
                peptideObj.setyAxis(yAxis.toString());
                peptideObj.setMaxValue(max);
                peptideObj.setMinValue(min);
                peptideObj.setThermodynamic(data.toString().substring(1,data.toString().length()-1));
                peptideObj.setyPred(yPred);
                peptideObj.setyProb(yProb);
                peptideObj.setChainNum(chainNum);
                peptideObj.setStartNum(startNum);
                peptideObj.setModelType(modelId);
                peptideObj.setIsTcr(0);
                peptideService.insertPeptide(peptideObj);
                String key = "data" + peptideObj.getId();
//                redisCache.setCacheObject(key, data.toString().substring(1,data.toString().length()-1));
            }
            int statusCode = response.getStatusLine().getStatusCode();
            response.close();
            httpclient.close();

            if (statusCode == HttpStatus.SC_OK) {
                System.out.println(resEntity);

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/myUploadTcr/{modelId}")
    public AjaxResult myUploadFileTcr(MultipartFile file,@PathVariable("modelId") Long modelId) throws Exception
    {
        try
        {
            String result="";
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传文件到本地并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;

            // 创建http客户端
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(tcrUploadUrl + modelId);

            FileBody bin = new FileBody(new File(filePath + fileName.substring(15)));

            //添加附件参数，一定要要是文件格式，否则字符串文件流Python无法解析
            HttpEntity reqEntity = MultipartEntityBuilder.create().
                    addPart("file", bin).build();//添加参数
            httppost.setEntity(reqEntity);
            // 执行上传文件到模型操作
            CloseableHttpResponse response = httpclient.execute(httppost);

            HttpEntity resEntity = response.getEntity();
            String responseJson = EntityUtils.toString(resEntity, "UTF-8");
            JsonArray json = gson.fromJson(responseJson, JsonArray.class);
            System.out.println(json);

            for (JsonElement element : json) {
                JsonObject jsonObject = element.getAsJsonObject();
                String hla = jsonObject.get("TCR").getAsString();
                String peptide = jsonObject.get("peptide").getAsString();
                JsonArray xAxis = jsonObject.get("xAxis").getAsJsonArray();
                JsonArray yAxis = jsonObject.get("yAxis").getAsJsonArray();
                Float max = jsonObject.get("max").getAsFloat();
                Float min = jsonObject.get("min").getAsFloat();
                Float yPred = jsonObject.get("y_pred").getAsFloat();
                Float yProb = Float.valueOf(jsonObject.get("y_prob").getAsString());
                String chainNum = jsonObject.get("listid").getAsString();
                String startNum = jsonObject.get("startid").getAsString();
                JsonArray data = jsonObject.get("data").getAsJsonArray();
                Peptide peptideObj = new Peptide();
                peptideObj.setHla(hla);
                peptideObj.setPeptide(peptide);
                peptideObj.setxAxis(xAxis.toString());
                peptideObj.setyAxis(yAxis.toString());
                peptideObj.setMaxValue(max);
                peptideObj.setMinValue(min);
                peptideObj.setThermodynamic(data.toString().substring(1,data.toString().length()-1));
                peptideObj.setyPred(yPred);
                peptideObj.setyProb(yProb);
                peptideObj.setChainNum(chainNum);
                peptideObj.setStartNum(startNum);
                peptideObj.setModelType(modelId);
                peptideObj.setIsTcr(1);
                peptideService.insertPeptide(peptideObj);
                String key = "data" + peptideObj.getId();
//                redisCache.setCacheObject(key, data.toString().substring(1,data.toString().length()-1));
            }
            int statusCode = response.getStatusLine().getStatusCode();
            response.close();
            httpclient.close();

            if (statusCode == HttpStatus.SC_OK) {
                System.out.println(resEntity);

            } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                result = "上传文件到模型出现异常，请检查模型端异常";
            }
            return AjaxResult.success(result);
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception
    {
        try
        {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            for (MultipartFile file : files)
            {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }
}
