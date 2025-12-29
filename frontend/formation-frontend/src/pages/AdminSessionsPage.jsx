import { useEffect, useState } from "react";
import { fetchAdminSessions } from "../api/adminSessionApi";
import AdminSessionTable from "../components/AdminSessionTable";

export default function AdminSessionsPage() {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAdminSessions()
      .then(setSessions)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Loading sessions...</p>;

  return (
    <div>
      <h2>Admin – Sessions</h2>
      <AdminSessionTable sessions={sessions} />
    </div>
  );
}
