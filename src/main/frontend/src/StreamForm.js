import React from 'react';
import {Col, Button, Form} from "react-bootstrap";
//import './App.css';
import axios from "axios";
import "./styles.css";




const StreamForm = props => {

    const onSubmit = (event) => {

        event.preventDefault();
        console.log("WE HAVE SUBMITTED THE FORM!!!!");
        console.log(event.target[0].value);
        console.log(event.target[1].value);


        const postStreamInfo = () => {
            axios.post("http://localhost:8080/streams", {
                name: event.target[0].value,
                startTimestamp: event.target[1].value,
                endTimestamp: event.target[2].value,
                threads: event.target[3].value,
                sampleRate: event.target[4].value
            })
            .then(res => {
                console.log("Successfully submitted")
                console.log(res);
                props.history.push("/streams")
            })
            .catch(err => {
                console.log(err);
            })
        }
        postStreamInfo();
    }

    return (
        <React.Fragment>
            <h1>Enter Stream Information</h1>
            <Form onSubmit={onSubmit} id="form">
                <Form.Group controlId="formBasicStreamName">
                    <Form.Row class="form-group">  
                        <Col>
                            <Form.Label>Stream Name</Form.Label>
                            <Form.Control type="text" name="streamName" placeholder="Enter stream name"/>
                        </Col>
                    </Form.Row>
                </Form.Group>
                
                <Form.Group controlId="formTimestamps">
                    <Form.Row class="form-group">
                        <Col>
                            <Form.Label>Start Timestamp</Form.Label>
                            <Form.Control type="text" name="startTimestamp" placeholder="Enter start timestamp"/>
                        </Col>
                        <Col>
                            <Form.Label>End Timestamp</Form.Label>
                            <Form.Control type="text" name="endTimestamp" placeholder="Enter end timestamp"/>
                        </Col>
                    </Form.Row>
                </Form.Group>

                <Form.Group controlId="formTimestamps">
                    <Form.Row class="form-group">
                        <Col>
                            <Form.Label>Threads</Form.Label>
                            <Form.Control type="text" name="threads" placeholder="Enter number of threads"/>
                        </Col>
                    </Form.Row>
                </Form.Group>
                
                <Form.Group>
                    <Form.Row class="form-group">
                        <Col>
                            <Form.Label>Sample Rate</Form.Label>
                            <Form.Control type="int" name="sampleRate" placeholder="Enter sample rate"/>
                        </Col>
                    </Form.Row>
                </Form.Group>

                <Button variant="primary" type="Submit">
                    Submit
                </Button>
            </Form>
        </React.Fragment>
    )
}

export default StreamForm;