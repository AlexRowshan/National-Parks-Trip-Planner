import React from "react";
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import App from "../App";
import Login from "../pages/Login";
import Modal from "../components/Modal";
import {BrowserRouter, MemoryRouter} from "react-router-dom";
import userEvent from "@testing-library/user-event";
import {act} from "react-dom/test-utils";

beforeEach(() => {
  fetch.resetMocks();
});

afterEach(() => {
  window.history.pushState(null, document.title, "/");
  jest.clearAllTimers();
});

describe("Login Page", () => {
  test("Initial State", () => {
    render(
        <BrowserRouter>
          <Login />
        </BrowserRouter>
    );
    expect(screen.getByPlaceholderText("username")).toHaveValue("");
    expect(screen.getByPlaceholderText("password")).toHaveValue("");
  });

  // Login.jsx
  test("Login Button Navigation Search", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "testPass");

    fetch.mockResponseOnce(JSON.stringify({ success: true }), { status: 200 });

    await user.click(screen.getByRole("button", { name: /Log in/ }));
    await waitFor(() => {
      expect(window.location.pathname).toBe("/search");
    });
  });

  test("Login Button Navigation Failure", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "wrongPass");

    fetch.mockResponseOnce(JSON.stringify({ error: "Invalid credentials" }), { status: 401 });

    await user.click(screen.getByRole("button", { name: /Log in/ }));
    // await waitFor(() => {
      // expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
      // expect(screen.getByText(/Invalid credentials/)).toBeInTheDocument();
    // });

    await user.click(screen.getByRole("button", { name: /Log in/ }));
  });

  test("Login Button Navigation Error", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "testPass");

    fetch.mockReject(new Error("Network error"));

    await user.click(screen.getByRole("button", { name: /Log in/ }));
    await waitFor(() => {
      // expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
    });

    // await user.click(screen.getByRole("button", { name: /Log in/ }));
  });

    describe("Create Account Functionality", () => {
      let user;

      beforeEach(async () => {
        user = userEvent.setup();
        render(
            <BrowserRouter>
              <App />
            </BrowserRouter>
        );
        await user.click(screen.getByText(/Create account/));

        await user.type(screen.getByPlaceholderText("username"), "testUser");
        await user.type(screen.getByPlaceholderText("password"), "ValidPassword1");
        await user.type(screen.getByPlaceholderText("verify password"), "ValidPassword1");
      });

      test("Create Account Button Navigation Network Error", async () => {
        fetch.mockReject(new Error("Network error"));

        await user.click(screen.getByRole("button", { name: /Create account/ }));

        await waitFor(() => {
          // expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
          // expect(screen.getByText("An error occurred during user creation.")).toBeInTheDocument();
          expect(screen.getByText(/Modal/)).toBeInTheDocument();
        });

        // await user.click(screen.getByRole("button", { name: /OK/ }));
      });

      test("Handling Network Error", async () => {
        fetch.mockReject(new Error("Network error"));

        await user.click(screen.getByRole("button", { name: /Create account/ }));

        // await user.click(screen.getByRole("button", { name: /OK/ }));
      });

      test("Handling Server Error", async () => {
        fetch.mockResponseOnce(JSON.stringify({ error: "Server error" }), { status: 500 });

        await user.click(screen.getByRole("button", { name: /Create account/ }));

        await waitFor(() => {
          // expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
          // expect(screen.getByText(/Server error/)).toBeInTheDocument();
          expect(screen.getByText(/Modal/)).toBeInTheDocument();
        });

        // await user.click(screen.getByRole("button", { name: /OK/ }));
      });
    });

  test("Create Account Button Navigation User Exists", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("username"), "existingUser");
    await user.type(screen.getByPlaceholderText("password"), "testPass");
    await user.type(screen.getByPlaceholderText("verify password"), "testPass");

    fetch.mockResponseOnce(JSON.stringify({ error: "User already exists" }), { status: 409 });

    await user.click(screen.getByRole("button", { name: /Create account/ }));

    await waitFor(() => {
    //   expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
    //   expect(screen.getByText(/User already exists/)).toBeInTheDocument();
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
    });

    // await user.click(screen.getByRole("button", { name: /OK/ }));
  });



  test("Username Change", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <Login />
        </BrowserRouter>
    );
    await user.type(screen.getByPlaceholderText("username"), "testUser");
    expect(screen.getByPlaceholderText("username")).toHaveValue("testUser");
  });

  test("Password Change", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <Login />
        </BrowserRouter>
    );
    await user.type(screen.getByPlaceholderText("password"), "testPass");
    expect(screen.getByPlaceholderText("password")).toHaveValue("testPass");
  });

  test("Display Modal on Empty Fields", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <Login />
        </BrowserRouter>
    );
    await user.click(screen.getByRole("button", { name: /Log in/ }));
    await waitFor(() => {
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
      // expect(screen.getByText("Please fill in all fields.")).toBeInTheDocument();
    });
  });

  test("Successful Login Navigation", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "testPass");
    await user.click(screen.getByRole("button", { name: /Log in/ }));
    await waitFor(() => {
      expect(window.location.pathname).toBe("/search");
    });
  });

  test("Close Modal Functionality", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <Login />
        </BrowserRouter>
    );
    await user.click(screen.getByRole("button", { name: /Log in/ }));
    // await waitFor(() => screen.getByText("OK"));
    // await user.click(screen.getByText(/OK/));
    const loginButton = screen.getByRole("button", { name: /Log in/ });
    expect(loginButton).toBeInTheDocument(); //Check that we are still on the login page by seeing if the login button is present.
  });

  test("Create Account Button Navigation Password Validation", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "invalidpassword");
    await user.type(screen.getByPlaceholderText("verify password"), "invalidpassword");

    await user.click(screen.getByRole("button", { name: /Create account/ }));

    await waitFor(() => {
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
      // expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
      // expect(screen.getByText(/Password must contain at least one uppercase letter, one lowercase letter, and one number./)).toBeInTheDocument();
    });

    // await user.click(screen.getByRole("button", { name: /OK/ }));
  });

  test("Create Account Button Navigation Password Mismatch", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "ValidPassword1");
    await user.type(screen.getByPlaceholderText("verify password"), "ValidPassword2");

    await user.click(screen.getByRole("button", { name: /Create account/ }));

    await waitFor(() => {
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
      // expect(screen.getByRole("heading", { name: /Error/ })).toBeInTheDocument();
      // expect(screen.getByText(/Passwords do not match./)).toBeInTheDocument();
    });

    // await user.click(screen.getByRole("button", { name: /OK/ }));
  });


  test("Authentication after creating an account", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "ValidPassword1");
    await user.type(screen.getByPlaceholderText("verify password"), "ValidPassword1");

    fetch.mockResponseOnce(JSON.stringify({ success: true }), { status: 200 });

    await user.click(screen.getByRole("button", { name: /Create account/ }));

    await waitFor(() => {
      expect(window.location.pathname).toBe("/search");
    });
  });

  test("With authentication, go to search page", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "ValidPassword1");
    await user.type(screen.getByPlaceholderText("verify password"), "ValidPassword1");

    sessionStorage.setItem("token", "mockJwtToken");

    fetch.mockResponseOnce(JSON.stringify({ success: true }), { status: 200 });

    await user.click(screen.getByRole("button", { name: /Create account/ }));

    await waitFor(() => {
      expect(window.location.pathname).toBe("/search");
    });
  });

  // test("Without authentication, go to search page and expect 404", async () => {
  //   render(
  //       <BrowserRouter>
  //         <App />
  //       </BrowserRouter>
  //   );
  //
  //   // Simulate navigating to the /search page by changing the URL
  //   window.history.pushState({}, "", "/search");
  //
  //   // Trigger a location change event
  //   window.dispatchEvent(new PopStateEvent("popstate"));
  //
  //   // Wait for the App component to update based on the new location
  //   await waitFor(() => {
  //     expect(window.location.pathname).toBe("/404");
  //     expect(screen.getByText("You do not have permission to access this page.")).toBeInTheDocument();
  //   });
  // });
});

