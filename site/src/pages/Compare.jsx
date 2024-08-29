import React, { useState } from 'react';
import UpperArea from '../components/UpperArea';
import '../styles/Compare.css';
import TagsInput from "../components/TagsInput";
import { useNavigate } from 'react-router-dom';

const Compare = () => {
    const [errorMessage, setErrorMessage] = useState('');
    const [usernamesCompare, setUsernamesCompare] = useState('');
    const [compareParks, setCompareParks] = useState([]);
    const [selectedPark, setSelectedPark] = useState(null);
    const [numUsers, setNumUsers] = useState(0);

    const username = sessionStorage.getItem('username');

    const jwtToken = sessionStorage.getItem('token');

    const navigate = useNavigate();

    const handleCompareSearchTags = (tags) => {
        const usernames = tags.map(tag => btoa(tag)).join(',');
        setUsernamesCompare(usernames);
        setErrorMessage('');
    };

    const handleUserPrivacyError = (message) => {
        setErrorMessage(message);
    };

    const fetchCompareParks = async (usernames) => {
        const response = await fetch(`/api/compareParks?usernames=${username},${usernames}`, {
            headers: {
                'Authorization': `Bearer ${jwtToken}`
            }
        });
        const data = await response.json();
        setNumUsers(data.total);
        console.log('Compare Parks Response:', data);

        const parsedParkData = data.parkData.map(parkDataObj => {
            const parsedParkData = JSON.parse(parkDataObj.data);
            return {
                ...parkDataObj,
                parsedData: parsedParkData.parks[0]
            };
        });

        setCompareParks({...data, parkData: parsedParkData});
    };

    const handleParkClick = (park) => {
        console.log("Clicking park named: " + park.fullName);
        if (selectedPark === park) {
            setSelectedPark(null);
        } else {
            setSelectedPark(park);
        }
    };

    const handleActivityClick = (activityName) => {
        navigate(`/search?searchType=activity&searchTerm=${encodeURIComponent(activityName.trim())}`);
    };

    const handleStateCodeClick = (stateCode) => {
        navigate(`/search?searchType=state&searchTerm=${encodeURIComponent(stateCode)}`);
    };

    const handleAmenityClick = (amenityName) => {
        navigate(`/search?searchType=amenities&searchTerm=${encodeURIComponent(amenityName.trim())}`);
    };

    return (
        <div>
            <UpperArea />
            <h1 className={"page-title-compare"}>Compare favorite parks with others!</h1>
            <div className="CenterColumn">
                <div className="compare-search-box box1">
                    <TagsInput onEnter={handleCompareSearchTags} onUserPrivacyError={handleUserPrivacyError}/>
                </div>
                <button className="compare-button" onClick={() => fetchCompareParks(usernamesCompare)}>
                    Compare!
                </button>
                {errorMessage && <p className="error-message">{errorMessage}</p>}
                <div className="CompareResultsArea">
                    {compareParks.length === 0 ? (
                        <div className="no-results-message">
                            Enter usernames you want to compare favorites with and click the Compare button!
                        </div>
                    ) : (
                        <div className="park-list">
                            {compareParks.parkData.map((parkData) => {
                                const park = parkData.parsedData;
                                return (
                                    <div key={park.id} className="park-card">
                                        <h2
                                            className="park-card-name-comp"
                                            onClick={() => handleParkClick(park)}
                                            style={{
                                                cursor: 'pointer',
                                                marginBottom: 10,
                                                color: selectedPark === park ? 'green' : 'black',
                                                display: 'flex', // Use flexbox for layout
                                                alignItems: 'center', // Center items vertically
                                            }}
                                        >
                                            <div className="tooltip" style={{marginRight: '0.5rem'}}>
                                                <span className="park-ratio">{parkData.frequency}/{numUsers}</span>
                                                <span className="tooltiptext">{parkData.favusernames}</span>
                                            </div>
                                            {park.fullName}
                                        </h2>
                                        {selectedPark === park && (
                                            <div>
                                                <img className="park-image" src={park.images[0]?.url}
                                                     alt={park.fullName}/>
                                                <p className="park-location">
                                                    Location: {park.addresses[0].city},{' '}
                                                    <span
                                                        className="park-state-code"
                                                        onClick={() => handleStateCodeClick(park.addresses[0].stateCode)}
                                                        style={{cursor: 'pointer', textDecoration: 'underline'}}
                                                    >
                                                        {park.addresses[0].stateCode}
                                                    </span>
                                                </p>
                                                <p className="park-entrance-fee">
                                                    Entrance Fee:{' '}
                                                    {park.entranceFees.length > 0 ? park.entranceFees[0].cost : 'Not available'}
                                                </p>
                                                <p className="park-description">Description: {park.description}</p>
                                                {park.activities && (
                                                    <p className="park-activities">
                                                        Activities:{' '}
                                                        {park.activities.map((activity, index) => (
                                                            <React.Fragment key={activity.name}>
                                                                <span
                                                                    className="park-activity"
                                                                    onClick={() => handleActivityClick(activity.name)}
                                                                    style={{
                                                                        cursor: 'pointer',
                                                                        textDecoration: 'underline'
                                                                    }}
                                                                >
                                                                    {activity.name}
                                                                </span>
                                                                {index !== park.activities.length - 1 && ', '}
                                                            </React.Fragment>
                                                        ))}
                                                    </p>
                                                )}
                                                <p>
                                                    Directions:{' '}
                                                    <a
                                                        className="park-url"
                                                        href={park.directionsUrl}
                                                        target="_blank"
                                                        rel="noopener noreferrer"
                                                    >
                                                        Official Website
                                                    </a>
                                                </p>
                                                {park.operatingHours && (
                                                    <div>
                                                        <p className="park-operating-hours">Operating Hours:</p>
                                                        {park.operatingHours.map((hours) => (
                                                            <p key={hours.name}>
                                                                {hours.name}: {Object.values(hours.standardHours).join(', ')}
                                                            </p>
                                                        ))}
                                                    </div>
                                                )}
                                                <p className="park-contact">
                                                    Contact:{' '}
                                                    {park.contacts.phoneNumbers.map((phone) => phone.phoneNumber).join(', ')}
                                                </p>
                                                {park.amenities && (
                                                    <p className="park-amenities">
                                                        Amenities:{' '}
                                                        {park.amenities.map((amenity, index) => (
                                                            <React.Fragment key={amenity}>
                                                    <span
                                                        className="park-amenity"
                                                        onClick={() => handleAmenityClick(amenity)}
                                                        style={{
                                                            cursor: 'pointer',
                                                            textDecoration: 'underline'
                                                        }}
                                                    >
                                                        {amenity}
                                                    </span>
                                                                {index !== park.amenities.length - 1 && ', '}
                                                            </React.Fragment>
                                                        ))}
                                                    </p>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Compare;