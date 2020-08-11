package com.amazonaws.kinesisvideo.labeldetectionwebapp.kvsservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;

import com.amazonaws.kinesisvideo.parser.ebml.InputStreamParserByteSource;
import com.amazonaws.kinesisvideo.parser.examples.KinesisVideoCommon;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElement;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitException;
import com.amazonaws.kinesisvideo.parser.mkv.MkvElementVisitor;
import com.amazonaws.kinesisvideo.parser.mkv.StreamingMkvReader;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideo;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoArchivedMedia;
import com.amazonaws.services.kinesisvideo.AmazonKinesisVideoArchivedMediaClient;
import com.amazonaws.services.kinesisvideo.model.*;

import lombok.extern.slf4j.Slf4j;

/* This worker retrieves all fragments within the specified Time Range from a specified Kinesis Video Stream and puts them in a list */

@Slf4j
public class GetMediaArchivedRekognitionWorker extends KinesisVideoCommon implements Runnable {
    private FragmentSelector fragmentSelector;
    private final AmazonKinesisVideoArchivedMedia amazonKinesisVideoArchivedMediaListFragments;
    private final AmazonKinesisVideoArchivedMedia amazonKinesisVideoArchivedMediaGetMediaForFragmentList;
    private MkvElementVisitor elementVisitor;
    private List<String> fragment_numbers = Collections.synchronizedList(new ArrayList<String>());

    public GetMediaArchivedRekognitionWorker(final String streamName,
                                             final AWSCredentialsProvider awsCredentialsProvider,
                                             final String listFragmentsEndPoint,
                                             final String getMediaForFragmentListEndPoint,
                                             final Regions region,
                                             final FragmentSelector fragmentSelector,
                                             final MkvElementVisitor elementVisitor) {
        super(region, awsCredentialsProvider, streamName);
        this.fragmentSelector = fragmentSelector;
        this.elementVisitor = elementVisitor;

        amazonKinesisVideoArchivedMediaListFragments = AmazonKinesisVideoArchivedMediaClient
                .builder()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(listFragmentsEndPoint, region.getName()))
                .build();

        amazonKinesisVideoArchivedMediaGetMediaForFragmentList = AmazonKinesisVideoArchivedMediaClient
                .builder()
                .withCredentials(awsCredentialsProvider)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(getMediaForFragmentListEndPoint, region.getName()))
                .build();

    }

    public static GetMediaArchivedRekognitionWorker create(final String streamName,
                                                           final AWSCredentialsProvider awsCredentialsProvider,
                                                           final Regions region,
                                                           final AmazonKinesisVideo amazonKinesisVideo,
                                                           final FragmentSelector fragmentSelector,
                                                           final MkvElementVisitor elementVisitor,
                                                           final String listFragmentsEndpoint,
                                                           final String getMediaForFragmentListEndpoint)
    {

        return new GetMediaArchivedRekognitionWorker(
                streamName, awsCredentialsProvider, listFragmentsEndpoint, getMediaForFragmentListEndpoint, region, fragmentSelector, elementVisitor);
    }

    @Override
    public void run() {
        try {
            log.info("Start ListFragment worker on stream {}", streamName);

            /* ---------------------------- LIST FRAGMENTS SECTION ---------------------------- */
            ListFragmentsRequest listFragmentsRequest = new ListFragmentsRequest()
                    .withStreamName(streamName).withFragmentSelector(fragmentSelector).withMaxResults((long) 100);

            System.out.println(listFragmentsRequest.toString());

            ListFragmentsResult listFragmentsResult = amazonKinesisVideoArchivedMediaListFragments.listFragments(listFragmentsRequest);


            log.info("List Fragments called on stream {} response {} request ID {}",
                    streamName,
                    listFragmentsResult.getSdkHttpMetadata().getHttpStatusCode(),
                    listFragmentsResult.getSdkResponseMetadata().getRequestId());

            List<Fragment> fragments_list = listFragmentsResult.getFragments();


            String nextToken = listFragmentsResult.getNextToken();

            /* If result is truncated, keep making requests until nextToken is empty */
            while (nextToken != null) {
                listFragmentsRequest = new ListFragmentsRequest()
                        .withStreamName(streamName).withNextToken(nextToken);
                listFragmentsResult = amazonKinesisVideoArchivedMediaListFragments.listFragments(listFragmentsRequest);

                fragments_list = Stream.concat(fragments_list.stream(), listFragmentsResult.getFragments().stream()).collect(Collectors.toList());
                nextToken = listFragmentsResult.getNextToken();
            }

            //System.out.println("Fragments retrieved in " + Thread.currentThread().getName());
            for (Fragment f : fragments_list) {
                //System.out.println(f.getFragmentNumber());
                fragment_numbers.add(f.getFragmentNumber());
            }
            Collections.sort(fragment_numbers);
            //System.out.println("-------------------------------");

            /* ------------------------- GET MEDIA SECTION ------------------------- */

            if (fragment_numbers.size() > 0) {
                GetMediaForFragmentListRequest getMediaFragmentListRequest = new GetMediaForFragmentListRequest()
                        .withFragments(fragment_numbers)
                        .withStreamName(streamName);

                GetMediaForFragmentListResult getMediaForFragmentListResult = amazonKinesisVideoArchivedMediaGetMediaForFragmentList.getMediaForFragmentList(getMediaFragmentListRequest);

                StreamingMkvReader mkvStreamReader = StreamingMkvReader.createDefault(
                        new InputStreamParserByteSource(getMediaForFragmentListResult.getPayload()));

                try {
                    mkvStreamReader.apply(this.elementVisitor);
                } catch (final MkvElementVisitException e) {
                    log.error("Exception while accepting visitor {}", e);
                    System.out.println("Exception while accepting visitor " + e.toString());
                }
            }

        } catch (Throwable t) {
            System.out.println("GetMediaArchivedRekognitionWorker failed in " + Thread.currentThread().getName());
            log.error("Failure in GetMediaArchivedRekognitionWorker for streamName {} {}", streamName, t.toString());
            throw t;
        } finally {
            log.info("Exiting GetMediaArchivedRekognitionWorker for stream {}", streamName);
        }
    }
}