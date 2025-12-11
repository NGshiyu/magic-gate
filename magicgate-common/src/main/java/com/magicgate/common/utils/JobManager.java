package com.magicgate.common.utils;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author yangyangsheep
 * @Description 职责文件管理
 * @CreateTime 2025/6/29 01:12
 * </br>
 * <font color="red">后期使用Nacos动态配置管理</font>
 */
@Component
public class JobManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    // 用来存储 job 文件的内容
    private static final Map<String, String> jobDescriptions = new HashMap<>();

    // 使用 @PostConstruct 确保在 Spring 启动后初始化
    @PostConstruct
    public void init() throws IOException {
        initialize();
    }


    // 使用 MyBatis 风格的路径加载文件
    public static void initialize() throws IOException {
        // 使用 PathMatchingResourcePatternResolver 来加载类路径中的文件
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 使用类似 MyBatis 的方式查找所有匹配的资源文件（*.job）
        Resource[] resources = resolver.getResources("classpath*:/job/**/*.job");

        for (Resource resource : resources) {
            String content = readJobFile(resource);
            jobDescriptions.put(Objects.requireNonNull(resource.getFilename()).replace(".job", ""), content);
        }
        LOGGER.info("------------JobManager Initialization successful,Current Jobs Descriptions：{}------------", JSON.toJSONString(jobDescriptions.keySet()));
    }

    // 读取文件内容的方法
    private static String readJobFile(Resource resource) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // 获取指定 job 文件的内容
    public static String getJobDescription(String jobName) {
        return jobDescriptions.getOrDefault(jobName, "");
    }
}