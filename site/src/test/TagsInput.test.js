import {fireEvent, render, waitFor} from "@testing-library/react";
import React from "react";
import TagsInput from "../components/TagsInput";

describe('TagInputs', () => {
    beforeEach(() => {
        jest.spyOn(global, 'fetch').mockResolvedValue({
            status: 200,
            ok: true,
            text: jest.fn().mockResolvedValue('false'),
        });
    });

    afterEach(() => {
        jest.restoreAllMocks();
    });

    it('should call handleCompareSearchTags when Enter key is pressed with valid input', async () => {
        const onEnterMock = jest.fn();
        const { getByPlaceholderText } = render(<TagsInput onEnter={onEnterMock} />);
        const input = getByPlaceholderText("Enter your friend's username");

        fireEvent.change(input, { target: { value: 'testuser' } });
        fireEvent.keyDown(input, { key: 'Enter', code: 'Enter' });

        await waitFor(() => expect(onEnterMock).toHaveBeenCalledWith(['testuser']));
    });

    it('should call handleCompareSearchTags with updated tags when adding a new tag', async () => {
        const onEnterMock = jest.fn();
        const { getByPlaceholderText, getByText } = render(<TagsInput onEnter={onEnterMock} />);
        const input = getByPlaceholderText("Enter your friend's username");
        const addUserButton = getByText('Add User');

        fireEvent.change(input, { target: { value: 'testuser' } });
        fireEvent.click(addUserButton);

        await waitFor(() => expect(onEnterMock).toHaveBeenCalledWith(['testuser']));
    });

    it('should not call handleCompareSearchTags when Enter key is pressed with empty input', async () => {
        const onEnterMock = jest.fn();
        const { getByPlaceholderText } = render(<TagsInput onEnter={onEnterMock} />);
        const input = getByPlaceholderText("Enter your friend's username");

        fireEvent.keyDown(input, { key: 'Enter', code: 'Enter' });

        await waitFor(() => expect(onEnterMock).not.toHaveBeenCalled());
    });

    it('should handle "not_found" when user is not found', async () => {
        // Mock fetch to return a 404 response
        global.fetch = jest.fn().mockResolvedValueOnce({
            status: 404,
        });

        // Mock onEnter function
        const onEnterMock = jest.fn();

        // Render TagsInput component with mock function
        const { getByPlaceholderText } = render(<TagsInput onEnter={onEnterMock} />);
        const input = getByPlaceholderText("Enter your friend's username");

        // Simulate user input and Enter key press
        fireEvent.change(input, { target: { value: 'nonExistingUser' } });
        fireEvent.keyDown(input, { key: 'Enter', code: 'Enter' });

        // Wait for async code inside TagsInput to resolve
        await waitFor(() => {
            // Check if onEnterMock was not called
            expect(onEnterMock).not.toHaveBeenCalled();
            // You might add more specific checks related to your component's behavior
        });
    });
});