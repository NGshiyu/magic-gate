package com.magicgate.rag.config;

import com.aliyun.bailian20231229.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yangyangsheep
 * @Description bean注入
 * @CreateTime 2025/7/2 21:25
 */
@Configuration
public class BaiLianAutoconfiguration {


    @Value("${bailian.endpoint}")
    String endpoint;

    /**
     * 百炼调用时需要配置 DashScope API，对 dashScopeApi 强依赖。
     *
     * @return
     */
    @Bean("baiLianClient")
    public Client createClient() throws Exception {
        //Create and initialize a Config instance.
        Config authConfig = new Config();
        authConfig.accessKeyId = "xxxxxxxxxxx";
        authConfig.accessKeySecret = "xxxxxxxxxxxxxxxxx";
        authConfig.endpoint = endpoint;
        authConfig.regionId = "cn-beijing";
        return new Client(authConfig);
    }

}
