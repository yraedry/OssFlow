package com.ossflow;

import com.ossflow.shared.properties.AppProperties;
import com.ossflow.shared.properties.FlywayMigrationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, FlywayMigrationProperties.class})
public class OssflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(OssflowApplication.class, args);
	}

}
