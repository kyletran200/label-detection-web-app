import React, {useHistory, useState, useEffect, Component, Fragment} from 'react';

import {Card} from "react-bootstrap";
import {Link, BrowserRouter as Router, Route, useParams} from "react-router-dom";
import './App.css';
import axios from "axios";

const Streams = () => {
    const [streams, setStreams] = useState([]);
  
    const fetchStreams = () => {
      axios.get("http://localhost:8080/streams").then(res => {
        console.log(res);
        setStreams(res.data);
      });
    };
  
    useEffect(() => {
      fetchStreams();
    }, []);

    return (
        <React.Fragment>
            <h1>Archived Stream Segments</h1>
            {streams.map((stream, index) => (
                <React.Fragment key={index}>
                    <Card style={{ width: '18rem' }}>
                        <Card.Body>
                            <Card.Title>{stream.name}</Card.Title>
                            <Card.Text>Start Timestamp: {stream.startTimestamp}</Card.Text>
                            <Card.Text>End Timestamp: {stream.endTimestamp}</Card.Text>
                            <Link to={`/streams/${stream.id}`} className="btn btn-primary">View Rekgonized Images</Link>
                        </Card.Body>
                    </Card>
                </React.Fragment>
            ))}
        </React.Fragment>
    )
  };

  export default Streams;