import React from "react";
import logo from "../assets/National-Park-Service-logo.png";

const NotFound = () => {
    return (
        <div>
            <img src={logo} alt="Logo"
                 style={{width: '200px', height: 'auto', marginBottom: '20px', marginLeft: '-25px'}}/>
            <h1 className="app-title">Let's Go Camping (Team 19)</h1>
            <div className={"access-denied"}>
                <h1>Access Denied</h1>
                <p>You do not have permission to access this page.</p>
                <p>
                    <a href="/" style={{ color: "white" }}>
                        Go back to the homepage.
                    </a>
                </p>
            </div>
            <style>{`
                body {
                    background-color: rgb(176, 115, 76);
                    color: white;
                    font-family: Arial, sans-serif;
                    text-align: center;
                }
                h1 {
                    margin-top: 50px;
                }
            `}</style>
        </div>
    );
};

export default NotFound;