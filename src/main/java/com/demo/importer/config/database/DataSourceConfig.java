//package com.demo.importer.config.database;
//
//import com.demo.importer.config.aws.KMSUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//
//    @Value("${dev.importer.datasource.url}")
//    private String url;
//    @Value("${dev.importer.datasource.username}")
//    private String username;
//    @Value("${dev.importer.datasource.driver-class-name}")
//    private String driverClassName;
//    @Value("${dev.importer.datasource.password.encrypted}")
//    private String devDbPassword;
//
//    @Autowired
//    private KMSUtil kmsUtil;
//
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl(url);
//        dataSource.setUsername(username);
//        dataSource.setPassword(kmsUtil.kmsDecrypt(devDbPassword));
//        return dataSource;
//    }
//}
//
