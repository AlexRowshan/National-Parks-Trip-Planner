import React from 'react';
import logo from '../assets/National-Park-Service-logo.png';
import '../styles/UpperArea.css';
import { Link, useNavigate } from "react-router-dom";

const UpperArea = () => {
    const navigate = useNavigate();

    const logout = () => {
        sessionStorage.removeItem("token");
        sessionStorage.removeItem("username");
        navigate("/"); // Redirect to the login page after logout
    }

    return (
        <div className="UpperArea">
            <div className="Left-NavHeaders">
                <img src={logo} alt="NPS Logo" className="Logo"/>
                <h1 className="Slogan">Let's Go Camping (Team 19)</h1>
            </div>
            <div className="NavLinks">
                <Link to="/search" className="SearchLink" alt={"search link"}>Search</Link>
                <Link to="/favorites" className="FavoritesLink" alt={"favorites link"}>Favorites</Link>
                <Link to="/suggest" className="SuggestLink" alt={"suggest link"}>Suggest</Link>
                <Link to="/compare" className="CompareLink" alt={"compare link"}>Compare</Link>
                <button className="LogoutButton" onClick={logout}>Logout</button>
            </div>
        </div>
    );
};

export default UpperArea;