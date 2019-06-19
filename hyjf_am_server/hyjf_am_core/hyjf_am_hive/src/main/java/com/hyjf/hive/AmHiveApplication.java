package com.hyjf.hive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyjf.hive.config.HiveDruidConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
@ServletComponentScan
@EnableConfigurationProperties({ HiveDruidConfig.class })
@ComponentScan(basePackages = {"com.hyjf.hive","com.hyjf.common"})
public class AmHiveApplication {

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

    @Bean(name = "rocketMQMessageObjectMapper")
    public ObjectMapper jacksonObjectMapper(@Autowired Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build();
    }



	public static void main(String[] args) {
		SpringApplication.run(AmHiveApplication.class, args);
	}
}
