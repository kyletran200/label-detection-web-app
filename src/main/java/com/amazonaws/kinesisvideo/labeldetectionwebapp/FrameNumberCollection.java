package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class FrameNumberCollection {

    @Id @GeneratedValue
    private Long id;

    @ElementCollection
    private List<Long> frameNumbers = new ArrayList<>();

    public FrameNumberCollection() {}

    public List<Long> getFrameNumbers() {
        return frameNumbers;
    }

    public void addFrameNumber(Long i) {
        frameNumbers.add(i);
    }
}
