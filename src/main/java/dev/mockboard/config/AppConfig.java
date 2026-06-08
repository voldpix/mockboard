package dev.mockboard.config;

import dev.mockboard.Constants;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean(destroyMethod = "close")
    public DB mapDb() {
        var file = new File(Constants.STORE_PATH);
        var parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        return DBMaker
                .fileDB(file)
                .fileMmapEnableIfSupported()
                .transactionEnable()
                .make();
    }
}
