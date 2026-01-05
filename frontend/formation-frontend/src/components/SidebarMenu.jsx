import React from "react";

const menuItems = [
  { title: "Dashboard", icon: "🏠" },
  { title: "Sessions", icon: "📅" },
  { title: "Formations", icon: "📚" },
  { title: "Finance", icon: "💰" },
  { title: "Users", icon: "👥" },
  { title: "Settings", icon: "⚙️" }
];

export default function SidebarMenu({ activePage, onSelect }) {
  return (
    <aside style={{
      width: 220,
      backgroundColor: "#F8F9FA",
      paddingTop: 20,
      borderRight: "1px solid #e0e0e0",
      minHeight: "100vh"
    }}>
      {menuItems.map(item => (
        <div
          key={item.title}
          onClick={() => onSelect(item.title)}
          style={{
            padding: "12px 20px",
            cursor: "pointer",
            backgroundColor: activePage === item.title ? "#E0E7FF" : "transparent",
            display: "flex",
            alignItems: "center",
            gap: 10,
            fontWeight: activePage === item.title ? "bold" : "normal"
          }}
        >
          <span>{item.icon}</span> {item.title}
        </div>
      ))}
    </aside>
  );
}
