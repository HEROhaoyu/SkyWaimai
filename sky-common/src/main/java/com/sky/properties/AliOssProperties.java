package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss") //加上这个注解，表示他是一个属性类。在运行时会读取配置文件，为属性赋值
@Data
public class AliOssProperties {

    private String endpoint;
//    private String accessKeyId;
//    private String accessKeySecret;
    private String bucketName;

}
