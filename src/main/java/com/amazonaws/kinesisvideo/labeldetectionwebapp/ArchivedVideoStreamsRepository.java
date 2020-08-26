package com.amazonaws.kinesisvideo.labeldetectionwebapp;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.ArchivedVideoStream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedVideoStreamsRepository extends JpaRepository <ArchivedVideoStream, Long> {
}
