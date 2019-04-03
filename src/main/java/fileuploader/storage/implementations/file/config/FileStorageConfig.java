package fileuploader.storage.implementations.file.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FileSystemStorageProperties.class)
public class FileStorageConfig {
}
