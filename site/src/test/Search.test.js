import React from "react";
import {render, screen, fireEvent, waitFor} from '@testing-library/react';
import Search from "../pages/Search";
import {BrowserRouter, MemoryRouter} from "react-router-dom";
import { act } from 'react-dom/test-utils';

beforeEach(() => {
    fetch.resetMocks();
});

afterEach(() => {
    window.history.pushState(null, document.title, "/");
});

describe("Search Page", () => {

    test("Toggle park card details by clicking the same park name twice", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );


        // Trigger the initial fetch to load parks
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "park1" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });


        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");


        // Simulate the first click to expand the park card details
        fireEvent.click(parkName);


        // Simulate the second click on the same park card to collapse the details
        fireEvent.click(parkName);


        // Use waitFor to give the UI time to update
        await waitFor(() => {
            // Check that the description is not in the document anymore, indicating the details are collapsed
            expect(screen.queryByText("Description: Description")).not.toBeInTheDocument();
        });
    });


    test("Fetching Favorites - Successful", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });


        const username = "testuser";
        sessionStorage.setItem("username", username);


        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );


        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        await screen.findByText("Park 1");

        const park1Element = screen.getByText("Park 1");
        expect(park1Element).toBeInTheDocument();

        fireEvent.click(park1Element);
    });


    test("Fetching Favorites - Non-OK Response", async () => {
        // Setup the mock data and a non-OK response status for fetching favorites
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });


        const username = "testuser";
        sessionStorage.setItem("username", username);


        fetch.mockResponseOnce(JSON.stringify({ message: "Failed to check favorite status" }), { status: 500 }); // Simulating a server error for favorites check


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );


        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });


        await screen.findByText("Park 1");
        fireEvent.click(screen.getByText("Park 1")); // Triggering the fetch for favorites


        // Assertions to ensure the fetch was called as expected
        expect(fetch).toHaveBeenCalledTimes(3); // Ensure fetch was called three time (one for activities, one for parks, and one for favorites)
        // Optionally, check for a UI element that might change based on the failed favorite fetch
        // For example, if you have a message or a log statement, ensure it's present or called.
    });

    test("Initial State", () => {
        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        expect(screen.getByText("Let's Go Camping (Team 19)")).toBeInTheDocument();
    });

    test("Search Type Radio Buttons", () => {
        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        expect(screen.getByText("Name")).toBeInTheDocument();
        expect(screen.getByText("State")).toBeInTheDocument();
        expect(screen.getByText("Activity")).toBeInTheDocument();
    });

    test("Clicking Search Type Radio Buttons", () => {
        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        const nameRadio = screen.getByLabelText("Name");
        const stateRadio = screen.getByLabelText("State");
        const activityRadio = screen.getByLabelText("Activity");

        expect(nameRadio).toBeChecked();
        expect(stateRadio).not.toBeChecked();
        expect(activityRadio).not.toBeChecked();

        fireEvent.click(stateRadio);
        expect(nameRadio).not.toBeChecked();
        expect(stateRadio).toBeChecked();
        expect(activityRadio).not.toBeChecked();

        fireEvent.click(activityRadio);
        expect(nameRadio).not.toBeChecked();
        expect(stateRadio).not.toBeChecked();
        expect(activityRadio).toBeChecked();
    });


    test("Fetching Parks", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");

        await act(async () => {
            fireEvent.change(searchInput, { target: { value: "park" } });
            fireEvent.click(screen.getByAltText("Search Button"));
        });

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Show More Button", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 11,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        expect(await screen.findByText("Show More")).toBeInTheDocument();
        fireEvent.click(screen.getByText("Show More"));
        expect(fetch).toHaveBeenCalledTimes(3);
    });

    test("Clicking Park Name", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByAltText("Search Button"));
        });


        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();

        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});

        act(() => {
            fireEvent.click(screen.getByText("Park 1"));
        });

        expect(await screen.findByText("State")).toBeInTheDocument();
        expect(await screen.findByText("Entrance Fee: 10")).toBeInTheDocument();
        expect(await screen.findByText("Description: Description")).toBeInTheDocument();
        expect(await screen.findByText("Operating Hours:")).toBeInTheDocument();
        expect(await screen.getByText("Hours: 9:00AM - 5:00PM")).toBeInTheDocument();
        expect(await screen.getByText("Contact: 123-456-7890")).toBeInTheDocument();
        expect(await screen.getByText("Activity 1")).toBeInTheDocument();
        expect(await screen.getByText("Amenity 1")).toBeInTheDocument();

        const officialWebsiteLink = screen.getByRole('link', { name: 'Official Website' });
        expect(officialWebsiteLink).toBeInTheDocument();
        expect(officialWebsiteLink).toHaveAttribute('href', 'directions-url');

        const parkNameElement = screen.getByText("Park 1");

        // Simulate pressing Enter on the park name element
        act(() => {
            fireEvent.keyDown(parkNameElement, { key: "Enter", code: "Enter" });
        });
        expect(screen.queryByText("Entrance Fee: 10")).not.toBeInTheDocument();
    });

    test("Clicking Search Icon", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});
    });

    test("Fetching Parks - Empty Search", async () => {
        const mockData = {
            total: 0,
            data: null,
        };
        fetch.mockResponseOnce(JSON.stringify(mockData));

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        expect(screen.queryByText("Show More")).not.toBeInTheDocument();

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();

    });

    test("Fetching Parks - Empty Response", async () => {
        const mockData = {
            total: 0,
            parks: null,
        };
        const mockActivityData = {
            data: null,
        };
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(mockActivityData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock an unsuccessful response for checking favorites


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "fjdkslaafsdjfda" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        expect(screen.queryByText("Show More")).not.toBeInTheDocument();

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();

    });

    test("Clicking Close Button of No Results Found Pop Up", async () => {
        const mockActivityData = {
            data: null,
        };
        const mockData = {
            total: 0,
            data: null,
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData));
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock a successful response for checking favorites


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "fjafdslafdskfdja" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        // Wait for "NO RESULTS FOUND!" to appear before clicking the "Close" button
        const noResultsMessage = await screen.findByText("NO RESULTS FOUND!");
        expect(noResultsMessage).toBeInTheDocument();

        const closeButton = screen.getByText("Close");
        fireEvent.click(closeButton);

        // Now, "NO RESULTS FOUND!" should not be present
        expect(screen.queryByText("NO RESULTS FOUND!")).not.toBeInTheDocument();

        // This line may not be necessary unless you have a specific need to check for this message
        // after closing the "No Results" message.
        // expect(await screen.findByText("Enter a new search and what to search by")).toBeInTheDocument();
    });

     test("Clicking Park Name - Entrance Fee Not Available", async () => {
         const mockActivityData = {
             data: [
                 {
                     name: "Activity 1",
                     parks: [
                         { parkCode: "park1" },
                         { parkCode: "park2" },
                     ],
                 },
             ],
         };

         const mockParkData = {
             total: 2,
             parks: [
                 {
                     id: 1,
                     fullName: "Park 1",
                     parkCode: "park1",
                     images: [{ url: "image-url" }],
                     addresses: [{ city: "City", stateCode: "State" }],
                     entranceFees: [], //Empty entrance fees array
                     description: "Description",
                     activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                     directionsUrl: "directions-url",
                     operatingHours: [
                         { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                     ],
                     contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                     amenities: ["Amenity 1", "Amenity 2"],
                 },
                 {
                     id: 2,
                     fullName: "Park 2",
                     parkCode: "park2",
                     // Other park data
                 },
             ],
             amenities: {
                 park1: ["Amenity 1", "Amenity 2"],
                 park2: ["Amenity 3", "Amenity 4"],
             },
         };

         fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
         fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
         fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

         render(
             <BrowserRouter>
                 <Search />
             </BrowserRouter>
         );

         const searchInput = screen.getByPlaceholderText("Search");
         fireEvent.change(searchInput, { target: { value: "test" } });

         expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
         expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

         act(() => {
             fireEvent.click(screen.getByAltText("Search Button"));
         });


         expect(await screen.findByText("Park 1")).toBeInTheDocument();
         expect(screen.getByText("Park 2")).toBeInTheDocument();

         expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});

         act(() => {
             fireEvent.click(screen.getByText("Park 1"));
         });
         expect(await screen.findByText("Entrance Fee: Not available")).toBeInTheDocument();
         expect(await screen.findByText("Description: Description")).toBeInTheDocument();
         expect(await screen.findByText("Operating Hours:")).toBeInTheDocument();
         expect(await screen.getByText("Hours: 9:00AM - 5:00PM")).toBeInTheDocument();
         expect(await screen.getByText("Contact: 123-456-7890")).toBeInTheDocument();
         expect(await screen.getByText("Activity 1")).toBeInTheDocument();
         expect(await screen.getByText("Amenity 1")).toBeInTheDocument();

         const officialWebsiteLink = screen.getByRole('link', { name: 'Official Website' });
         expect(officialWebsiteLink).toBeInTheDocument();
         expect(officialWebsiteLink).toHaveAttribute('href', 'directions-url');

         fireEvent.click(screen.getByText("Park 1"));
         expect(screen.queryByText("Entrance Fee: Not available")).not.toBeInTheDocument();
     });

    test("Pressing Non-Enter Key", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites


        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();

        fireEvent.keyDown(searchInput, { key: "Escape" });

        expect(screen.getByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Clicking Hover Button", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites


        const username = "testuser";
        sessionStorage.setItem("username", username);

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });
        fireEvent.keyDown(searchInput, { key: "Enter" });

        await screen.findByText("Park 1");

        // Simulate hovering over the park card and clicking the "+" button
        const parkCard = screen.getByText("Park 1").closest(".park-card");
        fireEvent.mouseEnter(parkCard);

        // Assert that the "+" button is visible
        expect(screen.getByRole("button", { name: "+" })).toBeInTheDocument();

        // Simulate the mouse leaving the park card
        fireEvent.mouseLeave(parkCard);

        // Assert that the "+" button is no longer visible
        expect(screen.queryByRole("button", { name: "+" })).not.toBeInTheDocument();

        // Mock a successful response for adding to favorites
        fetch.mockResponseOnce(JSON.stringify("Park added to favorites"));

        // Simulate hovering over the park card again and clicking the "+" button
        fireEvent.mouseEnter(parkCard);
        fireEvent.click(screen.getByRole("button", { name: "+" }));

        // Mock an error response for the second attempt to add to favorites
        fetch.mockResponseOnce(JSON.stringify("Park already in favorites"), { status: 404 });

        fireEvent.click(screen.getByRole("button", { name: "+" }));

        // Assertions to ensure the fetch was called as expected
        expect(fetch).toHaveBeenCalledTimes(4);
    });

    test("Clicking Favorites Link", () => {
        const navigateMock = jest.fn();
        render(
            <BrowserRouter>
                <Search navigate={navigateMock} />
            </BrowserRouter>
        );

        // assert that the favorites link is present
        expect(screen.getByText("Favorites")).toBeInTheDocument();

        const favoritesLink = screen.getByText("Favorites");
        fireEvent.click(favoritesLink);
    });

    test("No results found when term length is less than 3 for name search type", async () => {
        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "ab" } }); // Set term length < 3
        fireEvent.click(screen.getByAltText("Search Button"));

        // Assert that no results message is displayed
        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("No results found when term length is less than 3 for activity search type", async () => {
        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "ab" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("Activity"));

        fireEvent.click(screen.getByAltText("Search Button"));

        // Assert that no results message is displayed
        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("Results found when term length is greater than 3 for activity search type", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "Activity 1" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("Activity"));
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Results found when term length is greater than 3, but term is not an activity even though user selected activity search type", async () => {
        const mockData = {
            total: 0,
            parks: null,
        };
        const mockActivityData = {
            data: null,
        };
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(mockActivityData), {status: 400});
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock an unsuccessful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "fjafdslafdskfdja" } });
        fireEvent.click(screen.getByLabelText("Activity"));
        fireEvent.keyDown(searchInput, { key: "Enter" });
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();
        expect(screen.queryByText("Show More")).not.toBeInTheDocument();

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("If entered activity string doens't exist in amenity API when on activity search type", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };


        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock an unsuccessful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "Activity 5" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("Activity"));
        fireEvent.keyDown(searchInput, { key: "Enter" });
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();
        expect(screen.queryByText("Show More")).not.toBeInTheDocument();

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("No results found when term length is less than 3 for amenities search type", async () => {
        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "ab" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("Amenities"));

        fireEvent.click(screen.getByAltText("Search Button"));

        // Assert that no results message is displayed
        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("Results found when term length is greater than 3 for activity search type number 2", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "Activity 1" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("Activity"));
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Results found when term length is greater than 3, but term is not an activity even though user selected activity search type number 2", async () => {
        const mockData = {
            total: 0,
            parks: null,
        };
        const mockActivityData = {
            data: null,
        };
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(mockActivityData), {status: 400});
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock an unsuccessful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "fjafdslafdskfdja" } });
        fireEvent.click(screen.getByLabelText("Activity"));
        fireEvent.keyDown(searchInput, { key: "Enter" });
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();
        expect(screen.queryByText("Show More")).not.toBeInTheDocument();

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("If entered activity string doens't exist in amenity API when on activity search type number 2", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "Activity 1",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };


        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock an unsuccessful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "Activity 5" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("Activity"));
        fireEvent.keyDown(searchInput, { key: "Enter" });
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();
        expect(screen.queryByText("Show More")).not.toBeInTheDocument();

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("Results found when term is stateCode and user selected stateCode search type", async () => {
        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        //fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "State" } }); // Set term length < 3
        fireEvent.click(screen.getByLabelText("State"));
        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });


    test("Results found when term length is greater than 3 for amenities search type", async () => {
        const mockAmenityData = {
            total: "2",
            limit: "10",
            start: "0",
            data: [
                [
                    {
                        id: "2856392B-17A7-4413-8521-C13A8D2188EB",
                        name: "Amenity 1",
                        parks: [
                            {
                                states: "KY",
                                designation: "National Historical Park",
                                parkCode: "park1",
                                fullName: "Abraham Lincoln Birthplace National Historical Park",
                                places: [
                                    {
                                        title: "The First Lincoln Memorial at Abraham Lincoln Birthplace",
                                        id: "78ED430A-ECEF-435B-B8F6-25964AD607C2",
                                        url: "https://www.nps.gov/places/abraham-lincoln-birthplace-memorial-building.htm"
                                    }
                                ],
                                url: "http://www.nps.gov/abli/",
                                name: "Abraham Lincoln Birthplace"
                            }
                        ]
                    }
                ]
            ]
        }

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockAmenityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        fireEvent.click(screen.getByLabelText("Amenities"));
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "amenity 1" } }); // Set term length < 3

        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        // expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Results not found when term length is greater than 3 for amenities search type", async () => {
        const mockAmenityData = {
            total: "2",
            limit: "10",
            start: "0",
            data: [
                [
                    {
                        id: "2856392B-17A7-4413-8521-C13A8D2188EB",
                        name: "Amenity 1",
                        parks: [
                            {
                                states: "KY",
                                designation: "National Historical Park",
                                parkCode: "park1",
                                fullName: "Abraham Lincoln Birthplace National Historical Park",
                                places: [
                                    {
                                        title: "The First Lincoln Memorial at Abraham Lincoln Birthplace",
                                        id: "78ED430A-ECEF-435B-B8F6-25964AD607C2",
                                        url: "https://www.nps.gov/places/abraham-lincoln-birthplace-memorial-building.htm"
                                    }
                                ],
                                url: "http://www.nps.gov/abli/",
                                name: "Abraham Lincoln Birthplace"
                            }
                        ]
                    }
                ]
            ]
        }

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        fetch.mockResponseOnce(JSON.stringify(mockAmenityData), { status: 200 });

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        fireEvent.click(screen.getByLabelText("Amenities"));
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "amenity 1" } }); // Set term length < 3

        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("Amenities api returns error when term length is greater than 3 for amenities search type", async () => {
        const mockAmenityData = {
            total: "2",
            limit: "10",
            start: "0",
            data: [
                [
                    {
                        id: "2856392B-17A7-4413-8521-C13A8D2188EB",
                        name: "Amenity 1",
                        parks: [
                            {
                                states: "KY",
                                designation: "National Historical Park",
                                parkCode: "park1",
                                fullName: "Abraham Lincoln Birthplace National Historical Park",
                                places: [
                                    {
                                        title: "The First Lincoln Memorial at Abraham Lincoln Birthplace",
                                        id: "78ED430A-ECEF-435B-B8F6-25964AD607C2",
                                        url: "https://www.nps.gov/places/abraham-lincoln-birthplace-memorial-building.htm"
                                    }
                                ],
                                url: "http://www.nps.gov/abli/",
                                name: "Abraham Lincoln Birthplace"
                            }
                        ]
                    }
                ]
            ]
        }

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockAmenityData), { status: 400 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        fireEvent.click(screen.getByLabelText("Amenities"));
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "amenity 1" } }); // Set term length < 3

        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("Amenities api returns success but no data when term length is greater than 3 for amenities search type", async () => {
        const mockAmenityData = {
            total: "2",
            limit: "10",
            start: "0",
            data: [
            ]
        }


        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [], //Empty entrance fees array
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockAmenityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );
        fireEvent.click(screen.getByLabelText("Amenities"));
        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "amenity 1" } }); // Set term length < 3

        fireEvent.click(screen.getByAltText("Search Button"));

        expect(await screen.findByText("NO RESULTS FOUND!")).toBeInTheDocument();
    });

    test("Performing search by clicking on activity in activities list within park details", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "test",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
                <BrowserRouter>
                    <Search />
                </BrowserRouter>
            );

            const searchInput = screen.getByPlaceholderText("Search");
            fireEvent.change(searchInput, { target: { value: "test" } });

            expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
            expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

            act(() => {
                fireEvent.click(screen.getByAltText("Search Button"));
            });


            expect(await screen.findByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();

            expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});

            act(() => {
                fireEvent.click(screen.getByText("Park 1"));
            });
            expect(await screen.findByText("Description: Description")).toBeInTheDocument();
            expect(await screen.getByText("Activity 1")).toBeInTheDocument();
            expect(await screen.getByText("Amenity 1")).toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByText("Activity 1"));
        });

        expect(fetch).toHaveBeenCalledTimes(4);
    });

    test("Performing search by pressing enter on activity in activities list within park details", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "test",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "State" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByAltText("Search Button"));
        });


        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();

        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});

        act(() => {
            fireEvent.click(screen.getByText("Park 1"));
        });
        expect(await screen.findByText("Description: Description")).toBeInTheDocument();
        expect(await screen.getByText("Activity 1")).toBeInTheDocument();
        expect(await screen.getByText("Amenity 1")).toBeInTheDocument();

        const activityElement = screen.getByText((content, node) => {
            return (
                node.tagName.toLowerCase() === "span" &&
                node.textContent === "Activity 1" &&
                node.classList.contains("park-activity")
            );
        });
        fireEvent.keyDown(activityElement, { key: "Enter" , code: "Enter"});

        expect(fetch).toHaveBeenCalledTimes(4);
    });

    test("Performing search by clicking on StateCode in Location within park details", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "test",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "CA" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                        // Other park data
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByAltText("Search Button"));
        });


        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();

        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});

        act(() => {
            fireEvent.click(screen.getByText("Park 1"));
        });

        expect(await screen.findByText("CA")).toBeInTheDocument();
        expect(await screen.findByText("Description: Description")).toBeInTheDocument();

    act(() => {
        const stateCodeElement = screen.getByText((content, node) => {
            return (
                node.tagName.toLowerCase() === "span" &&
                node.textContent === "CA" &&
                node.classList.contains("park-state-code")
            );
        });
        fireEvent.click(stateCodeElement);
    });

        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});
    });

    test("Performing search by pressing enter on StateCode in Location within park details", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "test",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "CA" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        expect(screen.queryByText("Park 1")).not.toBeInTheDocument();
        expect(screen.queryByText("Park 2")).not.toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByAltText("Search Button"));
        });


        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();

        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});

        act(() => {
            fireEvent.click(screen.getByText("Park 1"));
        });

        expect(await screen.findByText("CA")).toBeInTheDocument();
        expect(await screen.findByText("Description: Description")).toBeInTheDocument();

        act(() => {
            const stateCodeElement = screen.getByText((content, node) => {
                return (
                    node.tagName.toLowerCase() === "span" &&
                    node.textContent === "CA" &&
                    node.classList.contains("park-state-code")
                );
            });
           fireEvent.keyDown(stateCodeElement, { key: "Enter" , code: "Enter"});
        });

        //expect(fetch).toHaveBeenCalledTimes(4);
        expect(fetch).toHaveBeenCalledWith("/api/getParks?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});
    });


    test("Performing search by clicking on Amenity in park details", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "test",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockAmenityData = {
            data: [
                [
                    {
                        name: "Amenity 1",
                        parks: [{ parkCode: "park1" }],
                    },
                    {
                        name: "Amenity 2",
                        parks: [{ parkCode: "park1" }, { parkCode: "park2" }],
                    },
                ],
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "CA" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites
        fetch.mockResponseOnce(JSON.stringify(mockAmenityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        act(() => {
            fireEvent.click(screen.getByAltText("Search Button"));
        });

        expect(await screen.findByText("Park 1")).toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByText("Park 1"));
        });

        expect(await screen.findByText("Amenity 1")).toBeInTheDocument();
        expect(await screen.findByText("Amenity 2")).toBeInTheDocument();

        const amenityElement = screen.getByText((content, node) => {
            return (
                node.tagName.toLowerCase() === "span" &&
                node.textContent === "Amenity 1" &&
                node.classList.contains("park-amenity")
            );
        });

        act(() => {
            fireEvent.click(amenityElement);
        });

        expect(fetch).toHaveBeenCalledWith("/api/getParkActivities?limit=10&start=0&q=test",
            {"headers": {"Authorization": "Bearer null"}},);
    });

    test("Performing search by pressing enter on Amenity in park details", async () => {
        const mockActivityData = {
            data: [
                {
                    name: "test",
                    parks: [
                        { parkCode: "park1" },
                        { parkCode: "park2" },
                    ],
                },
            ],
        };

        const mockAmenityData = {
            data: [
                [
                    {
                        name: "Amenity 1",
                        parks: [{ parkCode: "park1" }],
                    },
                    {
                        name: "Amenity 2",
                        parks: [{ parkCode: "park1" }, { parkCode: "park2" }],
                    },
                ],
            ],
        };

        const mockParkData = {
            total: 2,
            parks: [
                {
                    id: 1,
                    fullName: "Park 1",
                    parkCode: "park1",
                    images: [{ url: "image-url" }],
                    addresses: [{ city: "City", stateCode: "CA" }],
                    entranceFees: [{ cost: "10" }],
                    description: "Description",
                    activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                    directionsUrl: "directions-url",
                    operatingHours: [
                        { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                    ],
                    contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                    amenities: ["Amenity 1", "Amenity 2"],
                },
                {
                    id: 2,
                    fullName: "Park 2",
                    parkCode: "park2",
                    // Other park data
                },
            ],
            amenities: {
                park1: ["Amenity 1", "Amenity 2"],
                park2: ["Amenity 3", "Amenity 4"],
            },
        };

        fetch.mockResponseOnce(JSON.stringify(mockActivityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock a successful response for checking favorites
        fetch.mockResponseOnce(JSON.stringify(mockAmenityData), { status: 200 });
        fetch.mockResponseOnce(JSON.stringify(mockParkData), { status: 200 });

        render(
            <BrowserRouter>
                <Search />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Search");
        fireEvent.change(searchInput, { target: { value: "test" } });

        act(() => {
            fireEvent.click(screen.getByAltText("Search Button"));
        });

        expect(await screen.findByText("Park 1")).toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByText("Park 1"));
        });

        expect(await screen.findByText("Amenity 1")).toBeInTheDocument();
        expect(await screen.findByText("Amenity 2")).toBeInTheDocument();

        const amenityElement = screen.getByText((content, node) => {
            return (
                node.tagName.toLowerCase() === "span" &&
                node.textContent === "Amenity 1" &&
                node.classList.contains("park-amenity")
            );
        });

        act(() => {
            fireEvent.keyDown(amenityElement, { key: "Enter", code: "Enter" });
        });

        expect(fetch).toHaveBeenCalledWith("/api/getParkActivities?limit=10&start=0&q=test", {"headers": {"Authorization": "Bearer null"}});
    });

    test('handles search parameters from URL', async () => {
        const searchParams = new URLSearchParams('?searchType=state&searchTerm=CA');
        const route = `/search?${searchParams.toString()}`;

        render(
            <MemoryRouter initialEntries={[route]}>
                <Search />
            </MemoryRouter>
        );

        // Assert that the search type and term are updated correctly
        const stateRadioButton = screen.getByRole('radio', { name: 'State' });
        expect(stateRadioButton).toBeChecked();

        expect(screen.getByRole('textbox')).toHaveValue('CA');
    });
});

