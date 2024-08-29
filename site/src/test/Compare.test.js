import React from "react";
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from "react-router-dom";
import Compare from "../pages/Compare";
import {act} from "react-dom/test-utils";

beforeEach(() => {
    require('jest-fetch-mock').enableMocks()
    fetch.resetMocks();
});

afterEach(() => {
    window.history.pushState(null, document.title, "/");
});

describe("Compare Page", () => {

    test("Initial State", () => {
        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );
        expect(screen.getByText("Compare favorite parks with others!")).toBeInTheDocument();

    });


    test("handleCompareSearchChange updates search term", () => {
        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Enter your friend's username");
        fireEvent.change(searchInput, { target: { value: "user1, user2" } });
        expect(searchInput.value).toBe("user1, user2");
    });

    test("fetchCompareParks updates compareParks state with data from API", async () => {
        const mockData = {
            total: 2,
            parkData: [
                { data: JSON.stringify({ parks: [{ id: "a", fullName: "Park A", images: [{ url: "image1.jpg" }], addresses: [{ city: "City A", stateCode: "AA" }], entranceFees: [{ cost: "10" }], description: "Description A", activities: [{ name: "Activity 1" }, { name: "Activity 2" }], directionsUrl: "directionUrlA", operatingHours: [{ name: "Hours A", standardHours: { monday: "9:00AM - 5:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "123456789" }] }, amenities: ["Amenity 1", "Amenity 2"] }] }), frequency: 2 },
                { data: JSON.stringify({ parks: [{ id: "b", fullName: "Park B", images: [{ url: "image2.jpg" }], addresses: [{ city: "City B", stateCode: "BB" }], entranceFees: [], description: "Description B", activities: [{ name: "Activity 3" }, { name: "Activity 4" }], directionsUrl: "directionUrlB", operatingHours: [{ name: "Hours B", standardHours: { tuesday: "10:00AM - 6:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "987654321" }] }, amenities: ["Amenity 3", "Amenity 4"] }] }), frequency: 1 }
            ]
        };
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockData)
            })
        );

        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const compareButton = screen.getByText("Compare!");
        fireEvent.click(compareButton);
        await waitFor(() => expect(screen.getByText("2/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park A")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("1/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park B")).toBeInTheDocument());

        fireEvent.click(screen.getByText("Park A"));
        expect(screen.getByAltText("Park A")).toBeInTheDocument();
        expect(screen.getByText("Entrance Fee: 10")).toBeInTheDocument();
        expect(screen.getByText("Description: Description A")).toBeInTheDocument();
        expect(screen.getByText("Activity 1")).toBeInTheDocument();
        expect(screen.getByText("Activity 2")).toBeInTheDocument();
        expect(screen.getByText("Official Website")).toHaveAttribute("href", "directionUrlA");
        expect(screen.getByText("Hours A: 9:00AM - 5:00PM")).toBeInTheDocument();
        expect(screen.getByText("Contact: 123456789")).toBeInTheDocument();
        expect(screen.getByText("Amenity 1")).toBeInTheDocument();
        expect(screen.getByText("Amenity 2")).toBeInTheDocument();
    });

    test("handle empty search in compare", async () => {
        const mockData = {
            total: 0,
            parkData: []
        };
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockData)
            })
        );

        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const compareButton = screen.getByText("Compare!");
        fireEvent.click(compareButton);
        expect(screen.queryByText(/^\d+\/\d+$/)).not.toBeInTheDocument();
    });

    // test("searching using Enter key triggers fetchCompareParks", async () => {
    //     const mockData = {
    //         total: 2,
    //         parkData: [
    //             { data: JSON.stringify({ parks: [{ id: "a", fullName: "Park A", images: [{ url: "image1.jpg" }] }] }), frequency: 2 },
    //             { data: JSON.stringify({ parks: [{ id: "b", fullName: "Park B", images: [{ url: "image2.jpg" }] }] }), frequency: 1 }
    //         ]
    //     };
    //     global.fetch = jest.fn(() =>
    //         Promise.resolve({
    //             json: () => Promise.resolve(mockData)
    //         })
    //     );
    //
    //     render(
    //         <BrowserRouter>
    //             <Compare />
    //         </BrowserRouter>
    //     );
    //
    //     const searchInput = screen.getByPlaceholderText("Enter your friend's username");
    //     await userEvent.type(searchInput, "user1{enter}");
    //     await waitFor(() => expect(screen.getByText("Park A")).toBeInTheDocument());
    //     await waitFor(() => expect(screen.getByText("1/2")).toBeInTheDocument());
    //     await waitFor(() => expect(screen.getByText("Park B")).toBeInTheDocument());
    // });

    test("handleCompareSearchChange does not update usernamesCompare when input is empty", () => {
        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const searchInput = screen.getByPlaceholderText("Enter your friend's username");
        fireEvent.change(searchInput, { target: { value: "" } });
        expect(searchInput.value).toBe("");
    });

    test("fetchCompareParks handles null park data correctly", async () => {
        const mockData = {
            total: 2,
            parkData: [
                { data: JSON.stringify({ parks: [{ id: "a", fullName: "Park A", images: [{ url: "image1.jpg" }] }] }), frequency: 2 },
                { data: JSON.stringify({ parks: [{ id: "b", fullName: null, images: null }] }), frequency: 1 }
            ]
        };
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockData)
            })
        );

        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const compareButton = screen.getByText("Compare!");
        fireEvent.click(compareButton);
        await waitFor(() => expect(screen.getByText("2/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park A")).toBeInTheDocument());
    });

    test("Clicking on activity in park details to navigate to search", async () => {
        const mockData = {
            total: 2,
            parkData: [
                { data: JSON.stringify({ parks: [{ id: "a", fullName: "Park A", images: [{ url: "image1.jpg" }], addresses: [{ city: "City A", stateCode: "AA" }], entranceFees: [{ cost: "10" }], description: "Description A", activities: [{ name: "Activity 1" }, { name: "Activity 2" }], directionsUrl: "directionUrlA", operatingHours: [{ name: "Hours A", standardHours: { monday: "9:00AM - 5:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "123456789" }] }, amenities: ["Amenity 1", "Amenity 2"] }] }), frequency: 2 },
                { data: JSON.stringify({ parks: [{ id: "b", fullName: "Park B", images: [{ url: "image2.jpg" }], addresses: [{ city: "City B", stateCode: "BB" }], entranceFees: [], description: "Description B", activities: [{ name: "Activity 3" }, { name: "Activity 4" }], directionsUrl: "directionUrlB", operatingHours: [{ name: "Hours B", standardHours: { tuesday: "10:00AM - 6:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "987654321" }] }, amenities: ["Amenity 3", "Amenity 4"] }] }), frequency: 1 }
            ]
        };
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockData)
            })
        );

        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const compareButton = screen.getByText("Compare!");
        fireEvent.click(compareButton);
        await waitFor(() => expect(screen.getByText("2/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park A")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("1/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park B")).toBeInTheDocument());

        fireEvent.click(screen.getByText("Park A"));
        expect(screen.getByAltText("Park A")).toBeInTheDocument();
        expect(screen.getByText("Entrance Fee: 10")).toBeInTheDocument();
        expect(screen.getByText("Description: Description A")).toBeInTheDocument();
        expect(screen.getByText("Activity 1")).toBeInTheDocument();
        expect(screen.getByText("Activity 2")).toBeInTheDocument();
        expect(screen.getByText("Official Website")).toHaveAttribute("href", "directionUrlA");
        expect(screen.getByText("Hours A: 9:00AM - 5:00PM")).toBeInTheDocument();
        expect(screen.getByText("Contact: 123456789")).toBeInTheDocument();
        expect(screen.getByText("Amenity 1")).toBeInTheDocument();
        expect(screen.getByText("Amenity 2")).toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByText("Activity 1"));
        });
    });

    test("Clicking on amenity in park details to navigate to search", async () => {
        const mockData = {
            total: 2,
            parkData: [
                { data: JSON.stringify({ parks: [{ id: "a", fullName: "Park A", images: [{ url: "image1.jpg" }], addresses: [{ city: "City A", stateCode: "AA" }], entranceFees: [{ cost: "10" }], description: "Description A", activities: [{ name: "Activity 1" }, { name: "Activity 2" }], directionsUrl: "directionUrlA", operatingHours: [{ name: "Hours A", standardHours: { monday: "9:00AM - 5:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "123456789" }] }, amenities: ["Amenity 1", "Amenity 2"] }] }), frequency: 2 },
                { data: JSON.stringify({ parks: [{ id: "b", fullName: "Park B", images: [{ url: "image2.jpg" }], addresses: [{ city: "City B", stateCode: "BB" }], entranceFees: [], description: "Description B", activities: [{ name: "Activity 3" }, { name: "Activity 4" }], directionsUrl: "directionUrlB", operatingHours: [{ name: "Hours B", standardHours: { tuesday: "10:00AM - 6:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "987654321" }] }, amenities: ["Amenity 3", "Amenity 4"] }] }), frequency: 1 }
            ]
        };
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockData)
            })
        );

        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const compareButton = screen.getByText("Compare!");
        fireEvent.click(compareButton);
        await waitFor(() => expect(screen.getByText("2/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park A")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("1/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park B")).toBeInTheDocument());

        fireEvent.click(screen.getByText("Park A"));
        expect(screen.getByAltText("Park A")).toBeInTheDocument();
        expect(screen.getByText("Entrance Fee: 10")).toBeInTheDocument();
        expect(screen.getByText("Description: Description A")).toBeInTheDocument();
        expect(screen.getByText("Activity 1")).toBeInTheDocument();
        expect(screen.getByText("Activity 2")).toBeInTheDocument();
        expect(screen.getByText("Official Website")).toHaveAttribute("href", "directionUrlA");
        expect(screen.getByText("Hours A: 9:00AM - 5:00PM")).toBeInTheDocument();
        expect(screen.getByText("Contact: 123456789")).toBeInTheDocument();
        expect(screen.getByText("Amenity 1")).toBeInTheDocument();
        expect(screen.getByText("Amenity 2")).toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByText("Amenity 1"));
        });
    });

    test("Clicking on stateCode in park details to navigate to search", async () => {
        const mockData = {
            total: 2,
            parkData: [
                { data: JSON.stringify({ parks: [{ id: "a", fullName: "Park A", images: [{ url: "image1.jpg" }], addresses: [{ city: "City A", stateCode: "AA" }], entranceFees: [{ cost: "10" }], description: "Description A", activities: [{ name: "Activity 1" }, { name: "Activity 2" }], directionsUrl: "directionUrlA", operatingHours: [{ name: "Hours A", standardHours: { monday: "9:00AM - 5:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "123456789" }] }, amenities: ["Amenity 1", "Amenity 2"] }] }), frequency: 2 },
                { data: JSON.stringify({ parks: [{ id: "b", fullName: "Park B", images: [{ url: "image2.jpg" }], addresses: [{ city: "City B", stateCode: "BB" }], entranceFees: [], description: "Description B", activities: [{ name: "Activity 3" }, { name: "Activity 4" }], directionsUrl: "directionUrlB", operatingHours: [{ name: "Hours B", standardHours: { tuesday: "10:00AM - 6:00PM" } }], contacts: { phoneNumbers: [{ phoneNumber: "987654321" }] }, amenities: ["Amenity 3", "Amenity 4"] }] }), frequency: 1 }
            ]
        };
        global.fetch = jest.fn(() =>
            Promise.resolve({
                json: () => Promise.resolve(mockData)
            })
        );

        render(
            <BrowserRouter>
                <Compare />
            </BrowserRouter>
        );

        const compareButton = screen.getByText("Compare!");
        fireEvent.click(compareButton);
        await waitFor(() => expect(screen.getByText("2/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park A")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("1/2")).toBeInTheDocument());
        await waitFor(() => expect(screen.getByText("Park B")).toBeInTheDocument());

        fireEvent.click(screen.getByText("Park A")); //Open and close to get more coverage
        fireEvent.click(screen.getByText("Park A"));
        fireEvent.click(screen.getByText("Park A"));
        expect(screen.getByAltText("Park A")).toBeInTheDocument();
        expect(screen.getByText("Entrance Fee: 10")).toBeInTheDocument();
        expect(screen.getByText("Description: Description A")).toBeInTheDocument();
        expect(screen.getByText("Activity 1")).toBeInTheDocument();
        expect(screen.getByText("Activity 2")).toBeInTheDocument();
        expect(screen.getByText("Official Website")).toHaveAttribute("href", "directionUrlA");
        expect(screen.getByText("Hours A: 9:00AM - 5:00PM")).toBeInTheDocument();
        expect(screen.getByText("Contact: 123456789")).toBeInTheDocument();
        expect(screen.getByText("Amenity 1")).toBeInTheDocument();
        expect(screen.getByText("Amenity 2")).toBeInTheDocument();

        act(() => {
            fireEvent.click(screen.getByText("AA"));
        });
    });
});