import { useNavigate } from "react-router-dom";
import StatusBadge from "../components/StatusBadge";

export default function AdminSessionTable({ sessions }) {
  const navigate = useNavigate();

  const handleRowClick = (sessionId) => {
    navigate(`/admin/sessions/${sessionId}`);
  };

  return (
    <table border="1" cellPadding="8" width="100%">
      <thead>
        <tr>
          <th>ID</th>
          <th>Formation</th>
          <th>Entreprise</th>
          <th>Début</th>
          <th>Fin</th>
          <th>Statut</th>
        </tr>
      </thead>
      <tbody>
        {sessions.map((session) => (
          <tr
            key={session.sessionId}
            onClick={() => handleRowClick(session.sessionId)}
            style={{ cursor: "pointer" }}
          >
            <td>{session.sessionId}</td>
            <td>{session.formationModule}</td>
            <td>{session.entrepriseNom}</td>
            <td>{session.dateDebut}</td>
            <td>{session.dateFin}</td>
            <td><StatusBadge status={session.statut} /></td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
