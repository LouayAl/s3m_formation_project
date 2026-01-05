// src/layouts/MainLayout.jsx
import React, { useState } from "react";
import Navbar from "../components/Navbar";
import SidebarMenu from "../components/SidebarMenu";

export default function MainLayout({ children, user }) {
  const [activePage, setActivePage] = useState("Dashboard");

  return (
    <div style={{ display: "flex", minHeight: "100vh" }}>
      <SidebarMenu activePage={activePage} onSelect={setActivePage} />
      <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
        <Navbar user={user} />
        <main style={{ flex: 1, padding: 20, backgroundColor: "#F4F6F8" }}>
          {children}
        </main>
      </div>
    </div>
  );
}
