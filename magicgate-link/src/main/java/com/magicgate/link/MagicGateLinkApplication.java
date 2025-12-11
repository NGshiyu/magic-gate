package com.magicgate.link;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.magicgate.common", "com.magicgate.link"})
@EnableConfigurationProperties
@ConfigurationPropertiesScan
public class MagicGateLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicGateLinkApplication.class, args);
    }

    //@Bean
    //public CommandLineRunner runner(ChatClient.Builder builder) {
    //    return args -> {
    //        ChatClient chatClient = builder.build();
    //        String response = chatClient.prompt("Tell me a joke").call().content();
    //        System.out.println(response);
    //    };
    //}

}
