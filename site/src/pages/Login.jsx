import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from '../components/Modal';
import logo from '../assets/National-Park-Service-logo.png';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [modalConfig, setModalConfig] = useState({
    showModal: false,
    title: '',
    content: '',
    isConfirmationModal: false,
    confirmButtonText: 'OK',
  });
  const navigate = useNavigate();

  const [loginAttempts, setLoginAttempts] = useState(0);
  const [lastLoginAttempt, setLastLoginAttempt] = useState(0);
  const [lockedUntil, setLockedUntil] = useState(0);

  const handleLogin = async (e) => {
    e.preventDefault();

    if (Date.now() < lockedUntil) {
      setModalConfig({
        showModal: true,
        title: 'Account Locked',
        content: 'Your account is temporarily locked due to multiple failed login attempts. Please try again later.',
        isConfirmationModal: false,
        confirmButtonText: 'OK',
      });
      return;
    }

    if (!username || !password) {
      setModalConfig({
        showModal: true,
        title: 'Error',
        content: 'Please fill in all fields.',
        isConfirmationModal: false,
        confirmButtonText: 'OK',
      });
      return;
    }

    try {
      const response = await fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        console.log('Login successful');
        const token = await response.text();
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('username', btoa(username));
        setLoginAttempts(0); // Reset login attempts on successful login
        navigate('/search');
      } else {
        const currentTime = Date.now();
        if (currentTime - lastLoginAttempt <= 60000) {
          setLoginAttempts(loginAttempts + 1);
          if (loginAttempts + 1 >= 3) {
            setLockedUntil(currentTime + 30000);
            setModalConfig({
              showModal: true,
              title: 'Account Locked',
              content: 'Your account has been locked for 5 seconds due to multiple failed login attempts.',
              isConfirmationModal: false,
              confirmButtonText: 'OK',
            });

            // Set a timeout to close the modal and reset the lockedUntil state after 5 seconds
            setTimeout(() => {
              setModalConfig({
                showModal: false,
                title: '',
                content: '',
                isConfirmationModal: false,
                confirmButtonText: '',
              });
              setLockedUntil(null);
            }, 5000);

            return;
          }
        } else {
          setLoginAttempts(1);
        }
        setLastLoginAttempt(currentTime);
        const errorMessage = await response.text();
        setModalConfig({
          showModal: true,
          title: 'Error',
          content: errorMessage,
          isConfirmationModal: false,
          confirmButtonText: 'OK',
        });
      }
    } catch (error) {
      console.error('Login error:', error);
      setModalConfig({
        showModal: true,
        title: 'Error',
        content: 'An error occurred during login.',
        isConfirmationModal: false,
        confirmButtonText: 'OK',
      });
    }
  };

  const closeModal = () => {
    setModalConfig({ ...modalConfig, showModal: false });
  };

  return (
      <div className="centered-container">
          <img src={logo} alt="Logo" style={{width: '200px', height: 'auto', marginBottom: '20px'}}/>
          <h1 className="app-title" role="heading" aria-level="1">Let's Go Camping (Team 19) </h1>
        <h2 className="app-title">Log in</h2>
        <form onSubmit={handleLogin}>
          <input
              id = "username-input"
              className="input-field"
              type="text"
              placeholder="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
          />
          <input
              id = "password-input"
              className="input-field"
              type="password"
              placeholder="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
          />
          <div className="button-container">
            <button type="submit" className="login-button">Log in</button>
            <button type="button" className="create-account-button" onClick={() => navigate('/create-user')}>Create
              account
            </button>
          </div>
        </form>
        {modalConfig.showModal && <Modal
            title={modalConfig.title}
            content={modalConfig.content}
            onConfirm={closeModal}
            showCancelButton={false}
            confirmButtonText={modalConfig.confirmButtonText}
        />}
      </div>
  );
}

export default Login;