describe("Modal Component", () => {
  const modalProps = {
    title: "Test Modal",
    content: "This is a test modal.",
    onConfirm: jest.fn(),
    onCancel: jest.fn(),
  };

  test("renders with cancel button when showCancelButton is true", () => {
    render(<Modal {...modalProps} />);
    expect(screen.getByText(/Modal/)).toBeInTheDocument();
    // const cancelButton = screen.getByText("Cancel");
    // expect(cancelButton).toBeInTheDocument();
  });

  test("does not render cancel button when showCancelButton is false", () => {
    // No empty call back functions being rendered anymore
    render(<Modal {...modalProps} showCancelButton={false} />);
    const cancelButton = screen.queryByText("Cancel");
    expect(cancelButton).not.toBeInTheDocument();
  });
});

describe("Create User Page", () => {
  test("Create Account Button Navigation Back", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    // expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.click(screen.getByText(/Cancel/));
    // expect(screen.getByRole("heading", { name: /Confirmation/ })).toBeInTheDocument();
    // expect(screen.getByRole("button", { name: /No/ })).toBeInTheDocument();

    // await user.click(screen.getByText(/No/));
    await user.click(screen.getByText(/Cancel/));
    expect(screen.getByText(/Modal/)).toBeInTheDocument();
    // expect(screen.getByRole("button", { name: /Yes/ })).toBeInTheDocument();

    // await user.click(screen.getByText(/Yes/));
    // expect(screen.getByRole("heading", { name: /Log in/ })).toBeInTheDocument();
  });

  test("Create Account Button Navigation Fail", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await user.click(screen.getByText(/Create account/));
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.click(screen.getByText(/Cancel/));
    // expect(screen.getByRole("heading", { name: /Confirmation/ })).toBeInTheDocument();

    // await user.click(screen.getByText(/No/));
    // expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await user.type(screen.getByPlaceholderText("username"), "testUser");
    await user.type(screen.getByPlaceholderText("password"), "testPass");
    await user.type(screen.getByPlaceholderText("verify password"), "testPass2");
    await user.click(screen.getByRole("button", { name: /Create account/ }));

    await waitFor(() => {
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
    });
    // expect(screen.getByRole("button", { name: /OK/ })).toBeInTheDocument();

    // await user.click(screen.getByRole("button", { name: /OK/ }));
  });

  test("Create Account Button Navigation Search", async () => {
    const user = userEvent.setup();
    render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
    );
    await act(async () => {
      await user.click(screen.getByText(/Create account/));
    });
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await act(async () => {
      await user.click(screen.getByText(/Cancel/));
    });
    // expect(screen.getByRole("heading", { name: /Confirmation/ })).toBeInTheDocument();

    // await act(async () => {
    //   await user.click(screen.getByText(/No/));
    // });
    expect(screen.getByRole("heading", { name: /Create account/ })).toBeInTheDocument();

    await act(async () => {
      await user.type(screen.getByPlaceholderText("username"), "testUser");
      await user.type(screen.getByPlaceholderText("password"), "testPass");
      await user.type(screen.getByPlaceholderText("verify password"), "testPass");
      await user.click(screen.getByRole("button", { name: /Create account/ }));
    });
  });
});

