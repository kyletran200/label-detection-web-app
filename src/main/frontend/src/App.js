import React, {useHistory, useState, useEffect} from 'react';

import {Button} from "react-bootstrap";
import {Link, BrowserRouter as Router, Route, useParams} from "react-router-dom";
import './App.css';
import axios from "axios";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import StreamForm from "./StreamForm.js"
import Streams from "./Streams.js"
import Stream from "./Stream.js"
import LoadingIndicator from "./LoadingIndicator.js"



const Video = () => {
  let {id} = useParams();
  const [video, setVideo] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const fetchVideoInfo = async () => {
    try {
      setLoading(true);
      axios.get("http://localhost:8080/videos/" + id).then(res => {
        console.log(res);
        setVideo(res.data);
        setLoading(false);
      });
    } catch (err) {
      console.log(err);
      setLoading(false);
    } 
  };

  useEffect(() => {
    fetchVideoInfo();
  }, []);

  var settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    arrows: true,
  };

  var labels = video.labels;
  var frames = video.frames;

  return (
    <React.Fragment>
      {loading ? <LoadingIndicator/> : <h1>Labels for {video.name}</h1>}
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

const Videos = () => {
  const [videos, setVideos] = useState([]);

  const fetchVideos = () => {
    axios.get("http://localhost:8080/videos").then(res => {
      console.log(res);
      setVideos(res.data);
    });
  };

  useEffect(() => {
    fetchVideos();
  }, []);

  return (
    <React.Fragment>
      <h1>Video Samples</h1>
      {videos.map((video, index) => (
        <React.Fragment key={index}>
          <h1></h1>
          <Link to={`/videos/${video.id}`} className="btn btn-primary">{video.name}</Link>
        </React.Fragment>
      ))}
      <h1></h1>
      <Link to="/archivedStream" className="btn btn-secondary">Archived Media Retrieval</Link>
    </React.Fragment>
  )
};

function App() {  
  return (
  <Router>
    <div className="App">
      <Route exact path="/" component={Videos}/>
      <Route exact path="/videos/:id">
        <Video/>
      </Route>
      <Route exact path="/archivedStream" component={StreamForm}/>    
      <Route exact path="/streams" component={Streams}/>
      <Route exact path="/streams/:id">
        <Stream/>
      </Route>
    </div>
  </Router>
  );
}

export default App;