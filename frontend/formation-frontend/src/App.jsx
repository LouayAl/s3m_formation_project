import { BrowserRouter, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import AdminSessionsPage from "./pages/AdminSessionsPage";
import AdminSessionDetailsPage from "./pages/admin/AdminSessionDetailsPage";
import LandingPage from "./pages/dashboard/LandingPage";

/* ---------- Pages ---------- */

function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = () => {
    login(); // your login function
    navigate("/dashboard"); // redirect to landing page after login
  };

  return (
    <div style={{ padding: 40 }}>
      <h2>Admin Login</h2>
      <button onClick={handleLogin}>Login as Admin</button>
    </div>
  );
}

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
          {/* Auth */}
          <Route path="/login" element={<LoginPage />} />

          {/* Dashboard / Landing */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <LandingPage />
              </ProtectedRoute>
            }
          />

          {/* Admin Sessions */}
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

          {/* Default redirect to dashboard */}
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
