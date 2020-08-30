
import React, {useState, useEffect} from 'react';
import {Button} from "react-bootstrap";
import Slider from 'react-slick';
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";


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

  export default Video;