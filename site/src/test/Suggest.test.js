import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { useNavigate } from 'react-router-dom';
import Suggest from "../pages/Suggest";
import * as ReactRouterDOM from 'react-router-dom';

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: jest.fn(),
}));

jest.mock('../components/UpperArea', () => () => <div>Mocked UpperArea</div>);

describe('Suggest component', () => {
    beforeEach(() => {
        sessionStorage.setItem('username', 'testUser');
        sessionStorage.setItem('token', 'testToken');
    });

    afterEach(() => {
        sessionStorage.clear();
        jest.clearAllMocks();
    });

    it('renders correctly', () => {
        render(<Suggest />);
        expect(screen.getByText('Suggest a park to visit!')).toBeInTheDocument();
        expect(screen.getByPlaceholderText('Enter your friend\'s username')).toBeInTheDocument();
        expect(screen.getByText('Suggest!')).toBeInTheDocument();
    });

    it('updates search term on input change', () => {
        render(<Suggest />);
        const searchInput = screen.getByPlaceholderText('Enter your friend\'s username');
        fireEvent.change(searchInput, { target: { value: 'John, Jane' } });
        expect(searchInput.value).toBe('John, Jane');
    });

    it('updates usernamesSuggest and clears error message on handleSuggestSearchTags', () => {
        render(<Suggest />);
        const tags = ['user1', 'user2'];
        fireEvent.submit(screen.getByPlaceholderText('Enter your friend\'s username'), { target: { value: tags.join(',') } });
        expect(screen.queryByText('error-message')).not.toBeInTheDocument();
    });

    // it('displays error message on handleUserPrivacyError', () => {
    //     render(<Suggest />);
    //     const errorMessage = 'User privacy error';
    //     fireEvent.submit(screen.getByPlaceholderText('Enter your friend\'s username'), { target: { value: 'private-user' } });
    //     expect(screen.getByText(errorMessage)).toBeInTheDocument();
    // });

    it('fetches suggest parks on Enter key press', () => {
        render(<Suggest />);
        const searchInput = screen.getByPlaceholderText('Enter your friend\'s username');
        fireEvent.change(searchInput, { target: { value: 'user1,user2' } });
        fireEvent.keyDown(searchInput, { key: 'Enter', code: 'Enter' });
        expect(global.fetch).toHaveBeenCalledTimes(1);
    });

    // it('displays error message on 400 status response', async () => {
    //     global.fetch = jest.fn().mockResolvedValueOnce({
    //         status: 400,
    //         text: () => Promise.resolve('Error message'),
    //     });
    //     render(<Suggest />);
    //     const suggestButton = screen.getByText('Suggest!');
    //     fireEvent.click(suggestButton);
    //     await waitFor(() => {
    //         expect(screen.getByText('Error message')).toBeInTheDocument();
    //     });
    // });

    it('expands and collapses park details on handleParkClick', async () => {

        const parkData = {
            data: [
                {
                    id: 1,
                    fullName: 'Test Park',
                    addresses: [{ city: 'Test City', stateCode: 'TS' }],
                    description: 'Test Description',
                    activities: [{ name: 'Test Activity' }],
                    images: [{ url: 'test-image-url', altText: 'Test Image' }],
                    parkCode: 'TP',
                },
            ],
            amenities: { TP: ['Test Amenity'] },
        };
        global.fetch = jest.fn().mockResolvedValueOnce({
            json: () => Promise.resolve(parkData),
        });
        render(<Suggest />);
        const suggestButton = screen.getByText('Suggest!');
        fireEvent.click(suggestButton);
        await waitFor(() => {
            expect(screen.getByText('Test Park')).toBeInTheDocument();
        });
        const parkNameElement = screen.getByText('Test Park');
        fireEvent.click(parkNameElement);

        expect(screen.getByText(/TS/)).toBeInTheDocument();

        fireEvent.click(parkNameElement);
        await waitFor(() => {
            expect(screen.queryByText('TS')).not.toBeInTheDocument();
        });
    });

    it('updates search term and usernamesSuggest on handleSuggestSearchChange', () => {
        render(<Suggest />);
        const searchInput = screen.getByPlaceholderText('Enter your friend\'s username');
        fireEvent.change(searchInput, { target: { value: 'John, Jane' } });
        expect(searchInput.value).toBe('John, Jane');
    });

    it('fetches suggest parks on button click', async () => {
        global.fetch = jest.fn().mockResolvedValueOnce({
            json: () => Promise.resolve({ data: [{ id: 1, fullName: 'Test Park' }] }),
        });
        render(<Suggest />);
        const suggestButton = screen.getByText('Suggest!');
        fireEvent.click(suggestButton);
        await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));
        await waitFor(() => {
            expect(screen.getByText('Test Park')).toBeInTheDocument();
        });
    });

    it('handles park click', () => {
        render(<Suggest />);
        const parkNameElement = screen.getByText('No park data available.');
        fireEvent.click(parkNameElement);
        expect(screen.queryByText('Location:')).not.toBeInTheDocument();
        fireEvent.click(parkNameElement);
        expect(screen.queryByText('Location:')).not.toBeInTheDocument();
    });

    it('navigates on activity click', () => {
        const navigateMock = jest.fn();
        useNavigate.mockReturnValueOnce(navigateMock);
        render(<Suggest />);
        const activityElement = screen.getByText('No park data available.');
        fireEvent.click(activityElement);
        expect(navigateMock).not.toHaveBeenCalled();
    });

    it('navigates on state code click', () => {
        const navigateMock = jest.fn();
        useNavigate.mockReturnValueOnce(navigateMock);
        render(<Suggest />);
        const stateCodeElement = screen.getByText('No park data available.');
        fireEvent.click(stateCodeElement);
        expect(navigateMock).not.toHaveBeenCalled();
    });

    it('navigates on amenity click', () => {
        const navigateMock = jest.fn();
        useNavigate.mockReturnValueOnce(navigateMock);
        render(<Suggest />);
        const amenityElement = screen.getByText('No park data available.');
        fireEvent.click(amenityElement);
        expect(navigateMock).not.toHaveBeenCalled();
    });

    it('displays park details when expandedPark is set', async () => {
        const parkData = {
            data: [
                {
                    id: 1,
                    fullName: 'Test Park',
                    addresses: [{ city: 'Test City', stateCode: 'TS' }],
                    description: 'Test Description',
                    activities: [{ name: 'Test Activity' }],
                    images: [{ url: 'test-image-url', altText: 'Test Image' }],
                    parkCode: 'TP',
                },
            ],
            amenities: { TP: ['Test Amenity'] },
        };
        global.fetch = jest.fn().mockResolvedValueOnce({
            json: () => Promise.resolve(parkData),
        });
        render(<Suggest />);
        const suggestButton = screen.getByText('Suggest!');
        fireEvent.click(suggestButton);
        await waitFor(() => {
            expect(screen.getByText('Test Park')).toBeInTheDocument();
        });
        const parkNameElement = screen.getByText('Test Park');
        fireEvent.click(parkNameElement);

        expect(screen.getByText(/TS/)).toBeInTheDocument();
        expect(screen.getByText('Test Activity')).toBeInTheDocument();
        expect(screen.getByText('Test Amenity')).toBeInTheDocument();
    });

    it('displays "No park data available" when no parks are returned', async () => {
        global.fetch = jest.fn().mockResolvedValueOnce({
            json: () => Promise.resolve({ data: [] }),
        });
        render(<Suggest />);
        const suggestButton = screen.getByText('Suggest!');
        fireEvent.click(suggestButton);
        await waitFor(() => {
            expect(screen.getByText('No park data available.')).toBeInTheDocument();
        });
    });

    // it('navigates to search page when activity is clicked', async () => {
    //     const parkData = {
    //         data: [
    //             {
    //                 id: 1,
    //                 fullName: 'Test Park',
    //                 activities: [{ name: 'Test Activity' }],
    //             },
    //         ],
    //     };
    //     global.fetch = jest.fn().mockResolvedValueOnce({
    //         json: () => Promise.resolve(parkData),
    //     });
    //     const navigateMock = jest.fn();
    //     useNavigate.mockReturnValueOnce(navigateMock);
    //     render(<Suggest />);
    //     const suggestButton = screen.getByText('Suggest!');
    //     fireEvent.click(suggestButton);
    //     await waitFor(() => {
    //         expect(screen.getByText('Test Park')).toBeInTheDocument();
    //     });
    //     const parkNameElement = screen.getByText('Test Park');
    //     fireEvent.click(parkNameElement);
    //     const activityElement = screen.getByText('Test Activity');
    //     fireEvent.click(activityElement);
    //     expect(navigateMock).toHaveBeenCalledWith('/search?searchType=activity&searchTerm=Test%20Activity');
    // });
    //
    it('navigates to search page when state code is clicked', async () => {
        const parkData = {
            data: [
                {
                    id: 1,
                    fullName: 'Test Park',
                    addresses: [{ city: 'Test City', stateCode: 'TS' }],
                },
            ],
        };
        global.fetch = jest.fn().mockResolvedValueOnce({
            json: () => Promise.resolve(parkData),
        });

        // Mock the useNavigate hook
        const navigateMock = jest.fn();
        jest.spyOn(ReactRouterDOM, 'useNavigate').mockReturnValue(navigateMock);

        render(<Suggest />);
        const suggestButton = screen.getByText('Suggest!');
        fireEvent.click(suggestButton);
        await waitFor(() => {
            expect(screen.getByText('Test Park')).toBeInTheDocument();
        });
        const parkNameElement = screen.getByText('Test Park');
        fireEvent.click(parkNameElement);
        await waitFor(() => {
            expect(screen.getByText('TS')).toBeInTheDocument();
        });
        const stateCodeElement = screen.getByText('TS');
        fireEvent.click(stateCodeElement);
        expect(navigateMock).toHaveBeenCalledWith('/search?searchType=state&searchTerm=TS');
    });
    //
    // it('navigates to search page when amenity is clicked', async () => {
    //     const parkData = {
    //         data: [
    //             {
    //                 id: 1,
    //                 fullName: 'Test Park',
    //                 parkCode: 'TP',
    //             },
    //         ],
    //         amenities: { TP: ['Test Amenity'] },
    //     };
    //     global.fetch = jest.fn().mockResolvedValueOnce({
    //         json: () => Promise.resolve(parkData),
    //     });
    //     const navigateMock = jest.fn();
    //     useNavigate.mockReturnValueOnce(navigateMock);
    //     render(<Suggest />);
    //     const suggestButton = screen.getByText('Suggest!');
    //     fireEvent.click(suggestButton);
    //     await waitFor(() => {
    //         expect(screen.getByText('Test Park')).toBeInTheDocument();
    //     });
    //     const parkNameElement = screen.getByText('Test Park');
    //     fireEvent.click(parkNameElement);
    //     const amenityElement = screen.getByText('Test Amenity');
    //     fireEvent.click(amenityElement);
    //     expect(navigateMock).toHaveBeenCalledWith('/search?searchType=amenities&searchTerm=Test%20Amenity');
    // });

    it('navigates to search page when activity is clicked 1', async () => {
        const parkData = {
            data: [
                {
                    id: 1,
                    fullName: 'Test Park',
                    addresses: [
                        {
                            city: 'Test City',
                            stateCode: 'TS',
                        },
                    ],
                    description: 'Test Description',
                    activities: [{ name: 'Test Activity' }],
                },
            ],
            amenities: {
                'Test Park': ['Test Amenity'],
            },
        };
        global.fetch = jest.fn().mockResolvedValueOnce({
            json: () => Promise.resolve(parkData),
        });

        // Mock the useNavigate hook
        const navigateMock = jest.fn();
        jest.spyOn(ReactRouterDOM, 'useNavigate').mockReturnValue(navigateMock);

        render(<Suggest />);
        const suggestButton = screen.getByText('Suggest!');
        fireEvent.click(suggestButton);
        await waitFor(() => {
            expect(screen.getByText('Test Park')).toBeInTheDocument();
        });
        const parkNameElement = screen.getByText('Test Park');
        fireEvent.click(parkNameElement);
        await waitFor(() => {
            expect(screen.getByText('Test Activity')).toBeInTheDocument();
        });
        const activityElement = screen.getByText('Test Activity');
        fireEvent.click(activityElement);
        expect(navigateMock).toHaveBeenCalledWith('/search?searchType=activity&searchTerm=Test%20Activity');
    });
});