import {Button, DropdownItem, Dropdown} from "react-bootstrap";
import React, {useState, useEffect} from 'react';

const DropdownLabel = (props) => {

    /* {isShowingTimestamps ? <h1>Timestamps for {props.text}: {props.timestamps[props.index].timestamps.map((timestamp, index) => (
            <div key={index}>
                <Button variant="secondary">{timestamp}</Button>
            </div>
        ))}</h1> : null} */
    //const handleClick = () => isShowingTimestamps ? setShowingTimestamps(false) : setShowingTimestamps(true);
    const handleClick = () => {
        props.onClick(props.index);
    }
    
    return (
        <React.Fragment>
            <Dropdown.Item onClick={handleClick} id="dropdown-labels-button" title="Labels">
                {props.label}
            </Dropdown.Item>  
        </React.Fragment>
    )
}

export default DropdownLabel;