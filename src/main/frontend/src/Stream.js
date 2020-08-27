import React, {useState, useEffect} from 'react';
import {useParams} from "react-router-dom";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import axios from "axios";
import {Button, DropdownButton, Dropdown, Carousel} from "react-bootstrap";
import LoadingIndicator from "./LoadingIndicator.js";
import LabelButton from "./LabelButton.js";
import DropdownLabel from "./DropdownLabel.js";
import TimestampButtons from "./TimestampButtons.js";
import ControlledCarousel from "./ControlledCarousel.js";


const Stream = () => {
    let {id} = useParams();
    const [Stream, setStream] = useState([]);
    const [loading, setLoading] = useState(false);

    const [TimestampsDisplaying, setTimestampsDisplaying] = useState([]);
    const [carouselIndex, setCarouselIndex] = useState(0);
    
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
    var timestamps;
    var labelToTimestamps = Stream.labelToTimestamps;
    var labels;
    var timestampCollections;

    if (labelToTimestamps) {
        labels = Object.keys(labelToTimestamps);
        timestampCollections = Object.values(labelToTimestamps);
        
        if (TimestampsDisplaying.length <= 0) {
          setTimestampsDisplaying(new Array(labels.length).fill(false));  
        }    
    }

    if (frames) {
      timestamps = frames.map(frame => frame.playbackTimestamp);
    }
    //console.log(TimestampsDisplaying);
    //console.log(labels);
    //console.log(timestampCollections);
   
    //console.log(labelToTimestamps);<Dropdown.Item>{label}</Dropdown.Item>
  
    const handleLabelSelect = (index) => {
      let newArr = Array(TimestampsDisplaying.length).fill(false);
      newArr[index] = true;
      setTimestampsDisplaying(newArr);
    }

    const handleCarouselIndexChange = (index) => {
      setCarouselIndex(index);
    }

    return (
      <React.Fragment>
        {loading ? <LoadingIndicator/> : <h1>Labels for {Stream.name}</h1>}
        <DropdownButton id="dropdown-labels-button" title="Labels">
          {labels && labels.map((label, index) => (
            <DropdownLabel label={label} timestamps={timestampCollections} index={index} key={index} onClick={handleLabelSelect}></DropdownLabel>
          ))}
        </DropdownButton>
        <React.Fragment>
          {timestampCollections && timestampCollections.map((timestampCollection, index) => (
            <TimestampButtons index={index} key={index} timestampsDisplaying={TimestampsDisplaying} labels={labels} timestampCollection={timestampCollection} timestamps={timestamps} onClick={handleCarouselIndexChange}></TimestampButtons>
          ))}
        </React.Fragment>
        <h1></h1>
        <ControlledCarousel frames={frames} index={carouselIndex} onSelect={handleCarouselIndexChange}>
        </ControlledCarousel> 
      </React.Fragment>
    )
  };
  
/*
  return (
      <React.Fragment>
        {loading ? <LoadingIndicator/> : <h1>Labels for {Stream.name}</h1>}
        <DropdownButton id="dropdown-labels-button" title="Labels">
          {labels && labels.map((label, index) => (
            <DropdownLabel label={label} timestamps={timestampCollections} index={index} key={index} onClick={handleLabelSelect}></DropdownLabel>
          ))}
        </DropdownButton>
        <React.Fragment>
          {timestampCollections && timestampCollections.map((timestampCollection, index) => (
            <TimestampButtons index={index} key={index} timestampsDisplaying={TimestampsDisplaying} labels={labels} timestampCollection={timestampCollection}></TimestampButtons>
          ))}
        </React.Fragment>
        <h1></h1>
        <Carousel>
            {frames && frames.map((frame, index) => (
              <Carousel.Item key={index}>
                <img src={`data:image/png;base64,${frame.imageBytes}`}/>
                <Carousel.Caption>
                  <h1>{frame.playbackTimestamp}</h1>
                </Carousel.Caption>
              </Carousel.Item>
            ))}
        </Carousel> 
      </React.Fragment>
    )
  };*/

  export default Stream;