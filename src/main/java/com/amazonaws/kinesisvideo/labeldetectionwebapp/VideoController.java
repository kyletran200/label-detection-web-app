package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import java.io.*;
import java.util.List;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.exceptions.VideoNotFoundException;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.PutAndGetMedia;
import com.amazonaws.regions.Regions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
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

            String filePath = "/Users/kyltran/KVSProject/label-detection-web-app/src/main/resources/videosamples/";
            String videoName;

            switch(id.intValue()) {
                case 1:
                    videoName = "bezos_vogels.mkv";
                    break;
                case 2:
                    videoName = "clusters.mkv";
                    break;
                case 3:
                    videoName = "video.mkv";
                    break;
                default:
                    videoName="";
                    break;
            }


            File f = new File(filePath + videoName);
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

            System.out.println("NUMBER OF FRAMES IS: ");
            System.out.println(putAndGetMedia.getFrames().size());
            /*
            for (JpaFrame jpaFrame : putAndGetMedia.getFrames()) {
                videoToUpdate.addFrame(jpaFrame);
            }*/
            putAndGetMedia.getFrames().forEach((k, v) -> videoToUpdate.addFrame(k));

            /*
            videoToUpdate.copyFrameNumToFrame(putAndGetMedia.getFrameNumToFrame());
            videoToUpdate.copyLabelToFrame(putAndGetMedia.getLabelToFrames());
            */
            log.info("Size of frames map is: {}", putAndGetMedia.getFrames().size());
            videoRepository.save(videoToUpdate);

        }
        return videoToUpdate;
    }
    
}
