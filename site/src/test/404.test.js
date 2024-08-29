import React from 'react';
import { render, screen } from '@testing-library/react';
import NotFound from "../pages/404";

describe('NotFound', () => {
    test('renders the 404 message', () => {
        render(<NotFound />);
        const notFoundMessage = screen.getByText(/Access Denied/i);
        expect(notFoundMessage).toBeInTheDocument();
    });

    test('renders the link to the home page', () => {
        render(<NotFound />);
        const homeLink = screen.getByRole('link', { name: /Go back to the homepage./i });
        expect(homeLink).toBeInTheDocument();
        expect(homeLink).toHaveAttribute('href', '/');
    });
});