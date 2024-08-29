import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Modal from '../components/Modal';
import logo from "../assets/National-Park-Service-logo.png";

function CreateUser() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [verifyPassword, setVerifyPassword] = useState('');
  const [modalConfig, setModalConfig] = useState({
    showModal: false,
    title: '',
    content: '',
    isConfirmationModal: false,
    confirmButtonText: 'OK',
    cancelButtonText: 'Cancel'
  });
  const navigate = useNavigate();

  const validatePassword = (password) => {
    const uppercaseRegex = /[A-Z]/;
    const lowercaseRegex = /[a-z]/;
    const numberRegex = /[0-9]/;

    return (
        uppercaseRegex.test(password) &&
        lowercaseRegex.test(password) &&
        numberRegex.test(password)
    );
  };


  const handleCreateUser = async (e) => {
    e.preventDefault();
    if (!username || !password || password !== verifyPassword) {
      setModalConfig({
        showModal: true,
        title: 'Error',
        content: 'Passwords do not match.',
        isConfirmationModal: false,
        confirmButtonText: 'OK',
      });
      return;
    }

    if (!validatePassword(password)) {
      setModalConfig({
        showModal: true,
        title: 'Error',
        content: 'Password must contain at least one uppercase letter, one lowercase letter, and one number.',
        isConfirmationModal: false,
        confirmButtonText: 'OK',
      });
      return;
    }

    try {
      const response = await fetch('/api/createAccount', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        console.log('User created');
        const token = await response.text();
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('username', btoa(username));
        navigate('/search');
      } else {
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
      console.error('User creation error:', error);
      setModalConfig({
        showModal: true,
        title: 'Error',
        content: 'An error occurred during user creation.',
        isConfirmationModal: false,
        confirmButtonText: 'OK',
      });
    }
  };

      const handleCancel = (e) => {
         e.preventDefault();
        setModalConfig({
          showModal: true,
          title: 'Confirmation',
          content: 'Are you sure you want to cancel account creation and navigate back to the home page?',
          isConfirmationModal: true,
          confirmButtonText: 'Yes',
          cancelButtonText: 'No',
        });
      console.log('Cancel successful');
    };

  const closeModal = () => {
    setModalConfig({ ...modalConfig, showModal: false });
  };

  return (
      <div className="centered-container">
        <img src={logo} alt="Logo" style={{width: '200px', height: 'auto', marginBottom: '20px', marginLeft: '-25px'}}/>
        <h1 className="app-title" role="heading" aria-level="1">Let's Go Camping (Team 19) </h1>
        <h2 className="app-title">Create account</h2>
        <form onSubmit={handleCreateUser}>
          <input id={"username-box"} className="input-field" type="text" placeholder="username" value={username}
                 onChange={(e) => setUsername(e.target.value)}/>
          <input id={"password-box"} className="input-field" type="password" placeholder="password" value={password}
                 onChange={(e) => setPassword(e.target.value)}/>
          <input id={"confirm-password-box"} className="input-field" type="password" placeholder="verify password"
                 value={verifyPassword} onChange={(e) => setVerifyPassword(e.target.value)}/>
          <div className="button-container">
            <button type="submit" className="create-account-button">Create account</button>
            <button type="button" className="cancel-button" onClick={handleCancel}>Cancel</button>
          </div>
        </form>
        {modalConfig.showModal && <Modal
            title={modalConfig.title}
            content={modalConfig.content}
            onConfirm={() => modalConfig.isConfirmationModal ? navigate('/') : closeModal()}
            onCancel={closeModal}
            showCancelButton={modalConfig.isConfirmationModal}
            confirmButtonText={modalConfig.confirmButtonText}
            cancelButtonText={modalConfig.cancelButtonText}
        />}
      </div>
  );
}

export default CreateUser;
