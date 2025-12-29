import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import {
  fetchAdminSessionById,
  demarrerSession,
  terminerSession,
  annulerSession
} from "../../api/adminSessionApi";
import { fetchSessionAudit } from "../../api/auditApi";
import StatusBadge from "../../components/StatusBadge";

const statusStyles = {
  PLANIFIEE: { color: "#2563eb", icon: "📅" },   // blue
  EN_COURS: { color: "#16a34a", icon: "▶️" },    // green
  TERMINEE: { color: "#6b7280", icon: "✅" },    // gray
  ANNULEE: { color: "#dc2626", icon: "❌" }      // red
};

const getStatusStyle = (status) =>
  statusStyles[status] || { color: "#374151", icon: "🔄" };


export default function AdminSessionDetailsPage() {
  const { id } = useParams();

  const [session, setSession] = useState(null);
  const [audit, setAudit] = useState([]);
  const [loading, setLoading] = useState(true);

  const refreshSession = () => {
    fetchAdminSessionById(id).then(setSession);
    fetchSessionAudit(id).then(setAudit);
  };

  useEffect(() => {
    refreshSession();
    setLoading(false);
  }, [id]);

  if (loading) return <p>Loading...</p>;
  if (!session) return <p>Session not found</p>;

  const handleDemarrer = async () => {
    await demarrerSession(id);
    refreshSession();
  };

  const handleTerminer = async () => {
    await terminerSession(id);
    refreshSession();
  };

  const handleAnnuler = async () => {
    await annulerSession(id);
    refreshSession();
  };

  return (
    <div style={{ padding: 20 }}>
        <h2>Session Details</h2>

        <p><strong>ID:</strong> {session.idSession}</p>
      <p><strong>Formation:</strong> {session.formation?.module}</p>
      <p>
        <strong>Status:</strong>{" "}
        <StatusBadge status={session.statut} />
      </p>
      <p><strong>Date début:</strong> {session.dateDebut}</p>
      <p><strong>Date fin:</strong> {session.dateFin}</p>

      <hr />

      <h3>Actions</h3>

      <button
        onClick={handleDemarrer}
        disabled={session.statut !== "PLANIFIEE"}
      >
        Démarrer
      </button>

      <button
        onClick={handleTerminer}
        disabled={session.statut !== "EN_COURS"}
      >
        Terminer
      </button>

      <button
        onClick={handleAnnuler}
        disabled={session.statut === "TERMINEE"}
      >
        Annuler
      </button>

      <hr />

        <h3>Audit Timeline</h3>

        {audit.length === 0 && (
        <p style={{ color: "#6b7280" }}>No audit history</p>
        )}

        <ul style={{ listStyle: "none", padding: 0 }}>
        {audit.map((entry) => {
            const from = getStatusStyle(entry.statutAvant);
            const to = getStatusStyle(entry.statutApres);

            return (
            <li
                key={entry.auditId}
                style={{
                display: "flex",
                gap: 12,
                marginBottom: 20
                }}
            >
                {/* Timeline dot */}
                <div
                style={{
                    width: 12,
                    height: 12,
                    marginTop: 6,
                    borderRadius: "50%",
                    backgroundColor: to.color
                }}
                />

                {/* Content */}
                <div
                style={{
                    background: "#f9fafb",
                    padding: 12,
                    borderRadius: 8,
                    width: "100%",
                    borderLeft: `4px solid ${to.color}`
                }}
                >
                <div style={{ fontWeight: 600 }}>
                    <span style={{ color: from.color }}>
                    {from.icon} {entry.statutAvant}
                    </span>
                    {" → "}
                    <span style={{ color: to.color }}>
                    {to.icon} {entry.statutApres}
                    </span>
                </div>

                <div
                    style={{
                    fontSize: 12,
                    color: "#6b7280",
                    marginTop: 4
                    }}
                >
                    👤 {entry.modifiePar || "system"} • 🕒{" "}
                    {new Date(entry.dateModification).toLocaleString()}
                </div>

                {entry.commentaire && (
                    <div style={{ marginTop: 6 }}>
                    💬 {entry.commentaire}
                    </div>
                )}
                </div>
            </li>
            );
        })}
        </ul>

    </div>
  );
}
