import {Button} from "react-bootstrap";
import React from 'react';

const TimestampButtons = (props) => {

    const timestampsDisplaying = props.timestampsDisplaying;
    var isShowingTimestamps = timestampsDisplaying[props.index];
    const label = props.labels[props.index];
    //console.log(timestampsDisplaying);
    //console.log(props.timestamps);
    
  
    return (
        <React.Fragment>
            {isShowingTimestamps ? <h1>Timestamps for {label}: {props.timestampCollection.timestamps.map((timestamp, index) => (
                <div key={index}>
                    <Button onClick={() => props.onClick(props.timestamps.indexOf(timestamp))} variant="secondary">{timestamp}</Button>
                </div>
            ))}</h1> : null} 
        </React.Fragment> 
    )
}

export default TimestampButtons;