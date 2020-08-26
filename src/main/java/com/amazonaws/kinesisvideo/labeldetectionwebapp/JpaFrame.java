package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@Entity
public class JpaFrame {

    @Id @GeneratedValue
    private Long id;

    private Long frameNumber;

    @Lob
    private byte[] imageBytes;

    private String playbackTimestamp;

    @ElementCollection
    private List<String> labels = new ArrayList<>();

    public JpaFrame() {}

    public JpaFrame(byte[] imageBytes, long frameNumber) {
        this.imageBytes = imageBytes;
        this.frameNumber = frameNumber;
    }

    public Long getFrameNumber() {
        return this.frameNumber;
    }

    public byte[] getImageBytes() {
        return this.imageBytes;
    }

    public void setPlaybackTimestamp(String playbackTimestamp) {
        this.playbackTimestamp = playbackTimestamp;
    }

    public String getPlaybackTimestamp() { return this.playbackTimestamp; }

    public List<String> getLabels() {
        return this.labels;
    }

    public void addLabel(String label) {
        this.labels.add(label);
    }
}
