import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import AdminSessionsPage from "./pages/AdminSessionsPage";
import AdminSessionDetailsPage from "./pages/admin/AdminSessionDetailsPage";

/* ---------- Pages ---------- */

function LoginPage() {
  const { login } = useAuth();

  return (
    <div style={{ padding: 40 }}>
      <h2>Admin Login</h2>
      <button onClick={login}>Login as Admin</button>
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

          {/* Admin */}
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

          {/* Default */}
          <Route path="*" element={<Navigate to="/admin/sessions" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
