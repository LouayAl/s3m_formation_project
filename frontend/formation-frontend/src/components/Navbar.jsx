import React from "react";

export default function Navbar({ user }) {
  return (
    <header style={{
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      padding: "0 20px",
      height: 60,
      backgroundColor: "#16496E",
      color: "#fff",
      boxShadow: "0 2px 8px rgba(0,0,0,0.1)"
    }}>
      <div style={{ fontWeight: "bold", fontSize: 20 }}>S3M Dashboard</div>
      <div style={{ display: "flex", alignItems: "center", gap: 15 }}>
        <div>{user?.email}</div>
        <button style={{
          backgroundColor: "#F0813C",
          border: "none",
          borderRadius: 4,
          color: "#fff",
          padding: "5px 10px",
          cursor: "pointer"
        }}>Logout</button>
      </div>
    </header>
  );
}
