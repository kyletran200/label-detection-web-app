package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
