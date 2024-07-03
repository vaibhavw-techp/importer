package com.demo.importer.config.database;

import com.demo.importer.config.aws.KMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class DataSourceConfig {

    @Value("${dev.importer.datasource.url}")
    private String url;
    @Value("${dev.importer.datasource.username}")
    private String username;
    @Value("${dev.importer.datasource.driver-class-name}")
    private String driverClassName;

    @Autowired
    private KMSUtil kmsUtil;
    @Autowired
    private ResourceLoader dbPasswordLoader;

    @Bean
    public DataSource dataSource() throws IOException {
        Resource dbPasswordResource = dbPasswordLoader.getResource("classpath:dbPassword.txt");
        String fileDbPasswordEncrypted = new String(dbPasswordResource.getInputStream().readAllBytes());
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(kmsUtil.kmsDecrypt(fileDbPasswordEncrypted));
        return dataSource;
    }
}

