import { useState } from "react";
import { login as loginApi } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);

  const { login } = useAuth();
  const navigate = useNavigate();

    const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    try {
        const data = await loginApi();
        login(data.token);
        navigate("/admin");
    } catch (err) {
        setError("Login failed");
    }
    };


  return (
    <div>
      <h2>Admin Login</h2>

        <form onSubmit={handleSubmit}>
            <button type="submit">Login as Admin</button>
        </form>


      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}
