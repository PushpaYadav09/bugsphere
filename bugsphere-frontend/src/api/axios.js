// Import axios library (used to make HTTP requests like GET, POST)
import axios from 'axios';

// Create a custom axios instance
// This saves us from writing full URL every time
const api = axios.create({
  // Base URL for all backend API calls
  // So api.get('/bugs') → http://localhost:8080/api/bugs
  baseURL: 'http://localhost:8080/api',
});


// ─────────────────────────────────────────────────────────────
// 🔐 REQUEST INTERCEPTOR
// This runs BEFORE every request is sent to backend
// ─────────────────────────────────────────────────────────────
api.interceptors.request.use((config) => {

  // Get JWT token from browser localStorage
  // (we saved it after login)
  const token = localStorage.getItem('token');

  // Debug log to check if token is present
  console.log("🔐 TOKEN BEING USED:", token);

  // If token exists → attach it to request headers
  if (token) {
    config.headers = {
      // Keep existing headers (important)
      ...config.headers,

      // Add Authorization header
      // Backend expects: Bearer <token>
      Authorization: `Bearer ${token}`,
    };
  }

  // Return modified config so request continues
  return config;
});


// ─────────────────────────────────────────────────────────────
// 🚨 RESPONSE INTERCEPTOR
// This runs AFTER response comes from backend
// ─────────────────────────────────────────────────────────────
api.interceptors.response.use(

  // If request is successful → just return response
  (response) => response,

  // If error occurs → handle it here
  (error) => {

    // Check if error status is 401 (Unauthorized)
    // This happens when token is invalid or expired
    if (error.response?.status === 401) {

      // Remove token from localStorage (logout user)
      localStorage.removeItem('token');

      // Remove user info (if stored)
      localStorage.removeItem('user');

      // Redirect user to login page
      window.location.href = '/login';
    }

    // Return error so components can still handle it
    return Promise.reject(error);
  }
);


// Export this api instance so we can use it in other files
export default api;