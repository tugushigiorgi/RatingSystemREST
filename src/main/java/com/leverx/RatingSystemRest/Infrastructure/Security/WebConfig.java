package com.leverx.RatingSystemRest.Infrastructure.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve images from the upload directory
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/"); // Ensure the path is correct
    }
}
