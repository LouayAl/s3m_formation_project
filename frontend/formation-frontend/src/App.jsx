// formation-frontend/src/App.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";

import LoginPage from "./pages/auth/LoginPage";
import LandingPage from "./pages/dashboard/LandingPage";
import AdminSessionsPage from "./pages/AdminSessionsPage";
import AdminSessionDetailsPage from "./pages/admin/AdminSessionDetailsPage";

/* ---------- Route Guard ---------- */
function ProtectedRoute({ children }) {
  const { token } = useAuth();
  return token ? children : <Navigate to="/login" replace />;
}

/* ---------- App ---------- */
function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>

          {/* ---------- AUTH ---------- */}
          <Route path="/login" element={<LoginPage />} />

          {/* ---------- DASHBOARD ---------- */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <LandingPage />
              </ProtectedRoute>
            }
          />

          {/* ---------- ADMIN ---------- */}
          <Route
            path="/admin/sessions"
            element={
              <ProtectedRoute>
                <AdminSessionsPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin/sessions/:id"
            element={
              <ProtectedRoute>
                <AdminSessionDetailsPage />
              </ProtectedRoute>
            }
          />

          {/* ---------- DEFAULT ---------- */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />

        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
