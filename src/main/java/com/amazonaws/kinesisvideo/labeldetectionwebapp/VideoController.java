package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import java.io.*;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.kinesisvideo.demoapp.auth.AuthHelper;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.exceptions.VideoNotFoundException;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.PutAndGetMedia;
import com.amazonaws.kinesisvideo.parser.examples.PutMediaWorker;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.*;
import com.amazonaws.services.kinesisvideo.model.AckEvent;
import com.amazonaws.services.kinesisvideo.model.FragmentTimecodeType;
import com.amazonaws.services.kinesisvideo.model.GetDataEndpointRequest;
import com.amazonaws.services.kinesisvideo.model.PutMediaRequest;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
public class VideoController {

    private final VideoRepository videoRepository;

    VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping("/videos")
    List<Video> all() {
        return videoRepository.findAll();
    }

    @PostMapping("/videos")
    Video newVideo(@RequestBody Video newVideo) {
        return videoRepository.save(newVideo);
    }

    @GetMapping("/videos/{id}")
    Video one(@PathVariable Long id) throws IOException, InterruptedException {
        /* Update the labels and images for the video */
        Video videoToUpdate = videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException(id));

        if (videoToUpdate.getLabels().size() == 0) {

            File f = new File("/Users/kyltran/KVSProject/label-detection-web-app/src/main/resources/videosamples/clusters.mkv");
            InputStream inputStream = new FileInputStream(f);


            PutAndGetMedia putAndGetMedia = PutAndGetMedia.builder()
                    .region(Regions.US_WEST_2)
                    .streamName("kyle_archived_stream")
                    .credentialsProvider(new ProfileCredentialsProvider())
                    .sampleRate(0)
                    .inputStream(inputStream)
                    .build();

            putAndGetMedia.execute();

            for (String label : putAndGetMedia.getLabels()) {
                videoToUpdate.addLabel(label);
            }
            videoRepository.save(videoToUpdate);
        }
        return videoToUpdate;
    }
    
}
