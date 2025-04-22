package com.leverx.RatingSystemRest.Infrastructure.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to map static resources such as uploaded images to accessible URLs.
 *
 * <p>This class implements {@link WebMvcConfigurer} and overrides {@code addResourceHandlers}
 * to expose a local file system path (e.g., for uploaded user images) as a web-accessible resource
 * under the "/images/**" URL path.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  /**
   * Directory where uploaded files are stored, injected from the application properties.
   */
  @Value("${file.upload-dir}")
  private String uploadDir;

  /**
   * Maps the "/images/**" URL pattern to the file system location defined by {@code uploadDir}.
   * @param registry the {@link ResourceHandlerRegistry} used to add resource handlers
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**")
        .addResourceLocations("file:" + uploadDir + "/");
  }
}
