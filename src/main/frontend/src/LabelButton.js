import {Button} from "react-bootstrap";
import React, {useState, useEffect} from 'react';


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