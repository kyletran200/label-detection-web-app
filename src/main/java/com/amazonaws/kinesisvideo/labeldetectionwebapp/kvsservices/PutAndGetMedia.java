package com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.JpaFrame;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.FrameNumberCollection;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.TimestampCollection;
import com.amazonaws.kinesisvideo.parser.examples.GetMediaWorker;
import com.amazonaws.kinesisvideo.parser.examples.KinesisVideoCommon;
import com.amazonaws.kinesisvideo.parser.examples.PutMediaWorker;
import com.amazonaws.kinesisvideo.parser.examples.StreamOps;
import com.amazonaws.kinesisvideo.parser.utilities.FragmentMetadataVisitor;
import com.amazonaws.kinesisvideo.parser.utilities.FrameVisitor;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.model.StartSelector;
import com.amazonaws.services.kinesisvideo.model.StartSelectorType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PutAndGetMedia extends KinesisVideoCommon {

    private static final String DEFAULT_REGION = "us-west-2";
    private static final String PUT_MEDIA_API = "/putMedia";
    private static final String STREAM_NAME = "my-stream";
    private static final long MAX_BANDWIDTH_KBPS = 15360L;
    private static final int READ_TIMEOUT_IN_MILLIS = 1000000;
    private static final int CONNECTION_TIMEOUT_IN_MILLIS = 10000;
    private final int awaitTerminationTime = 180;

    private final InputStream inputStream;
    private final StreamOps streamOps;
    private final ExecutorService executorService;
    private final int sampleRate;
    private PutAndGetMedia.GetMediaProcessingArguments getMediaProcessingArguments;

    @Builder
    private PutAndGetMedia(Regions region,
                           String streamName,
                           AWSCredentialsProvider credentialsProvider,
                           InputStream inputStream,
                           int sampleRate) {
        super(region, credentialsProvider, streamName);

        this.inputStream = inputStream;
        this.streamOps = new StreamOps(region, streamName, credentialsProvider);
        this.executorService = Executors.newFixedThreadPool(2);
        this.sampleRate = sampleRate;
    }

    public void execute() throws InterruptedException, IOException {
        streamOps.createStreamIfNotExist();

        getMediaProcessingArguments = new PutAndGetMedia.GetMediaProcessingArguments(Optional.empty(), sampleRate);

        PutMediaWorker putMediaWorker = PutMediaWorker.create(getRegion(),
                getCredentialsProvider(),
                getStreamName(),
                inputStream,
                streamOps.getAmazonKinesisVideo());
        executorService.submit(putMediaWorker);

        GetMediaWorker getMediaWorker = GetMediaWorker.create(getRegion(),
                getCredentialsProvider(),
                getStreamName(),
                new StartSelector().withStartSelectorType(StartSelectorType.NOW),
                streamOps.getAmazonKinesisVideo(),
                getMediaProcessingArguments.getFrameVisitor());

        executorService.submit(getMediaWorker);

        executorService.shutdown();
        executorService.awaitTermination(awaitTerminationTime, TimeUnit.SECONDS);
        if (!executorService.isTerminated()) {
            log.warn("Shutting down executor service by force");
            executorService.shutdownNow();
        } else {
            log.info("Executor service is shutdown");
        }
        
        for (String label: getMediaProcessingArguments.getLabels()) {
            System.out.println(label);
        }
    }

    private static class GetMediaProcessingArguments {
        @Getter private final FrameVisitor frameVisitor;
        @Getter private Set<String> labels = new HashSet<>();
        @Getter private Map<JpaFrame, JpaFrame> jpaFrames = new HashMap<>();
        @Getter private Map<String, TimestampCollection> labelToTimestamps = new HashMap<>();

        GetMediaProcessingArguments(Optional<FragmentMetadataVisitor.MkvTagProcessor> tagProcessor, int sampleRate) {
            this.frameVisitor = FrameVisitor.create(H264ImageDetectionBoundingBoxSaver.create(sampleRate, getLabels(), getJpaFrames(), getLabelToTimestamps()));
        }
    }

    public Set<String> getLabels() {
        return getMediaProcessingArguments.getLabels();
    }

    public Map<JpaFrame, JpaFrame> getFrames() { return getMediaProcessingArguments.getJpaFrames(); }

}
