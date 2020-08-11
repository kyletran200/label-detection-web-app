package com.amazonaws.kinesisvideo.labeldetectionwebapp;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices.PutAndGetMedia;
import com.amazonaws.regions.Regions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class LabelDetectionWebAppApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(LabelDetectionWebAppApplication.class, args);
		
	}

}
