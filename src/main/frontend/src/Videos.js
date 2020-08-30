
import React, {useState, useEffect} from 'react';
import {Button} from "react-bootstrap";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

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
        <Link to="/streamform" className="btn btn-secondary">Archived Media Retrieval</Link>
      </React.Fragment>
    )
  };

  export default Videos;