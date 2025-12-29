const STATUS_CONFIG = {
  PLANIFIEE: {
    label: "Planifiée",
    color: "#2563eb", // blue
    bg: "#dbeafe",
  },
  EN_COURS: {
    label: "En cours",
    color: "#15803d", // green
    bg: "#dcfce7",
  },
  TERMINEE: {
    label: "Terminée",
    color: "#374151", // gray
    bg: "#e5e7eb",
  },
  ANNULEE: {
    label: "Annulée",
    color: "#b91c1c", // red
    bg: "#fee2e2",
  },
};

export default function StatusBadge({ status }) {
  const config = STATUS_CONFIG[status];

  if (!config) return null;

  return (
    <span
      style={{
        padding: "4px 10px",
        borderRadius: "999px",
        fontSize: 12,
        fontWeight: 600,
        color: config.color,
        backgroundColor: config.bg,
        display: "inline-block",
        minWidth: 80,
        textAlign: "center",
      }}
    >
      {config.label}
    </span>
  );
}
