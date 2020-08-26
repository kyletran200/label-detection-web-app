import {Button} from "react-bootstrap";
import React, {useState, useEffect} from 'react';

/* 
{isShowingTimestamps && Object.keys(props.timestamps).map((timestamp, index) => (
            <div key={index}>
                <h1>{timestamp}</h1>
            </div>
        ))} */

/* {isShowingTimestamps ? <h1>Timestamps for {props.text}: {props.timestamps[props.index].id}</h1> : null} */

const LabelButton = (props) => {
    const [isShowingTimestamps, setShowingTimestamps] = useState(false);

    const handleClick = () => isShowingTimestamps ? setShowingTimestamps(false) : setShowingTimestamps(true);

    return (
        <React.Fragment>
        <Button 
            variant="primary"
            onClick={handleClick}
            style={{padding:10, margin:10}}
        >   
            {props.text}
        </Button>
        {isShowingTimestamps ? <h1>Timestamps for {props.text}: {props.timestamps[props.index].timestamps.map((timestamp, index) => (
            <div key={index}>
                <Button variant="secondary">{timestamp}</Button>
            </div>
        ))}</h1> : null}
        </React.Fragment>
    )
}

export default LabelButton;