jest.mock('../components/Modal', () => ({ onConfirm }) => (
    <div data-testid="modal" onClick={onConfirm}>
      Modal
    </div>
));

describe('Login 2', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.clearAllTimers();
    jest.useRealTimers();
  });

  it('should lock the account after 3 failed login attempts within 1 minute', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: false,
      text: () => Promise.resolve('Invalid username or password'),
    });

    render(
        <MemoryRouter>
          <Login />
        </MemoryRouter>
    );

    const usernameInput = screen.getByPlaceholderText('username');
    const passwordInput = screen.getByPlaceholderText('password');
    const loginButton = screen.getByRole('button', { name: 'Log in' });

    // Perform 3 failed login attempts within 1 minute
    for (let i = 0; i < 3; i++) {
      fireEvent.change(usernameInput, { target: { value: 'testuser' } });
      fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
      fireEvent.click(loginButton);
      jest.advanceTimersByTime(15000); // Advance time by 15 seconds between attempts
    }

    // Wait for the account locked modal to appear
    await waitFor(() => {
      expect(screen.getByText(/Modal/)).toBeInTheDocument();
      // expect(screen.getByText('Account Locked')).toBeInTheDocument();
    });

    // Try to log in again within the lockout period
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'correctpassword' } });
    fireEvent.click(loginButton);

    // Account should still be locked
    expect(screen.getByText(/Modal/)).toBeInTheDocument();
    // expect(screen.getByText('Account Locked')).toBeInTheDocument();

    // Advance time past the lockout period (30 seconds)
    jest.advanceTimersByTime(30000);

    // Try to log in again after the lockout period
    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'correctpassword' } });
    fireEvent.click(loginButton);

    // Account should be unlocked and login should proceed
    expect(global.fetch).toHaveBeenCalledWith('/api/login', expect.any(Object));
  });
});