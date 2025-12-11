package com.magicgate.link.domain.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @Author yangyangsheep
 * @Description 提供者配置
 * @CreateTime 2025/6/30 00:00
 */
@Setter
@Getter
@ConfigurationProperties("llm")
public class LLMProviderProperties {

    Map<String, ProviderConfig> providers;

    @Getter
    @Setter
    public static class ProviderConfig {
        private List<String> models;
        private String apiKey;
        private String baseUrl;
        /**
         * 支持多态的模型
         */
        private List<String> visionModels;
        /**
         * 多态模型支持的文件类型；
         */
        private List<String> visionFileType;

        /**
         * 价格(￥)
         */
        private Map<String, String> price;

    }
}
