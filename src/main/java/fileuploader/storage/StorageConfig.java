package fileuploader.storage;

import fileuploader.storage.interfaces.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    private final ApplicationContext context;

    @Autowired
    public StorageConfig(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public StorageService DefaultStorageService(@Value("${storage.type}") String qualifier) {
        BaseStorageService bean = context.getBean(qualifier, BaseStorageService.class);
        bean.init();
        return bean;
    }

}
