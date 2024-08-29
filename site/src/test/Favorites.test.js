import React from 'react';
import {render, screen, fireEvent, waitFor, within} from '@testing-library/react';
import Favorites from "../pages/Favorites";
import { BrowserRouter } from "react-router-dom";
import {act} from "react-dom/test-utils";

beforeEach(() => {
    fetch.resetMocks();  // Reset fetch mocks before each test
    jest.clearAllMocks(); // Clear all jest mocks to ensure no residual data
    sessionStorage.clear(); // Clear all sessionStorage data
    window.history.pushState(null, document.title, "/"); // Reset the URL state
});

describe("Favorites Page", () => {

    test("Fetching Park Data - Success", async () => {
        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });
    });

    test("Fetching Park Data - Fail (No favorites found.)", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        // Mock the fetch response with a non-successful status code (e.g., 500)
        fetch.mockResponseOnce("No favorites found.", { status: 404 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render and fetch data
        await waitFor(() => {
            expect(screen.getByText("No favorites found.")).toBeInTheDocument();
        });
    });

    test("Fetching Park Data - Fail (Error fetching park data)", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        // Mock the fetch response with a non-successful status code (e.g., 500)
        fetch.mockResponseOnce("", { status: 404 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );
        expect(fetch).toHaveBeenCalledTimes(2); // Ensure that fetch was called twice
    });

    test("Toggle park card details by clicking the same park name twice", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });
        // Simulate the second click on the same park card to collapse the details
        fireEvent.click(parkName);

        // Use waitFor to give the UI time to update
        await waitFor(() => {
            // Check that the description is not in the document anymore, indicating the details are collapsed...
            expect(screen.queryByText("Description: Description")).not.toBeInTheDocument();
        });
    });

    test("Clicking Hover Button", async () => {
        const mockData = [
            {
                total: 1,
                parks: [
                    {
                        id: 1,
                        fullName: "Park 1",
                        parkCode: "park1",
                        // Other park data...
                    },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        const username = "testuser";
        sessionStorage.setItem("username", username);

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        await screen.findByText("Park 1");

        // Simulate hovering over the park card and clicking the "-" button
        const parkCard = screen.getByText("Park 1").closest(".park-card");
        fireEvent.mouseEnter(parkCard);

        // Assert that the "-" button is visible
        expect(screen.getByRole("button", { name: "-" })).toBeInTheDocument();

        // Simulate the mouse leaving the park card
        fireEvent.mouseLeave(parkCard);

        // Assert that the "-" button is no longer visible
        expect(screen.queryByRole("button", { name: "-" })).not.toBeInTheDocument();

        // Mock a successful response for removing from favorites
        window.confirm = jest.fn(() => true);
        fetch.mockResponseOnce(JSON.stringify("Park removed from favorites"));
        fetch.mockResponseOnce(JSON.stringify([])); // Mock empty favorites response

        // Simulate hovering over the park card again and clicking the "-" button
        fireEvent.mouseEnter(parkCard);
        fireEvent.click(screen.getByRole("button", { name: "-" }));

        // Assert that the confirmation dialog was called
        expect(window.confirm).toHaveBeenCalledTimes(1);

        // Mock an error response for the second attempt to remove from favorites
        window.confirm = jest.fn(() => true);
        fetch.mockResponseOnce(JSON.stringify("Failed to remove park from favorites"), { status: 500 });

        fireEvent.mouseEnter(parkCard);
        fireEvent.click(screen.getByRole("button", { name: "-" }));


    });

    test("Remove Park from Favorites - Error", async () => {
        const mockData = [
            {
                total: 1,
                parks: [
                    {
                        id: 1,
                        fullName: "Park 1",
                        parkCode: "park1",
                    },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        const username = "testuser";
        sessionStorage.setItem("username", username);

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        await screen.findByText("Park 1");

        // Simulate hovering over the park card and clicking the "-" button
        const parkCard = screen.getByText("Park 1").closest(".park-card");
        fireEvent.mouseEnter(parkCard);

        // Mock an error response when attempting to remove the park from favorites
        // Mock an error response when attempting to remove the park from favoritesd
        window.confirm = jest.fn(() => true);
        fetch.mockResponseOnce(JSON.stringify("Failed to remove park from favorites"), { status: 500 });

        // Mock the window.alert function
        window.alert = jest.fn();

        // Click the "-" button to trigger the removal
        fireEvent.click(screen.getByRole("button", { name: "-" }));

        // Assert that the confirmation dialog was called
        expect(window.confirm).toHaveBeenCalledTimes(1);


    });

    test("Update Favorite Rank - Success (pressing up arrow)", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });
        // Simulate the rank change
         const parkCard = screen.getByText("Park 1").closest(".park-card");
         const upArrow = within(parkCard).getByRole("button", { name: "▲" });
         fireEvent.click(upArrow);
      //  fetch.mockResponseOnce(ok);
        fetch.mockResponseOnce(JSON.stringify(mockData));

        //Wait for the fetch calls and component update
         await waitFor(() => expect(fetch).toHaveBeenCalledTimes(3));
        expect(screen.getByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Update Favorite Rank - Success (pressing sown arrow)", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });
        // Simulate the rank change
        const parkCard = screen.getByText("Park 1").closest(".park-card");
        const downArrow = within(parkCard).getByRole("button", { name: "▼" });
        fireEvent.click(downArrow);
        //  fetch.mockResponseOnce(ok);
        fetch.mockResponseOnce(JSON.stringify(mockData));

        //Wait for the fetch calls and component update
        await waitFor(() => expect(fetch).toHaveBeenCalledTimes(3));
        expect(screen.getByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
    });

    test("Update Favorite Rank - Fetch Park Data Fail", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });
        // Simulate the rank change
        const parkCard = screen.getByText("Park 1").closest(".park-card");
        const downArrow = within(parkCard).getByRole("button", { name: "▼" });
        fireEvent.click(downArrow);
        //  fetch.mockResponseOnce(ok);
        fetch.mockResponseOnce("No favorites found.", { status: 404 });

        //Wait for the fetch calls and component update
        await waitFor(() => expect(fetch).toHaveBeenCalledTimes(3));
    });


    test("Toggle Private Status - Success", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);
        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });

        await waitFor(() => screen.getByRole("checkbox"));

        // Simulate toggling the private status
        const checkbox = screen.getByRole("checkbox");
        fireEvent.click(checkbox);
        fetch.mockResponseOnce(JSON.stringify(true));

        expect(screen.getByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
        expect(fetch).toHaveBeenCalledTimes(3);
    });


    test("Fetching Park Data - Fail (Error fetching park data)", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        // Mock the fetch response with a non-successful status code (e.g., 500)
        fetch.mockResponseOnce("", { status: 500 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the error alert to be called
        await waitFor(() => {
            expect(window.alert).toHaveBeenCalledWith("Failed to fetch favorites data.");
        });

        expect(fetch).toHaveBeenCalledTimes(2); // Ensure that fetch was called twice
    });

    test("Fetching Park Data - Fail (Throw Error)", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        // Mock the fetch response with a 404 status code and a custom error message
        fetch.mockResponseOnce("Custom error message", { status: 404 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the error alert to be called with the custom error message
        await waitFor(() => {
            expect(window.alert).toHaveBeenCalledWith("Custom error message");
        });

        expect(fetch).toHaveBeenCalledTimes(2); // Ensure that fetch was called twice
    });

    test("Update Favorite Rank - Fail", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1", parkCode: "park1" },
                    { id: 2, fullName: "Park 2", parkCode: "park2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });

        // Mock the window.alert function
        window.alert = jest.fn();

        // Simulate the rank change with an error response
        const parkCard = screen.getByText("Park 1").closest(".park-card");
        const upArrow = within(parkCard).getByRole("button", { name: "▲" });
        fireEvent.click(upArrow);
        fetch.mockReject(new Error("Failed to update favorite rank.")); // Simulate an error thrown by the server

        // Wait for the fetch calls and component update
        await waitFor(() => expect(fetch).toHaveBeenCalledTimes(3));

        // Assert that the error message is displayed using window.alert
        expect(window.alert).toHaveBeenCalledWith("Failed to update favorite rank.");
    });

    test("Clicking Park Name - Entrance Fee Not Available", async () => {
        const mockData = [
            {
                total: 1,
                parks: [
                    {
                        id: 1,
                        fullName: "Park 1",
                        parkCode: "parkCode1",
                        images: [{ url: "image-url" }],
                        addresses: [{ city: "City", stateCode: "State" }],
                        entranceFees: [], // Empty entranceFees array
                        description: "Description",
                        activities: [{ name: "Activity 1" }, { name: "Activity 2" }],
                        directionsUrl: "directions-url",
                        operatingHours: [
                            { name: "Hours", standardHours: { monday: "9:00AM - 5:00PM" } },
                        ],
                        contacts: { phoneNumbers: [{ phoneNumber: "123-456-7890" }] },
                        isFavorite: false,
                    }
                ],
                amenities: {
                    "parkCode1": ["Amenity 1", "Amenity 2"],
                },
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        expect(await screen.findByText("Park 1")).toBeInTheDocument();
        fireEvent.click(screen.getByText("Park 1"));

        expect(screen.getByText("Entrance Fee: Not available")).toBeInTheDocument(); // Assert "Not available" text
        expect(screen.getByText("Description: Description")).toBeInTheDocument();

        expect(screen.getByText("Contact: 123-456-7890")).toBeInTheDocument();

        // Clicking the park name again should hide the details
        fireEvent.click(screen.getByText("Park 1"));
        expect(screen.queryByText("Location: City, State")).not.toBeInTheDocument();
    });

    test("Delete All Favorites - Success", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1", parkCode: "park1" },
                    { id: 2, fullName: "Park 2", parkCode: "park2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        // Mock the successful response for deleting all favorites
        fetch.mockResponseOnce(
            JSON.stringify({ ok: true }),
            { ok: true, text: () => Promise.resolve("All favorites deleted, and privacy status was set back to private.") }
        );

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });

        // Simulate user confirming the action
        window.confirm = jest.fn(() => true);

        // Click the 'Delete All Favorites' button
        fireEvent.click(screen.getByText("Delete All Favorites"));

        // Assert that the confirmation dialog was called
        expect(window.confirm).toHaveBeenCalledTimes(1);

        // Wait for the fetch call to be made and the response to be received
        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith("/api/deleteAllFavorites", expect.anything());
        });

        fetch.mockResponseOnce("No favorites found.", { status: 404 });
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        // Wait for the component to render and fetch data
        await waitFor(() => {
            expect(screen.getByText("No favorites found.")).toBeInTheDocument();
        });
    });

    test("Delete All Favorites - Fail", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);

        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1", parkCode: "park1" },
                    { id: 2, fullName: "Park 2", parkCode: "park2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(false)); // Mock response for visibility

        // Mock a failed response for deleting all favorites
        fetch.mockResponseOnce(
            JSON.stringify({ ok: false }),
            { ok: false, status: 500, statusText: "Internal Server Error" }
        );

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });

        // Simulate user confirming the action
        window.confirm = jest.fn(() => true);

        // Mock the console.log function
        const originalConsoleLog = console.log;
        console.log = jest.fn();

        // Click the 'Delete All Favorites' button
        fireEvent.click(screen.getByText("Delete All Favorites"));

        // Assert that the confirmation dialog was called
        expect(window.confirm).toHaveBeenCalledTimes(1);

        // Wait for the fetch call to be made and the response to be received
        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith("/api/deleteAllFavorites", expect.anything());
        });

        // Assert that the error log is called
        expect(console.log).toHaveBeenCalledWith("Failed to delete all favorites and set list to private.");

        // Restore the original console.log function
        console.log = originalConsoleLog;
    });

    test("Toggle Private Status via Enter Press - Success", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);
        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });

        await waitFor(() => screen.getByRole("checkbox"));

        // Simulate toggling the private status
        const checkbox = screen.getByRole("checkbox");
        act(() => {
            fireEvent.keyDown(checkbox, { key: "Enter", code: "Enter" });
        });
        fetch.mockResponseOnce(JSON.stringify(true));

        expect(screen.getByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
        expect(fetch).toHaveBeenCalledTimes(3);
    });

    test("Clicking on stateCode in park details to navigate to search", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });

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
    });

    test("Pressing Enter on stateCode in park details to navigate to search", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });

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
    });

    test("Clicking on activity in park details to navigate to search", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });

        act(() => {
            fireEvent.click(screen.getByText("Activity 1"));
        });
    });

    test("Pressing Enter on activity in park details to navigate to search", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });


        act(() => {
            fireEvent.keyDown(screen.getByText("Activity 1"), { key: "Enter" , code: "Enter"});
        });
    });

    test("Clicking on amenity in park details to navigate to search", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });

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
    });

    test("Pressing enter on amenity in park details to navigate to search", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");

        fireEvent.click(parkName);
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });

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
    });

    test("Toggle park card details by pressing enter on the same park name twice", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");
        fireEvent.focus(parkName);

        act(() => {
            fireEvent.keyDown(parkName, { key: "Enter", code: "Enter" });
        });
        await waitFor(() => {
            // Check that the description is in the document, indicating the details are expanded...
            expect(screen.queryByText("Description: Description")).toBeInTheDocument();
        });
        // Simulate the second click on the same park card to collapse the details
        act(() => {
            fireEvent.keyDown(parkName, { key: "Enter", code: "Enter" });
        });

        // Use waitFor to give the UI time to update
        await waitFor(() => {
            // Check that the description is not in the document anymore, indicating the details are collapsed...
            expect(screen.queryByText("Description: Description")).not.toBeInTheDocument();
        });
    });

    test("Toggle Private Status via Bad Key Press - Fail", async () => {
        const username = "testuser";
        sessionStorage.setItem("username", username);
        const mockData = [
            {
                total: 2,
                parks: [
                    { id: 1, fullName: "Park 1" },
                    { id: 2, fullName: "Park 2" },
                ],
            },
        ];
        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify(true)); // Mock response for visibility

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the component to render with the mocked data
        await waitFor(() => {
            expect(screen.getByText("Park 1")).toBeInTheDocument();
            expect(screen.getByText("Park 2")).toBeInTheDocument();
        });

        await waitFor(() => screen.getByRole("checkbox"));

        // Simulate toggling the private status
        const checkbox = screen.getByRole("checkbox");
        act(() => {
            fireEvent.keyDown(checkbox, { key: ".", code: "Enter" });
        });

        expect(screen.getByText("Park 1")).toBeInTheDocument();
        expect(screen.getByText("Park 2")).toBeInTheDocument();
        expect(fetch).toHaveBeenCalledTimes(2);
    });

    test("User tries to open park details with keyboard but presses a key that isn't Enter", async () => {
        const mockData = [
            {
                total: 1,
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
                    },
                ],
                amenities: {
                    park1: ["Amenity 1", "Amenity 2"],
                    park2: ["Amenity 3", "Amenity 4"],
                },
            },
        ];

        fetch.mockResponseOnce(JSON.stringify(mockData));
        fetch.mockResponseOnce(JSON.stringify({ visibility: false })); // Mock response for visibility as a valid JSON object

        render(
            <BrowserRouter>
                <Favorites />
            </BrowserRouter>
        );

        // Wait for the park card to be in the document
        const parkName = await screen.findByText("Park 1");
        fireEvent.focus(parkName);

        act(() => {
            fireEvent.keyDown(parkName, { key: ".", code: "Enter" });
        });

        expect(screen.queryByText("Description: Description")).not.toBeInTheDocument();
    });

});