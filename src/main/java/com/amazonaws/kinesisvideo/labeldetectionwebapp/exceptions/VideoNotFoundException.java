package com.amazonaws.kinesisvideo.labeldetectionwebapp.exceptions;

public class VideoNotFoundException extends RuntimeException {

    public VideoNotFoundException(Long id) {
        super("Could not find video " + id);
    }
}
