package com.vst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// *** ADD THESE TWO LINES ***
@EntityScan("com.vst.entity")
@EnableJpaRepositories("com.vst.repository")
public class VirtualStockTraderApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualStockTraderApplication.class, args);
	}
	

}

