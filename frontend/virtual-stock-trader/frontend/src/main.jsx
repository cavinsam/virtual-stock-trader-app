import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';
import './index.css';

/**
 * This is the root of your React application.
 * 1. It finds the HTML element with the id 'root' in your index.html file.
 * 2. It renders your main <App /> component inside that element.
 * (Since your App.jsx is a single-file application, it contains its own
 * BrowserRouter and AuthProvider, so they are not needed here).
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

