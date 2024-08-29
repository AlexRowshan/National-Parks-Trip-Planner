import React, {useEffect, useState} from 'react';
import searchIcon from '../assets/searchIcon.svg';
import UpperArea from '../components/UpperArea';
import {useLocation, useSearchParams} from "react-router-dom";

const Search = () => {
    const [searchParams] = useSearchParams();
    const [searchType, setSearchType] = useState('name');
    const [searchTerm, setSearchTerm] = useState('');
    const [isRedirected, setIsRedirected] = useState(false);
    const location = useLocation();
    const [parks, setParks] = useState([]);
    const [selectedPark, setSelectedPark] = useState(null);
    const [showMore, setShowMore] = useState(false);
    const [parkLimit, setParkLimit] = useState(10);
    const [hoveredPark, setHoveredPark] = useState(null);
    const [noResultsFound, setNoResultsFound] = useState(false);
    const [isFavorite, setIsFavorite] = useState(false);
    const [internalClick, setInternalClick] = useState(false);


    const jwtToken = sessionStorage.getItem('token');

    useEffect(() => {
        const urlSearchType = searchParams.get('searchType') || null;
        const urlSearchTerm = searchParams.get('searchTerm') || '';

        if (location.search) {
            setIsRedirected(true);
            if (urlSearchType) {
                setSearchType(urlSearchType);
            }
            setSearchTerm(urlSearchTerm);
        }
    }, [location.search, searchParams]);

    useEffect(() => {
        if (isRedirected) {
            setParks([]);
            setParkLimit(10);
            fetchParks(searchTerm, 0);
            setIsRedirected(false);
        }
    }, [isRedirected, searchTerm, searchType]);

    useEffect(() => {
        if(internalClick)
        {
            setParks([]);
            setParkLimit(10);
            setInternalClick(false);
            fetchParks(searchTerm, 0);
        }
    }, [internalClick]);

    const fetchParks = async (term, start) => {
        try {
            if (term === "") {
                setNoResultsFound(true)
            } else {
                let params= {limit: parkLimit, start: start};

                if(searchType === 'name' || searchType === 'activity')
                {
                     console.log("searchType was by name or activity!!!!!");
                     console.log("This is the term: " + term);
                    if(term.length < 3) //Prevents searching by states ƒor activities or name buttons
                    {
                        setNoResultsFound(true);
                        return;
                    }
                    //Grab a response from the activity API.
                    const activityResponse = await fetch(`/api/getParkActivities?limit=${parkLimit}&start=${start}&q=${term}`, {
                        headers: {
                            'Authorization': `Bearer ${jwtToken}`
                        }
                    });

                    //If the response was good, the term might be an activity...
                    if(activityResponse.ok && searchType === 'activity')
                    {
                        const activityData = await activityResponse.json();
                        //Check if activity exists or if API returned unrelated data.
                        if (activityData.data && !activityData.data.some(activity => activity.name.toLowerCase().includes(term.toLowerCase()))) {
                            setNoResultsFound(true);
                            return;
                        }
                        //If the response from the activity API matched the search term, it means search term was actually an activity.
                        const parksArray = activityData.data.flatMap(activity => activity.parks);
                        const parkCodes = parksArray.flat().map(park => park.parkCode).slice(0,500);
                        params.parkCode = parkCodes.join(',');
                        //console.log("This is the park code: " + params.parkCode);
                    }
                    //If here, response was 100% not an activity but user selected activity search type.
                    else if(!activityResponse.ok && searchType === 'activity')
                    {
                        setNoResultsFound(true) //Therefore should not be returning any results.
                        return;
                    }
                    else //Otherwise,
                    {
                        //Maybe make it so you compare the term to park names and print out only those parks.
                        params.q = term;
                    }
                }
                else if(searchType === 'state')
                {
                   // console.log("searchType was by STATE!!!!!");
                    params.stateCode = term;
                }
                else if(searchType === 'amenities')
                {
                    //console.log("searchType was by amenityyyyyyyy!!!!!");
                    if(term.length < 3) //Prevents searching by states ƒor activities or name buttons
                    {
                        setNoResultsFound(true);
                        return;
                    }
                    //Need to call /amenities/parkplaces with amenity string to get park codes
                    //Then perform a search by park codes.
                    const amenityResponse = await fetch(`/api/getParkAmenities?start=${start}&q=${term}`, {
                        headers: {
                            'Authorization': `Bearer ${jwtToken}`
                        }
                    });
                    if(amenityResponse.ok)
                    {
                        const amenityData = await amenityResponse.json();
                        //Check if amenity exists or if API returned unrelated data.
                        if (amenityData.data && amenityData.data.length > 0) {
                            // Check each inner array for amenities containing the search term
                            const hasAmenityWithTerm = amenityData.data.some(innerArray => {
                                // Check if any amenity in the inner array contains the search term in its name
                                return innerArray.some(amenity => {
                                    return amenity.name.toLowerCase().includes(term.toLowerCase());
                                });
                            });

                            if (!hasAmenityWithTerm) {
                                // No amenity with the search term found, set noResultsFound to true
                                setNoResultsFound(true);
                                return;
                            }

                            // If here, at least one amenity with the search term is found
                            // Extract the park codes for all amenities containing the search term
                            const parksArray = amenityData.data.flatMap(innerArray => {
                                return innerArray.filter(amenity => amenity.name.toLowerCase().includes(term.toLowerCase()))
                                    .flatMap(amenity => amenity.parks)
                                    .map(park => park.parkCode);
                            });

                            params.parkCode = parksArray.join(',');
                        } else {
                            // No amenity data or empty array, set noResultsFound to true
                            setNoResultsFound(true);
                            return;
                        }
                        const parksArray = amenityData.data[0].map(amenity => amenity.parks);
                        const parkCodes = parksArray.flat().map(park => park.parkCode);
                        params.parkCode = parkCodes.join(',');
                    }
                    else
                    {
                        setNoResultsFound(true);
                        return;
                    }
                }
                    const searchParam = new URLSearchParams(params);
                    const response = await fetch(`/api/getParks?${searchParam.toString()}`,{
                        headers: {
                            'Authorization': `Bearer ${jwtToken}`
                        }
                    });
                    const data = await response.json();
                    const fetchedParks = data.parks || [];
                    const fetchedAmenities = data.amenities || {};
            //Merge the amenities data with the parks data...
            const parksWithAmenities = fetchedParks.map(park => {
                const parkCode = park.parkCode;
                park.amenities = fetchedAmenities[parkCode] || [];
                return park;
            });
                if (fetchedParks.length === 0) {
                    setNoResultsFound(true)
                } else {
                    setNoResultsFound(false)
                }
                setParks(prevParks => [...prevParks, ...parksWithAmenities]);
                setShowMore(start + fetchedParks.length < data.total);
            }
        } catch (error) {
            console.error('Error fetching parks:', error);
        }
    };

    const fetchFavorites = async (parkCode) => {
        try{
            const username = sessionStorage.getItem('username');

            const response = await fetch('/api/checkFavorites', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwtToken}`
                },
                body: JSON.stringify({ username, parkCode }),
            });

            if (response.ok) {
                return await response.json();
            } else {
                console.error('Failed to check favorite status: response not OK.');
                return false;
            }
        }catch(error)
        {
            console.error('Error checking favorite status:', error);
            return false;
        }

    };


    const handleShowMore = () => {
        fetchParks(searchTerm, parks.length);
    };

    const handleParkClick = async (park) => {
        if (selectedPark === park) {
            setSelectedPark(null);
            setIsFavorite(false);
        } else {
            const isFavorite = await fetchFavorites(park.parkCode);
            setSelectedPark(park);
            setIsFavorite(isFavorite);
        }
    };


    const handleSearchTypeChange = (event) => {
        setSearchType(event.target.value);
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            setParks([]);
            setParkLimit(10);
            fetchParks(searchTerm, 0);
        }
    };

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleSearchIconClick = () => {
        setParks([]);
        setParkLimit(10);
        fetchParks(searchTerm, 0);
    };

    const handleButtonClick = (park) => {
        // Get the park code from the park object
        const parkCode = park.parkCode;

        // Get the username (replace with your own logic to retrieve the username)
        const username = sessionStorage.getItem('username');

        // Make the API call to add the park to favorites
        fetch('/api/addToFavorites', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtToken}`
            },
            body: JSON.stringify({ username, parkCode }),
        })
            .then((response) => {
                if (response.ok) {
                    setIsFavorite(true);
                    return response.text();
                }
                else {
                    throw new Error('Park already in favorites.');
                }
            })
            .then((message) => {
                // Display the success message
                alert(message);
            })
            .catch((error) => {
                // Display the error message
                alert(error.message);
            });
    };

    const handleCloseNoResults = () =>{
        setNoResultsFound(false)
    }

    const handleActivityClick = (activityName) => {
        setSearchTerm(activityName.trim());
        setSearchType('activity');
        setInternalClick(true);
    };

    const handleStateCodeClick = (stateCode) => {
        setSearchTerm(stateCode);
        setSearchType('state'); // Update searchType before useEffect triggers
        setInternalClick(true);
    };

    const handleAmenityClick = (amenityName) => {
        setSearchTerm(amenityName.trim());
        setSearchType('amenities');
        setInternalClick(true);
    };

    return (
        <div>
            < UpperArea />
            <div className="SearchBarArea1">
                <div className="SearchBox">
                    <input
                        type="text"
                        className="search-box"
                        placeholder="Search"
                        value={searchTerm}
                        onChange={handleSearchChange}
                        onKeyDown={handleKeyPress}
                    />
                    <img src={searchIcon} alt={"Search Button"} onClick={handleSearchIconClick} className={"search-button"}/>
                </div>
                    <div className="SearchTypeLabel"> Search by:
                        <label>
                            <input
                                type="radio"
                                value="name"
                                className={"radio-button-name"}
                                checked={searchType === 'name'}
                                tabIndex={0}
                                onChange={handleSearchTypeChange}
                            />
                            Name
                        </label>
                        <label>
                            <input
                                type="radio"
                                value="state"
                                className={"radio-button-state"}
                                checked={searchType === 'state'}
                                tabIndex={0}
                                onChange={handleSearchTypeChange}
                            />
                            State
                        </label>
                        <label>
                            <input
                                type="radio"
                                value="activity"
                                className={"radio-button-activity"}
                                checked={searchType === 'activity'}
                                tabIndex={0}
                                onChange={handleSearchTypeChange}
                            />
                            Activity
                        </label>
                        <label>
                            <input
                                type="radio"
                                value="amenities"
                                className={"radio-button-amenities"}
                                checked={searchType === 'amenities'}
                                tabIndex={0}
                                onChange={handleSearchTypeChange}
                            />
                            Amenities
                        </label>
                    </div>
            </div>
            {noResultsFound ? (
                <div className="CenterColumn">
                    <div id="no-results" className="no-results-found">
                        NO RESULTS FOUND!
                        <button name="close-no-results" onClick={handleCloseNoResults} className="no-results-close-button">Close</button>
                    </div>
                </div>
            ):(
                <div className="CenterColumn">
                    <div className="centered-container-parks">
                        {parks.length === 0 ? (
                            <div className="no-results-message">
                                Enter a new search and what to search by
                            </div>
                        ) : (
                        <div className="park-list">
                            {parks.map((park) => (
                                <div key={park.id}
                                     className="park-card"
                                     onMouseEnter={() => setHoveredPark(park)}
                                     onMouseLeave={() => setHoveredPark(null)}
                                     onFocus={() => setHoveredPark(park)}
                                >
                                    <h2 className="park-card-name" onClick={() => handleParkClick(park)}
                                        tabIndex={0}
                                        onKeyDown={(event) => {
                                            if (event.key === 'Enter') {
                                                handleParkClick(park);
                                            }
                                        }}
                                        style={{
                                            cursor: 'pointer',
                                            marginBottom: 10,
                                            color: selectedPark === park ? 'green' : 'black'
                                        }}>
                                        {park.fullName}
                                    </h2>
                                    {hoveredPark === park && (
                                        <button
                                            className="button_plus"
                                            onClick={() => handleButtonClick(park)}
                                            tabIndex={0}
                                        >
                                            +
                                        </button>
                                    )}
                                    {selectedPark === park && (
                                        <div>
                                            <img className="park-image" src={park.images[0]?.url} alt={park.fullName}/>
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
                                            </p>
                                            <p className="park-entrance-fee">Entrance
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
                                            <p>Directions: <a className={"park-url"} href={park.directionsUrl}
                                                              target="_blank"
                                                              rel="noopener noreferrer">Official Website</a></p>
                                            {park.operatingHours && (
                                                <div>
                                                    <p className="park-operating-hours">Operating Hours:</p>
                                                    {park.operatingHours.map(hours => (
                                                        <p key={hours.name}>
                                                            {hours.name}: {Object.values(hours.standardHours).join(', ')}</p>
                                                    ))}
                                                </div>
                                            )}
                                            <p className="park-contact">Contact: {park.contacts.phoneNumbers.map((phone) => phone.phoneNumber).join(', ')}</p>
                                            {park.amenities && (
                                                <p className="park-amenities">
                                                    Amenities:{' '}
                                                    {park.amenities.map((amenity, index) => (
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
                                                            {index !== park.amenities.length - 1 && ', '}
                                                        </React.Fragment>
                                                    ))}
                                                </p>
                                            )}
                                            <p className="park-favorite?">Favorite?: {isFavorite ? 'Yes' : 'No'}</p>
                                        </div>
                                    )}
                                </div>
                            ))}
                            {showMore && (
                                <button type="button" onClick={handleShowMore} className="show-more-button">
                                    Show More
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </div>
            )}

        </div>
    );
}

export default Search;