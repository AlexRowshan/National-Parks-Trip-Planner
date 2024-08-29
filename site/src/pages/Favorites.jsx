import React, { useState, useEffect } from 'react';
import UpperArea from "../components/UpperArea";
import '../styles/Favorites.css';
import { useNavigate } from 'react-router-dom';

const Favorites = () => {
    const [parkData, setParkData] = useState(null);
    const [username, setUsername] = useState("");
    const [token, setToken] = useState("");
    const [expandedPark, setExpandedPark] = useState(null);
    const [hoveredPark, setHoveredPark] = useState(null);
    const [selectedPark, setSelectedPark] = useState(null);
    const [isFavorite, setIsFavorite] = useState(false);
    const [isPrivate, setIsPrivate] = useState(false);
    const [displayCount, setDisplayCount] = useState(10); // Initial display count
    const [isLoading, setIsLoading] = useState(false); // State for loading indicator

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

    useEffect(() => {
        const storageUsername = sessionStorage.getItem('username');
        const jwtToken = sessionStorage.getItem('token');
        console.log(`Current user: ${storageUsername}`);
        setToken(jwtToken);
        setUsername(storageUsername);
        // Use the storageUsername directly for the initial API calls
        fetchParkData(storageUsername, jwtToken);
        fetchPrivateStatus(storageUsername, jwtToken);
    }, []);

    const handleParkClick = (park) => {
        if (selectedPark === park) {
            setSelectedPark(null);
            setExpandedPark(null);
            setIsFavorite(false);
        } else {
            setSelectedPark(park);
            setExpandedPark(park);
            setIsFavorite(true);
        }
    };

    const handleHover = (park) => {
        setHoveredPark(park);
    };

    const handleLeave = () => {
        setHoveredPark(null);
    };

    const handleButtonClick = async (park) => {
        const parkCode = park.parkCode;
        const confirmRemoval = window.confirm('Are you sure you want to remove this park from favorites?');
        if (confirmRemoval) {
            try {
                const response = await fetch('/api/deleteParkFromFavorites', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ username, parkCode }),
                });

                if (response.ok) {
                    setIsFavorite(false);
                    const message = await response.text();
                    alert(message);
                    // Fetch the updated parkData
                    const updatedParkData = await fetchParkData(username, token);
                    console.log("Updated park data:", updatedParkData);
                    setParkData(updatedParkData);
                } else {
                    throw new Error('Failed to remove park from favorites.');
                }
            } catch (error) {
                alert(error.message);
            }
        }
    };

    const deleteAllFavorites = async () => {
        const confirmation = window.confirm('Are you sure you want to delete all favorites?');
        if (confirmation) {
            try {
                const response = await fetch('/api/deleteAllFavorites', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({username}),
                });

                if (response.ok) {
                    setParkData([]);
                    setIsPrivate(true);
                    alert('All favorites deleted, and privacy status was set back to private.');
                } else {
                    console.log('Failed to delete all favorites and set list to private.');
                }
            } catch (error) {
                alert(error.message);
            }
        }
    }

    const handleRankChange = async (park, rankChange) => {
        const parkCode = park.parkCode;
        try {
            console.log("Bouta handle a rank change!!!!!")
            const response = await fetch('/api/updateFavoriteRank', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ username, parkCode, rankChange }),
            });

            if (response.ok) {
                const updatedParkData = await fetchParkData(username, token);
                console.log("Updated park data:", updatedParkData);
                setParkData(updatedParkData);
            } else {
                throw new Error('Failed to update favorite rank.');
            }
        } catch (error) {
            console.log("Error updating rank!!!!!!");
            alert(error.message);
        }
    };

    const fetchParkData = async (myUsername, myToken) => {
        console.log("INSIDE FETCH PARK DATA!!!!!")
        console.log("Username: ", myUsername);
        console.log("Token: ", myToken);
        setIsLoading(true);
        try {
            const response = await fetch(`/api/getFavorites?username=${myUsername}`, {
                headers: {
                    'Authorization': `Bearer ${myToken}`,
                },
            });
            if (!response.ok) {
                if (response.status === 404) {
                    const message = await response.text();
                    if (message === "No favorites found.") {
                        setParkData([]); // Sets parkData to an empty array to indicate no favorites.
                        setIsLoading(false);
                        return [];
                    }
                    throw new Error(message);
                }
                throw new Error('Failed to fetch favorites data.');
            }
            const data = await response.json();
            console.log("Favorites data: ", data);
            setParkData(data);
            setIsLoading(false); // Set isLoading to false after data is fetched
            return data; // Return the fetched data
        } catch (error) {
            console.error("Error fetching park data:", error);
            alert(error.message);
            setParkData([]); // In case of any error, assume no data.
            setIsLoading(false); // Set isLoading to false in case of an error
        }
    };

    const fetchPrivateStatus = async (myUsername, myToken) => {
        // console.log all the inputs and make sure they are correct
        console.log("INSIDE FETCH PRIVATE STATUS FUNCTION!!!!!")
        console.log("Username: ", myUsername);
        console.log("Token: ", myToken);
        const response = await fetch(`/api/isPrivate?username=${myUsername}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${myToken}`,
            },
        });
        const data = await response.text();
        setIsPrivate(data === 'true');
    };

    const togglePrivateStatus = async () => {
        const response = await fetch(`/api/togglePrivate?username=${username}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
        });

        if (response.ok) {
            const data = await response.text();
            console.log(data);
            console.log("Toggled private status good!!!!!!")
            fetchPrivateStatus(username, token);
        } else {
            alert("Unable to toggle private status.");
        }
    };

    const handleSwitchEnterPress = (event) => {
        if (event.key === 'Enter') {
            togglePrivateStatus();
        }
    };

    return (
        <div>
            <UpperArea />
            <h1 className={"page-title-favorites"}>Favorites</h1>
            <div className="CenterColumn">
                <div className={"private-button-container"}>
                    <h2 className={"private-button-title"}>Toggle Privacy</h2>
                    <label className="switch" tabIndex="0" onKeyDown={handleSwitchEnterPress} aria-label="Toggle privacy">
                        <input
                            type="checkbox"
                            checked={isPrivate}
                            onChange={togglePrivateStatus}
                            aria-checked={isPrivate}
                        />
                        <span className="slider round"></span>
                    </label>
                </div>
                    <button className={"delete-all-button"} onClick={deleteAllFavorites}>Delete All Favorites</button>
                <div className="centered-container-parks">
                    {isLoading ? (
                        <div className="no-results-message">Loading...</div>
                    ) : Array.isArray(parkData) && parkData.length > 0 ? (
                        parkData.slice(0, displayCount).map((item) => (
                            <div key={item.total} className="park-group">
                                {item.parks.map((park, index) => (
                                    <div
                                        key={park.id}
                                        className="park-card"
                                        onMouseEnter={() => handleHover(park)}
                                        onMouseLeave={handleLeave}
                                        onFocus={() => setHoveredPark(park)}
                                    >
                                        <div className="park-card-header">
                                            <span className="park-rank">{parkData.indexOf(item) * item.parks.length + 1}</span>
                                            <h3 className="park-card-name" onClick={() => handleParkClick(park)}
                                                tabIndex={0}
                                                onKeyDown={(event) => {
                                                    if (event.key === 'Enter') {
                                                        handleParkClick(park);
                                                    }
                                                }}
                                                style={{
                                                    cursor: 'pointer',
                                                    display: 'inline-block',
                                                    color: selectedPark === park ? 'green' : 'black'
                                                }}>
                                                {park.fullName}
                                            </h3>
                                            {hoveredPark === park && (
                                                <button className="button_minus" onClick={() => handleButtonClick(park)}>-</button>
                                            )}
                                            <div className="park-rank-arrows" style={{ display: 'inline-block' }}>
                                                <button className="park-rank-arrow up" onClick={() => handleRankChange(park, '+')}>&#9650;</button>
                                                <button className="park-rank-arrow down" onClick={() => handleRankChange(park, '-')}>&#9660;</button>
                                            </div>
                                        </div>
                                        {expandedPark === park && (
                                            <div>
                                                <img className="park-image" src={park.images[0]?.url}
                                                     alt={park.fullName}/>
                                                <p className="park-location">
                                                    Location: {park.addresses[0].city},{' '}
                                                    <span
                                                        className="park-state-code"
                                                        onClick={() => handleStateCodeClick(park.addresses[0].stateCode)}
                                                        style={{
                                                            cursor: 'pointer',
                                                            textDecoration: 'underline'
                                                        }}
                                                        tabIndex={0}
                                                        onKeyDown={(e) => {
                                                            if (e.key === 'Enter') {
                                                                handleStateCodeClick(park.addresses[0].stateCode);
                                                            }
                                                        }}
                                                    >
                                                {park.addresses[0].stateCode}
                                                </span>
                                                </p>                                                <p
                                                className="park-entrance-fee">Entrance
                                                Fee: {park.entranceFees.length > 0 ? park.entranceFees[0].cost : 'Not available'}</p>
                                                <p className="park-description">Description: {park.description}</p>
                                                {park.activities && (
                                                    <p className="park-activities">
                                                        Activities:{' '}
                                                        {park.activities.map((activity, index) => (
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
                                                                {index !== park.activities.length - 1 && ', '}
                                                            </React.Fragment>
                                                        ))}
                                                    </p>
                                                )}
                                                {park.contacts.phoneNumbers && (
                                                    <p className="park-contact">Contact: {park.contacts.phoneNumbers.map(phone => phone.phoneNumber).join(', ')}</p>
                                                )}
                                                {item.amenities && item.amenities[park.parkCode] && (
                                                    <p className="park-amenities">
                                                        Amenities:{' '}
                                                        {item.amenities[park.parkCode].map((amenity, index) => (
                                                            <React.Fragment key={amenity}>
                                                            <span
                                                                className="park-amenity"
                                                                onClick={() => handleAmenityClick(amenity)}
                                                                style={{ cursor: 'pointer', textDecoration: 'underline' }}
                                                                tabIndex={0}
                                                                onKeyDown={(e) => {
                                                                    if (e.key === 'Enter') {
                                                                        handleAmenityClick(amenity);
                                                                    }
                                                                }}>
                                                                {amenity}
                                                            </span>
                                                            {index !== item.amenities[park.parkCode].length - 1 && ', '}
                                                            </React.Fragment>
                                                        ))}
                                                    </p>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        ))
                    ) : (
                        <div className="no-results-message">No favorites found.</div>
                    )}
                    {parkData && parkData.length > displayCount && (
                        <button
                            className="load-more-button"
                            onClick={() => setDisplayCount(displayCount + 10)}
                        >
                            Load More
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Favorites;