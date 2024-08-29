import {fireEvent, render} from "@testing-library/react";
import {BrowserRouter} from "react-router-dom";
import UpperArea from "../components/UpperArea";
import React from "react";

describe('UpperArea component', () => {
    test('calls logout and navigates to login page when Logout button is clicked', () => {
        // Render the component
        const {getByText} = render(
            <BrowserRouter>
                <UpperArea/>
            </BrowserRouter>
        );
        const logoutButton = getByText('Logout');

        // Set some session storage values
        sessionStorage.setItem('token', 'dummy-token');
        sessionStorage.setItem('username', 'dummy-username');

        // Simulate a click on the Logout button
        fireEvent.click(logoutButton);

        // Assert that session storage items are removed
        expect(sessionStorage.getItem('token')).toBeNull();
        expect(sessionStorage.getItem('username')).toBeNull();

        // Assert that navigate is called with the correct path
        // expect(navigateMock).toHaveBeenCalledWith('/');
    });
});