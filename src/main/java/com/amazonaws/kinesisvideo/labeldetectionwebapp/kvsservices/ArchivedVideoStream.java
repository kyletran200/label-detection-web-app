package com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices;

import com.amazonaws.kinesisvideo.labeldetectionwebapp.JpaFrame;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.TimestampCollection;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@Entity
public class ArchivedVideoStream {

    private @Id @GeneratedValue Long id;

    @ElementCollection
    private Set<String> labels = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<JpaFrame> frames = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, TimestampCollection> labelToTimestamps = new HashMap<>();

    private String name;

    private String startTimestamp;
    private String endTimestamp;
    private int sampleRate;

    private int tasks;
    private int threads;

    protected ArchivedVideoStream() {}

    public ArchivedVideoStream(String name, String startTimestamp, String endTimestamp, int sampleRate) {
        this.name = name;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.sampleRate = sampleRate;
    }

    public void addLabel (String label) {
        this.labels.add(label);
    }

    public void addFrame (JpaFrame jpaFrame) {
        this.frames.add(jpaFrame);
    }

    public void addLabelAndTimestampCollection (String label, TimestampCollection timestampCollection) {
        this.labelToTimestamps.put(label, timestampCollection);
    }

    public void sortFrames() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Collections.sort(this.frames, (f1, f2) -> LocalDateTime.parse(f1.getPlaybackTimestamp(), formatter).
                compareTo(LocalDateTime.parse(f2.getPlaybackTimestamp(), formatter)));
    }


}
