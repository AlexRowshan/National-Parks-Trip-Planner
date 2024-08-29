import React from 'react';
import { render, screen, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import App from "../App";

jest.mock('../pages/Login', () => () => <div>Login</div>);

describe('App', () => {
    beforeEach(() => {
        jest.useFakeTimers();
        sessionStorage.setItem('token', 'mockToken');
    });

    afterEach(() => {
        sessionStorage.clear();
        jest.clearAllTimers();
        jest.useRealTimers();
    });

    it('should clear session storage and navigate to login page after inactivity', () => {
        render(
            <MemoryRouter initialEntries={['/search']}>
                <App />
            </MemoryRouter>
        );

        expect(screen.getByText('Search')).toBeInTheDocument();

        act(() => {
            jest.advanceTimersByTime(60001);
        });

        expect(screen.getByText('Login')).toBeInTheDocument();
    });
});