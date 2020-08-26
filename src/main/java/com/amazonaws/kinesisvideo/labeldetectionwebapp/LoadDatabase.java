package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.ArchivedVideoStream;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initVideoDatabase(VideoRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Video("bezos_vogels.mkv")));
            log.info("Preloading " + repository.save(new Video("clusters.mkv")));
            log.info("Preloading " + repository.save(new Video("video.mkv")));
        };
    }

    /*
    @Bean
    CommandLineRunner initStreamDatabase(ArchivedVideoStreamsRepository archivedVideoStreamsRepository) {
        return args -> {
            log.info("Preloading " + archivedVideoStreamsRepository.save(new ArchivedVideoStream("init_test_stream", "17/08/2020 09:51:33", "17/08/2020 09:52:23", 0)));
        };
    }*/

}
