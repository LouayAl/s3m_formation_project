// src/context/AuthContext.jsx
import { createContext, useContext, useState, useEffect } from "react";
import jwt_decode from "jwt-decode"; // ✅ proper import for Vite

const AuthContext = createContext();

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }) {
  // Initialize token from localStorage safely
  const [token, setToken] = useState(() => {
    const t = localStorage.getItem("token");
    return t && t !== "undefined" && t !== "null" ? t : null;
  });

  // Initialize user from token
  const [user, setUser] = useState(() => {
    return token ? decodeToken(token) : null;
  });

  // Login: save token and decode user
  function login(newToken) {
    if (!newToken) return;
    localStorage.setItem("token", newToken);
    setToken(newToken);
    const decoded = decodeToken(newToken);
    setUser(decoded);
    console.log("User logged in:", decoded); // 🔥 debug log
  }

  // Logout: remove token and user
  function logout() {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
    console.log("User logged out");
  }

  // Update user whenever token changes
  useEffect(() => {
    if (!token) {
      setUser(null);
      return;
    }

    const decoded = decodeToken(token);
    if (!decoded) {
      // Token invalid -> clear it
      console.warn("Invalid token detected, logging out");
      logout();
    } else {
      setUser(decoded);
    }
  }, [token]);

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

// Decode JWT safely
function decodeToken(token) {
  if (!token || typeof token !== "string") return null;

  try {
    const decoded = jwt_decode(token);

    // Optional: sanity check decoded object
    if (!decoded.sub) return null;

    return {
      email: decoded.sub,
      role: decoded.role,
      entrepriseId: decoded.entrepriseId,
    };
  } catch (err) {
    console.error("Failed to decode token:", err);
    return null;
  }
}
