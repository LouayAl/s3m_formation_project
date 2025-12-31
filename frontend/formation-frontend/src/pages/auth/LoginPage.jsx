import { useState } from "react";
import logo from "../../assets/logo/Logo.webp";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    // TEMP (backend will come next)
    setTimeout(() => {
      setLoading(false);
      setError("Backend not connected yet");
    }, 1000);
  };

  return (
    <div style={styles.page}>
      <div style={styles.card} className="fade-in">
        {/* Logo */}
        <div style={styles.logoContainer}>
          <img src={logo} alt="Company Logo" style={styles.logo} />
        </div>

        {/* Header */}
        <h1 style={styles.title}>Welcome Back</h1>
        <p style={styles.subtitle}>Sign in to access your dashboard</p>

        {/* Form */}
        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.inputGroup}>
            <label style={styles.label}>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@company.com"
              style={styles.input}
              required
            />
          </div>

          <div style={styles.inputGroup}>
            <label style={styles.label}>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              style={styles.input}
              required
            />
          </div>

          {error && <div style={styles.error}>{error}</div>}

          <button
            type="submit"
            disabled={loading}
            style={styles.button}
            className="gradient-button"
          >
            {loading ? "Signing in..." : "Login"}
          </button>
        </form>

        {/* Footer */}
        <div style={styles.footer}>
          <span style={styles.footerText}>
            © {new Date().getFullYear()} Training Management Platform
          </span>
        </div>
      </div>

      {/* Animations & hover effects */}
      <style>{`
        .fade-in {
          animation: fadeIn 1s ease-out;
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(-20px); }
          to { opacity: 1; transform: translateY(0); }
        }

        .gradient-button {
          transition: all 0.3s ease;
        }

        .gradient-button:hover {
          background: linear-gradient(135deg, rgb(35,129,192), rgb(240,129,60));
          transform: translateY(-2px);
        }

        .gradient-button:disabled {
          transform: none;
        }
      `}</style>
    </div>
  );
}

/* ================= STYLES ================= */

const styles = {
  page: {
    height: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "linear-gradient(135deg, rgb(22,73,110), rgb(35,129,192))",
  },

  card: {
    width: "100%",
    maxWidth: 420,
    background: "#ffffff",
    borderRadius: 12,
    padding: "40px 35px",
    boxShadow: "0 20px 40px rgba(0,0,0,0.15)",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },

  logoContainer: {
    display: "flex",
    justifyContent: "center",
    marginBottom: 25,
  },

  logo: {
    width: 200,
    height: "auto",
  },

  title: {
    margin: 0,
    textAlign: "center",
    color: "rgb(22,73,110)",
    fontSize: 28,
    fontWeight: 700,
  },

  subtitle: {
    textAlign: "center",
    color: "#666",
    marginBottom: 30,
  },

  form: {
    width: "100%",
    display: "flex",
    flexDirection: "column",
    gap: 18,
  },

  inputGroup: {
    display: "flex",
    flexDirection: "column",
    gap: 6,
  },

  label: {
    fontSize: 14,
    fontWeight: 600,
    color: "rgb(22,73,110)",
  },

  input: {
    padding: "12px 14px",
    fontSize: 15,
    borderRadius: 8,
    border: "1px solid #ddd",
    outline: "none",
    transition: "border 0.2s",
  },

  button: {
    marginTop: 10,
    padding: "14px",
    fontSize: 16,
    fontWeight: 600,
    color: "#fff",
    background: "rgb(240,129,60)",
    border: "none",
    borderRadius: 8,
    cursor: "pointer",
  },

  error: {
    background: "#fdecea",
    color: "#b71c1c",
    padding: "10px 12px",
    borderRadius: 6,
    fontSize: 14,
  },

  footer: {
    marginTop: 30,
    textAlign: "center",
  },

  footerText: {
    fontSize: 12,
    color: "#999",
  },
};
