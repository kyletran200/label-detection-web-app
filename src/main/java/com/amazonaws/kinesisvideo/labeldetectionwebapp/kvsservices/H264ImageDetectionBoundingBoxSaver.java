package com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

import com.amazonaws.kinesisvideo.parser.examples.KinesisVideoFrameViewer;
import com.amazonaws.kinesisvideo.parser.mkv.Frame;
import com.amazonaws.kinesisvideo.parser.mkv.FrameProcessException;
import com.amazonaws.kinesisvideo.parser.utilities.*;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import static com.amazonaws.kinesisvideo.parser.utilities.BufferedImageUtil.addTextToImage;


import javax.imageio.ImageIO;

@Slf4j
public class H264ImageDetectionBoundingBoxSaver extends H264FrameDecoder {

    //private final KinesisVideoFrameViewer kinesisVideoFrameViewer;
    private final int sampleRate;
    private Set<String> labels;

    private int frame_no;

    private H264ImageDetectionBoundingBoxSaver(final int sampleRate, Set<String> labels) {
        //super(kinesisVideoFrameViewer);
        //this.kinesisVideoFrameViewer = kinesisVideoFrameViewer;
        this.sampleRate = sampleRate;
        this.labels = labels;
    }

    public static H264ImageDetectionBoundingBoxSaver create(final int sampleRate, Set<String> labels) {
        return new H264ImageDetectionBoundingBoxSaver(sampleRate, labels);
    }

    @Override
    public void process(Frame frame, MkvTrackMetadata trackMetadata, Optional<FragmentMetadata> fragmentMetadata,
                        Optional<FragmentMetadataVisitor.MkvTagProcessor> tagProcessor) throws FrameProcessException {

        boolean isKeyFrame = frame.isKeyFrame();
        BufferedImage bufferedImage = decodeH264Frame(frame, trackMetadata);

        /* Only send key frames to Rekognition */
        if (sampleRate == 0) {
            if (isKeyFrame) {
                saveFrame(bufferedImage);
            }
        }
        else {
            /* Only send to Rekognition every N frames */
            if ((frame_no % sampleRate) == 0) {
                saveFrame(bufferedImage);
            }
            frame_no++;
        }
    }

    public void saveFrame(final BufferedImage bufferedImage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, "png", outputStream);
            ByteBuffer imageBytes = ByteBuffer.wrap(outputStream.toByteArray());
            List<BoundingBox> boundingBoxes = obtainBoundingBoxes(imageBytes, bufferedImage);


            for (BoundingBox boundingBox: boundingBoxes) {
                addBoundingBoxToImage(bufferedImage, boundingBox);
            }
            //kinesisVideoFrameViewer.update(bufferedImage);
        }
        catch (IOException e) {
            log.warn("Error with png conversion", e);
            System.out.println("Error with byte buffer conversion");
        }
    }

    public List<BoundingBox> obtainBoundingBoxes(ByteBuffer imageBytes, BufferedImage bufferedImage) {
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.defaultClient();

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withBytes(imageBytes))
                .withMaxLabels(10)
                .withMinConfidence(77F);

        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();
            List<BoundingBox> boundingBoxes = new ArrayList<>();

            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            System.out.println("Detected Labels:");
            for (Label label: labels) {
                System.out.println(label.getName() + ": " + label.getConfidence().toString());
                this.labels.add(label.getName());
            }
            System.out.println("----------------------");

            for (Label label: labels) {
                for (Instance instance: label.getInstances()) {
                    boundingBoxes.add(instance.getBoundingBox());
                    final int left = (int) (instance.getBoundingBox().getLeft() * width);
                    final int top = (int) (instance.getBoundingBox().getTop() * height);
                    addTextToImage(bufferedImage, label.getName(), left, top);
                }
            }

            return boundingBoxes;

        } catch (AmazonRekognitionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addBoundingBoxToImage(@NonNull BufferedImage bufferedImage, BoundingBox boundingBox) {
        Graphics graphics = bufferedImage.getGraphics();

        graphics.setColor(Color.RED);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        final int left = (int) (boundingBox.getLeft() * width);
        final int top = (int) (boundingBox.getTop() * height);
        final int bbWidth = (int) (boundingBox.getWidth() * width);
        final int bbHeight = (int) (boundingBox.getHeight() * height);

        graphics.drawRect(left, top, bbWidth, bbHeight);
    }
}