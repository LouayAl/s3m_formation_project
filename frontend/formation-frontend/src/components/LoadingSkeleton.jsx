import React from "react";

export default function LoadingSkeleton({ width = "100%", height = 20 }) {
  return (
    <div style={{
      width,
      height,
      backgroundColor: "#e0e0e0",
      borderRadius: 4,
      marginBottom: 10,
      animation: "pulse 1.5s infinite"
    }} />
  );
}
