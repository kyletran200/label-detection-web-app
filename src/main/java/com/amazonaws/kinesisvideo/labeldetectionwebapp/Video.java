package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Data
@Entity
public class Video {

    private @Id @GeneratedValue Long id;
    private String name;
    @ElementCollection
    private Set<String> labels = new HashSet<>();

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, FrameNumberCollection> labelToFrameNums = new HashMap<String, FrameNumberCollection>();


    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<Long, JpaFrame> frameNumToFrame = new HashMap<Long, JpaFrame>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<JpaFrame> frames = new ArrayList<>();

    protected Video() {}

    Video(String name) {
        this.name = name;
    }

    public void addLabel (String label) {
        this.labels.add(label);
    }

    public void addLabelAndFrameNum (String label, Long frameNumber) {
        this.labelToFrameNums.putIfAbsent(label, new FrameNumberCollection());
        this.labelToFrameNums.get(label).addFrameNumber(frameNumber);
    }

    public void addFrameNumAndFrame (Long frameNumber, JpaFrame jpaFrame) {
        this.frameNumToFrame.put(frameNumber, jpaFrame);
    }

    public void addFrame (JpaFrame jpaFrame) {
        this.frames.add(jpaFrame);
    }

    public void sortFrames() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Collections.sort(this.frames, (f1, f2) -> LocalDateTime.parse(f1.getPlaybackTimestamp(), formatter).
                compareTo(LocalDateTime.parse(f2.getPlaybackTimestamp(), formatter)));
    }

}
