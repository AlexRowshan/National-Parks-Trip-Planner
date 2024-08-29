import React, {useEffect, useState} from 'react';
import UpperArea from '../components/UpperArea';
import '../styles/Compare.css';
import {useNavigate} from "react-router-dom";
import TagInput from '../components/TagInput';


const Suggest = () => {
    // usernames state
    const [usernamesSuggest, setUsernamesSuggest] = useState('');

    const [suggestSearchTerm, setSuggestSearchTerm] = useState('');

    // suggest parks state
    const [parkData, setParkData] = useState(null);
    const [selectedSuggestPark, setSelectedSuggestPark] = useState(null);

    const [expandedPark, setExpandedPark] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const [username, setUsername] = useState("");
    const [token, setToken] = useState("");

    const [errorMessage, setErrorMessage] = useState('');

    const handleSuggestSearchTags = (tags) => {
        const usernames = tags.join(',');
        setUsernamesSuggest(usernames);
        setErrorMessage('');
    };

    const handleUserPrivacyError = (message) => {
        setErrorMessage(message);
    };

    useEffect(() => {
        const storageUsername = sessionStorage.getItem('username');
        const jwtToken = sessionStorage.getItem('token');
        console.log(`Current user: ${storageUsername}`);
        setToken(jwtToken);
        setUsername(storageUsername);
    }, []);

    const handleParkClick = (park) => {
        if (expandedPark === park) {
            setExpandedPark(null);
        } else {
            setExpandedPark(park);
        }
    };

    const handleSuggestSearchChange = (event) => {
        setSuggestSearchTerm(event.target.value);
        const usernames = event.target.value.replace(/\s/g, '');
        if (usernames === '') {
            return;
        }
        setUsernamesSuggest(usernames);
        // props.fetchSuggestParks(usernames); // Call the fetchSuggestParks prop
        // fetchSuggestParks(usernames); // Call the fetchSuggestParks NOT A PROP
    };

    const navigate = useNavigate();

    const handleActivityClick = (activityName) => {
        navigate(`/search?searchType=activity&searchTerm=${encodeURIComponent(activityName.trim())}`);
    };

    const handleStateCodeClick = (stateCode) => {
        navigate(`/search?searchType=state&searchTerm=${encodeURIComponent(stateCode)}`);
    };

    const handleAmenityClick = (amenityName) => {
        navigate(`/search?searchType=amenities&searchTerm=${encodeURIComponent(amenityName.trim())}`);
    };

    const handleKeyPressSuggest = (event) => {
        if (event.key === 'Enter') {
            fetchSuggestParks(usernamesSuggest);
        }
    };

    const fetchSuggestParks = async (usernames) => {
        // make each username in usernames btoa(username)
        usernames = usernames.split(',').map((username) => btoa(username)).join(',');
        const response = await fetch(`api/suggestParks?usernames=${username},${usernames}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
            }
        });

        if (response.status === 400) {
            const errorMessage = await response.text();
        }

        const data = await response.json();
        console.log(data);
        setParkData(data);
        setIsLoading(false);
    };

    return (
        <div>
            <UpperArea />
            <h1>Suggest a park to visit!</h1>
            <div className="SuggestSearchArea">
                <div className="suggest-search-box box1">
                    <TagInput onEnter={handleSuggestSearchTags} onUserPrivacyError={handleUserPrivacyError} />
                </div>
                <button className="suggest-button" onClick={() => fetchSuggestParks(usernamesSuggest)}>
                    Suggest!
                </button>
            </div>
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            {isLoading ? (
                <div>Loading...</div>
            ) : parkData && parkData.data && parkData.data[0] ? (
                <div key={parkData.data[0].id} className="park-card">
                    <div className="park-card-header">
                        <h3
                            className="park-card-name"
                            onClick={() => handleParkClick(parkData.data[0])}
                            tabIndex={0}
                            onKeyDown={(event) => {
                                if (event.key === 'Enter') {
                                    handleParkClick(parkData.data[0]);
                                }
                            }}
                            // style={{
                            //     cursor: 'pointer',
                            //     display: 'inline-block',
                            //     // color: expandedPark === parkData.parks[0] ? 'green' : 'black',
                            //     color: 'green',
                            // }}
                        >
                            {parkData.data[0].fullName}
                        </h3>
                    </div>
                    <div className="park-card-images">
                        {parkData && parkData.data && parkData.data[0] && parkData.data[0].images && (
                            parkData.data[0].images.slice(0, 3).map((image, index) => (
                                <img key={index} src={image.url} alt={image.altText}/>
                            ))
                        )}
                    </div>
                    {expandedPark === parkData.data[0] && (
                        <div>
                            <p className="park-location">
                                Location: {parkData.data[0].addresses[0].city},{' '}
                                <span
                                    className="park-state-code"
                                    onClick={() => handleStateCodeClick(parkData.data[0].addresses[0].stateCode)}
                                    style={{
                                        cursor: 'pointer',
                                        textDecoration: 'underline',
                                    }}
                                    tabIndex={0}
                                    onKeyDown={(e) => {
                                        if (e.key === 'Enter') {
                                            handleStateCodeClick(parkData.data[0].addresses[0].stateCode);
                                        }
                                    }}
                                >
                {parkData.data[0].addresses[0].stateCode}
              </span>
                            </p>
                            <p className="park-description">Description: {parkData.data[0].description}</p>
                            {parkData.data[0].activities && (
                                <p className="park-activities">
                                    Activities:{' '}
                                    {parkData.data[0].activities.map((activity, index) => (
                                        <React.Fragment key={activity.name}>
                    <span
                        className="park-activity"
                        onClick={() => handleActivityClick(activity.name)}
                        style={{cursor: 'pointer', textDecoration: 'underline'}}
                        tabIndex={0}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                handleActivityClick(activity.name);
                            }
                        }}
                    >
                      {activity.name}
                    </span>
                                            {index !== parkData.data[0].activities.length - 1 && ', '}
                                        </React.Fragment>
                                    ))}
                                </p>
                            )}
                            {parkData.amenities && parkData.amenities[parkData.data[0].parkCode] && (
                                <p className="park-amenities">
                                    Amenities:{' '}
                                    {parkData.amenities[parkData.data[0].parkCode].map((amenity, index) => (
                                        <React.Fragment key={amenity}>
                    <span
                        className="park-amenity"
                        onClick={() => handleAmenityClick(amenity)}
                        style={{cursor: 'pointer', textDecoration: 'underline'}}
                        tabIndex={0}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') {
                                handleAmenityClick(amenity);
                            }
                        }}
                    >
                      {amenity}
                    </span>
                                            {index !== parkData.amenities[parkData.data[0].parkCode].length - 1 && ', '}
                                        </React.Fragment>
                                    ))}
                                </p>
                            )}
                        </div>
                    )}
                </div>
            ) : (
                <div>No park data available.</div>
            )}
        </div>
    );
}
export default Suggest;