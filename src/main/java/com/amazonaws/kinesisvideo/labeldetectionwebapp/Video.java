package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import lombok.Data;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.util.*;


@Data
@Entity
public class Video {

    private @Id @GeneratedValue Long id;
    private String name;
    @ElementCollection
    private Set<String> labels = new HashSet<>();

    /*
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private List<ByteArrayOutputStream> imageFrames = new ArrayList<>();

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private Map<String, List<ByteArrayOutputStream>> labelToFrame = new HashMap<>();*/

    protected Video() {}

    Video(String name) {
        this.name = name;
    }

    public void addLabel (String label) {
        this.labels.add(label);
    }


}
