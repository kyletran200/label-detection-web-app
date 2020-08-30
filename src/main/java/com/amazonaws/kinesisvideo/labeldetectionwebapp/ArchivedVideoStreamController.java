package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.exceptions.ArchivedVideoStreamNotFoundException;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.ArchivedVideoStream;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.GetArchivedMedia;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@CrossOrigin("*")
public class ArchivedVideoStreamController {

    private final ArchivedVideoStreamsRepository archivedVideoStreamsRepository;

    ArchivedVideoStreamController(ArchivedVideoStreamsRepository archivedVideoStreamsRepository) {
        this.archivedVideoStreamsRepository = archivedVideoStreamsRepository;
    }

    @GetMapping("/streams")
    List<ArchivedVideoStream> all() { return archivedVideoStreamsRepository.findAll(); }

    @PostMapping("/streams")
    ArchivedVideoStream archivedVideoStream(@RequestBody ArchivedVideoStream newArchivedVideoStream) {

        log.info("Just saved a new video stream");
        log.info(newArchivedVideoStream.toString());

        return archivedVideoStreamsRepository.save(newArchivedVideoStream);
    }

    @GetMapping("/streams/{id}")
    ArchivedVideoStream one(@PathVariable Long id) throws IOException, InterruptedException, ParseException, ExecutionException {

        ArchivedVideoStream streamToUpdate = archivedVideoStreamsRepository.findById(id)
                .orElseThrow(() -> new ArchivedVideoStreamNotFoundException(id));

        log.info("Performing Get Media on Archived Stream: {}", streamToUpdate.getName());

        if (streamToUpdate.getLabels().size() == 0) {
            String startTimestamp = streamToUpdate.getStartTimestamp();
            String endTimestamp = streamToUpdate.getEndTimestamp();
            String streamName = streamToUpdate.getName();
            int threads = streamToUpdate.getThreads();
            int sampleRate = streamToUpdate.getSampleRate();

            TimestampRange timestampRange = new TimestampRange();
            try {
                timestampRange.setStartTimestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(startTimestamp));
                timestampRange.setEndTimestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(endTimestamp));
            } catch (java.text.ParseException e) {
                log.error(e.getMessage());
                System.exit(1);
            }

            long timeDuration = timestampRange.getEndTimestamp().getTime() - timestampRange.getStartTimestamp().getTime();
            int tasks = (int) timeDuration / 10000;
            log.info("Starting processing with {} tasks and {} threads on {}", tasks, threads, streamName);

            GetArchivedMedia getArchivedMedia = GetArchivedMedia.builder()
                    .region(Regions.US_WEST_2)
                    .streamName(streamName)
                    .awsCredentialsProvider(new ProfileCredentialsProvider())
                    .sampleRate(sampleRate)
                    .tasks(tasks)
                    .threads(threads)
                    .timestampRange(timestampRange)
                    .build();

            getArchivedMedia.execute();

            for (String label : getArchivedMedia.getLabels()) {
                streamToUpdate.addLabel(label);
            }

            getArchivedMedia.getLabelToTimestamps().forEach((k, v) -> streamToUpdate.addLabelAndTimestampCollection(k, v));
            getArchivedMedia.getFrames().forEach((k, v) -> streamToUpdate.addFrame(k));

            streamToUpdate.sortFrames();
            archivedVideoStreamsRepository.save(streamToUpdate);
        }
        return streamToUpdate;
    }
}
