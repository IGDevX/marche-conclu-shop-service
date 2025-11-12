package org.igdevx.shopservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "org.igdevx.shopservice.elasticsearch.repositories")
@EnableRetry
public class ShopServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopServiceApplication.class, args);
	}

}
