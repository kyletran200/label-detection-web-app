import React, {useHistory, useState, useEffect, Component, Fragment} from 'react';

import {Col, Button, Form} from "react-bootstrap";
import {withRouter, Redirect} from "react-router-dom";
import './App.css';
import axios from "axios";




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
                tasks: event.target[3].value,
                threads: event.target[4].value,
                sampleRate: event.target[5].value
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
        <Form onSubmit={onSubmit}>
            <Form.Group controlId="formBasicStreamName">
                <Form.Row>  
                    <Col xs="6" >
                        <Form.Label>Stream Name</Form.Label>
                        <Form.Control type="text" name="streamName" placeholder="Enter stream name"/>
                    </Col>
                </Form.Row>
            </Form.Group>
            
            <Form.Group controlId="formTimestamps">
                <Form.Row>
                    <Col xs="3">
                        <Form.Label>Start Timestamp</Form.Label>
                        <Form.Control type="text" name="startTimestamp" placeholder="Enter start timestamp"/>
                    </Col>
                    <Col xs="3">
                        <Form.Label>End Timestamp</Form.Label>
                        <Form.Control type="text" name="endTimestamp" placeholder="Enter end timestamp"/>
                    </Col>
                </Form.Row>
            </Form.Group>

            <Form.Group controlId="formTimestamps">
                <Form.Row>
                    <Col xs="3">
                        <Form.Label>Tasks</Form.Label>
                        <Form.Control type="text" name="tasks" placeholder="Enter number of tasks to partition timerange"/>
                    </Col>
                    <Col xs="3">
                        <Form.Label>Threads</Form.Label>
                        <Form.Control type="text" name="threads" placeholder="Enter number of threads to process video"/>
                    </Col>
                </Form.Row>
            </Form.Group>
            
            <Form.Group>
                <Form.Row>
                    <Col xs="6">
                        <Form.Label>Sample Rate</Form.Label>
                        <Form.Control type="int" name="sampleRate" placeholder="Enter sample rate"/>
                    </Col>
                </Form.Row>
            </Form.Group>

            <Button variant="primary" type="Submit">
                Submit
            </Button>
        </Form>
    )
}

export default StreamForm;