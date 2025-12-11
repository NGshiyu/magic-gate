package com.magicgate.rag.controller;

import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.AddFileResponse;
import com.aliyun.bailian20231229.models.ApplyFileUploadLeaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author yangyangsheep
 * @Description 查询使用的Rag测试
 * @CreateTime 2025/7/2 20:33
 */
@RestController
@Slf4j
public class RagQueryController {

    @Autowired
    @Qualifier("baiLianClient")
    private Client balianClient;


    @GetMapping("/rag")
    public void importDocuments() throws Exception {
        String filePath = "/Users/yangyangsheep/Downloads/测试上传.docx";

        // 计算文件的MD5值
        String md5Value = calculateMD5(filePath);
        // 获取文件大小
        String fileSize = String.valueOf(new File(filePath).length());
        String cateId = "cate_xxxxxxx";
        String workSpaceId = "llm-xxxxxx";
        // 申请文档上传租约
        ApplyFileUploadLeaseResponse applyFileUploadLeaseResponse = applyLease(balianClient, cateId, "测试上传.docx", md5Value, fileSize, workSpaceId);
        String xbailian = ((Map<String, Object>) applyFileUploadLeaseResponse.getBody().getData().getParam().getHeaders()).get("X-bailian-extra").toString();
        String contentType = ((Map<String, Object>) applyFileUploadLeaseResponse.getBody().getData().getParam().getHeaders()).get("Content-Type").toString();
        String preSignedUrlOrHttpUrl = applyFileUploadLeaseResponse.getBody().getData().getParam().getUrl();
        String leaseId = applyFileUploadLeaseResponse.getBody().getData().getFileUploadLeaseId().toString();
        uploadFile(preSignedUrlOrHttpUrl, xbailian, contentType, filePath);

        addFile(balianClient, leaseId, "DASHSCOPE_DOCMIND", cateId, workSpaceId);
    }

    private static void uploadFile(String preSignedUrlOrHttpUrl, String xbailian, String contentType, String filePath) {
        HttpURLConnection connection = null;
        try {
            // 创建URL对象
            URL url = new URL(preSignedUrlOrHttpUrl);
            connection = (HttpURLConnection) url.openConnection();
            // 设置请求方法用于文档上传，需与您在上一步中调用ApplyFileUploadLease接口实际返回的Data.Param中Method字段的值一致
            connection.setRequestMethod("PUT");
            // 允许向connection输出，因为这个连接是用于上传文档的
            connection.setDoOutput(true);
            connection.setRequestProperty("X-bailian-extra", xbailian);
            connection.setRequestProperty("Content-Type", contentType);
            // 读取文档并通过连接上传
            try (DataOutputStream outStream = new DataOutputStream(connection.getOutputStream()); FileInputStream fileInputStream = new FileInputStream(filePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.flush();
            }
            // 检查响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 文档上传成功处理
                System.out.println("File uploaded successfully.");
            }
            else {
                // 文档上传失败处理
                System.out.println("Failed to upload the file. ResponseCode: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 计算文件的MD5值
     *
     * @param filePath 文件路径
     *
     * @return MD5值
     */
    private String calculateMD5(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
        }

        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    /**
     * @param client
     * @param categoryId
     * @param fileName
     * @param fileMd5
     * @param fileSize
     * @param workspaceId
     *
     * @return {@link ApplyFileUploadLeaseResponse }
     *
     * @throws Exception
     */
    public ApplyFileUploadLeaseResponse applyLease(com.aliyun.bailian20231229.Client client, String categoryId, String fileName, String fileMd5, String fileSize,
                                                   String workspaceId) throws Exception {
        Map<String, String> headers = new HashMap<>();
        com.aliyun.bailian20231229.models.ApplyFileUploadLeaseRequest applyFileUploadLeaseRequest = new com.aliyun.bailian20231229.models.ApplyFileUploadLeaseRequest();
        applyFileUploadLeaseRequest.setFileName(fileName);
        applyFileUploadLeaseRequest.setMd5(fileMd5);
        applyFileUploadLeaseRequest.setSizeInBytes(fileSize);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        ApplyFileUploadLeaseResponse applyFileUploadLeaseResponse = null;
        applyFileUploadLeaseResponse = client.applyFileUploadLeaseWithOptions(categoryId, workspaceId, applyFileUploadLeaseRequest, headers, runtime);
        return applyFileUploadLeaseResponse;
    }

    /**
     * 将文档添加到类目中。
     *
     * @param client      客户端对象
     * @param leaseId     租约ID
     * @param parser      用于文档的解析器
     * @param categoryId  类目ID
     * @param workspaceId 业务空间ID
     *
     * @return 阿里云百炼服务的响应对象
     */
    public AddFileResponse addFile(com.aliyun.bailian20231229.Client client, String leaseId, String parser, String categoryId, String workspaceId) throws Exception {
        Map<String, String> headers = new HashMap<>();
        com.aliyun.bailian20231229.models.AddFileRequest addFileRequest = new com.aliyun.bailian20231229.models.AddFileRequest();
        addFileRequest.setLeaseId(leaseId);
        addFileRequest.setParser(parser);
        addFileRequest.setCategoryId(categoryId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        return client.addFileWithOptions(workspaceId, addFileRequest, headers, runtime);
    }
}
