package com.hyjf.callcenter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@EnableDiscoveryClient
@ComponentScan(basePackages = "com.hyjf")
public class HyjfCallcenterApplication {
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
		//不用了
		//SpringApplication.run(HyjfCallcenterApplication.class, args);
	}
}
