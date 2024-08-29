import { useState } from 'react';
import '../styles/TagsInput.css';

const TagsInput = ({ onEnter }) => {
    const [tags, setTags] = useState([]);
    const [inputValue, setInputValue] = useState('');
    const token = sessionStorage.getItem('token');

    const checkUserPrivacy = async (myUsername) => {
        try {
            const response = await fetch(`/api/isPrivate?username=${myUsername}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });

            if (response.status === 404) {
                return 'not_found';
            }

            const data = await response.text();
            return data === 'true';
        } catch (error) {
            console.error('Error checking user privacy:', error);
            return false;
        }
    };

    const checkUserFavorites = async (myUsername) => {
        try {
            const response = await fetch(`/api/getFavorites?username=${myUsername}`, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                },
            });
            if (!response.ok) {
                return false;
            }
        } catch (error) {
            console.error('Error checking user favorites:', error);
            return false;
        }
        return true;
    };

    const handleKeyDown = async (e) => {
        if (e.key !== 'Enter') return;
        const value = e.target.value;
        if (!value.trim()) return;

        const encodedValue = btoa(value);

        const privacyStatus = await checkUserPrivacy(encodedValue);

        if (privacyStatus === 'not_found') {
            alert('The entered username does not exist.');
            return;
        }

        if (privacyStatus) {
            alert('The entered username belongs to a private user. Cannot compare parks.');
            return;
        }

        const hasFavorites = await checkUserFavorites(encodedValue);

        if (!hasFavorites) {
            alert('The entered username belongs to a user with no favorited parks or an error occurred.');
            return;
        }

        setTags([...tags, value]);
        onEnter([...tags, value]);
        setInputValue(''); // Clear input value after adding tag
    };

    const removeTag = (index) => {
        const updatedTags = tags.filter((el, i) => i !== index);
        setTags(updatedTags);
        onEnter(updatedTags);
    };

    const handleAddUser = async () => {
        const inputElement = document.getElementById('userInput');
        if (!inputElement) return;

        const value = inputElement.value.trim();
        if (!value) return;

        const encodedValue = btoa(value);

        const privacyStatus = await checkUserPrivacy(encodedValue);

        if (privacyStatus === 'not_found') {
            alert('The entered username does not exist.');
            return;
        }

        if (privacyStatus) {
            alert('The entered username belongs to a private user. Cannot compare parks.');
            return;
        }

        const hasFavorites = await checkUserFavorites(encodedValue);

        if (!hasFavorites) {
            alert('The entered username belongs to a user with no favorited parks or an error occurred.');
            return;
        }

        setTags([...tags, value]);
        onEnter([...tags, value]);
        setInputValue(''); // Clear input value after adding tag
    };

    const handleInputChange = (e) => {
        setInputValue(e.target.value);
    };


    return (
        <div className="tags-input-container">
            {tags.map((tag, index) => (
                <div className="tag-item" key={index}>
                    <span className="text">{tag}</span>
                    <span className="close" onClick={() => removeTag(index)}>&times;</span>
                </div>
            ))}
            <input
                id="userInput"
                value={inputValue}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
                type="text"
                className="tags-input"
                placeholder="Enter your friend's username"
            />
            <button className="add-user-button" onClick={handleAddUser}>
                Add User
            </button>
        </div>
    );
};

export default TagsInput;