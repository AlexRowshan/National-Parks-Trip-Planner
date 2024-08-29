import React, {useEffect, useState} from 'react';
import {Routes, Route, Navigate, useNavigate} from 'react-router-dom';
import CreateUser from "./pages/CreateUser";
import Login from "./pages/Login";
import Search from "./pages/Search";
import Compare from "./pages/Compare";
import Favorites from "./pages/Favorites";
import NotFound from "./pages/404";
const PrivateRoute = ({ element: Element, ...rest }) => {
    const isAuthenticated = () => {
        const token = sessionStorage.getItem('token');
        // Add your token validation logic here
        return token !== null;
    };

    return isAuthenticated() ? (
        <Element {...rest} />
    ) : (
        <Navigate to="/404" replace />
    );
};

function App() {
    const [lastActivity, setLastActivity] = useState(Date.now());
    const navigate = useNavigate();

    useEffect(() => {
        const handleActivity = () => {
            setLastActivity(Date.now());
        };

        window.addEventListener('mousemove', handleActivity);
        window.addEventListener('keydown', handleActivity);

        const checkInactivity = setInterval(() => {
            if (Date.now() - lastActivity > 30000) {
                sessionStorage.clear();
                navigate('/');
            }
        }, 1000);

        return () => {
            window.removeEventListener('mousemove', handleActivity);
            window.removeEventListener('keydown', handleActivity);
            clearInterval(checkInactivity);
        };
    }, [lastActivity, navigate]);

    return (
        <div className="app-container">
            <Routes>
                <Route path="/" element={<Login />} />
                <Route path="/create-user" element={<CreateUser />} />
                <Route path="/404" element={<NotFound />} />
                <Route path="/search" element={<PrivateRoute element={Search} />} />
                <Route path="/compare" element={<PrivateRoute element={Compare} />} />
                <Route path="/favorites" element={<PrivateRoute element={Favorites} />} />
            </Routes>
        </div>
    );
}

export default App;