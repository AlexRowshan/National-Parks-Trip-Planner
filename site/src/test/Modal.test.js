import React from 'react';
import { render, screen } from '@testing-library/react';
import Modal from '../components/Modal';

describe('Modal', () => {
    it('renders the modal content correctly', () => {
        const title = 'Test Modal';
        const content = 'This is a test modal.';

        render(<Modal title={title} content={content} />);

        expect(screen.getByText(title)).toBeInTheDocument();
        expect(screen.getByText(content)).toBeInTheDocument();
    });
});