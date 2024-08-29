import React from "react";
import ReactDOM from "react-dom/client";
import "./styles/Compare.css"
import "./styles/Favorites.css"
import "./styles/index.css";
import "./styles/Search.css"
import "./styles/UpperArea.css"
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import { BrowserRouter } from "react-router-dom";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

reportWebVitals();