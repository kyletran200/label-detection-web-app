import React, {useState, useEffect} from 'react';
import {useParams} from "react-router-dom";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import axios from "axios";
import {Button} from "react-bootstrap";
import LoadingIndicator from "./LoadingIndicator.js"
import LabelButton from "./LabelButton.js"


const Stream = () => {
    let {id} = useParams();
    const [Stream, setStream] = useState([]);
    const [loading, setLoading] = useState(false);
    
    const fetchStreamInfo = async () => {
      try {
        setLoading(true);
        axios.get("http://localhost:8080/streams/" + id).then(res => {
          console.log(res);
          setStream(res.data);
          setLoading(false);
        });
      } catch (err) {
        console.log(err);
        setLoading(false);
      } 
    };
  
    useEffect(() => {
      fetchStreamInfo();
    }, []);
  
    var settings = {
      dots: true,
      infinite: true,
      speed: 500,
      slidesToShow: 1,
      slidesToScroll: 1,
      arrows: true,
    };
  
    //var labels = Stream.labels;
    var frames = Stream.frames;
    var labelToTimestamps = Stream.labelToTimestamps;
    var labels;
    var timestampCollections;

    if (labelToTimestamps) {
        labels = Object.keys(labelToTimestamps);
        timestampCollections = Object.values(labelToTimestamps);
    }

    console.log(labels);
    console.log(timestampCollections);
   
    //console.log(labelToTimestamps);
  
  
    /*
    return (
      <React.Fragment>
        {loading ? <LoadingIndicator/> : <h1>Labels for {Stream.name}</h1>}
        {labels && labels.map((label, index) => (
          <React.Fragment key={index}>
            <Button variant="primary" style={{padding:10, margin:10}}>{label}</Button>
          </React.Fragment>
        ))}
        <Slider {... settings}>
        {frames && frames.map((frame, index) => (
            <div key={index}>
              <img src={`data:image/png;base64,${frame.imageBytes}`}/>
              <h1>{frame.playbackTimestamp}</h1>
            </div>
        ))}
        </Slider>
      </React.Fragment>
    )
  };
  */

  return (
    <React.Fragment>
      {loading ? <LoadingIndicator/> : <h1>Labels for {Stream.name}</h1>}
      {labels && labels.map((label, index) => (
        <React.Fragment key={index}>
          <LabelButton text={label} timestamps={timestampCollections} index={index}></LabelButton>
        </React.Fragment>
      ))}
      <Slider {... settings}>
      {frames && frames.map((frame, index) => (
          <div key={index}>
            <img src={`data:image/png;base64,${frame.imageBytes}`}/>
            <h1>{frame.playbackTimestamp}</h1>
          </div>
      ))}
      </Slider>
    </React.Fragment>
  )
};

  export default Stream;