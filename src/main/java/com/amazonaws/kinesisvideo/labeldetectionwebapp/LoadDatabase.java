package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(VideoRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Video("bezos_vogels.mkv")));
            log.info("Preloading " + repository.save(new Video("clusters.mkv")));
            log.info("Preloading " + repository.save(new Video("vogels_480.mkv")));
        };
    }
}
