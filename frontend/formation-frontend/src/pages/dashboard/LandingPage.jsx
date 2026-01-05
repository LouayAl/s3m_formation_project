import { useEffect, useState } from "react";
import { useAuth } from "../../context/AuthContext";
import { getClientKpis } from "../../api/kpiApi";
import KPIBox from "../../components/KPIBox";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import MainLayout from "../../layouts/MainLayout";

const COLORS = ["#2381C0", "#F0813C", "#16496E"];

export default function LandingPage() {
  const { token, user } = useAuth();
  const [kpis, setKpis] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token && user?.entrepriseId) {
      getClientKpis(user.entrepriseId, token)
        .then(setKpis)
        .catch(err => console.error("Failed to fetch KPIs:", err))
        .finally(() => setLoading(false));
    } else setLoading(false);
  }, [token, user]);

  if (loading) return <div>Loading KPIs...</div>;
  if (!kpis) return <div>No KPIs found.</div>;

  const { volume, financier, formations, population, efficacite } = kpis;

  return (
    <MainLayout user={user}>
      {/* Greeting */}
      <h2 style={{ color: "#16496E", marginBottom: 20 }}>
        Welcome back, {user.email}!
      </h2>
      <h1>Client KPIs Dashboard</h1>

      {/* KPI Boxes */}
      <div style={{ display: "flex", gap: 20, marginTop: 20, flexWrap: "wrap" }}>
        <KPIBox title="Total Sessions" value={volume.totalSessions} color={COLORS[0]} />
        <KPIBox title="Total Participants" value={volume.totalParticipants} color={COLORS[1]} />
        <KPIBox title="Total Hours" value={volume.totalHeuresFormation} color={COLORS[2]} />
        <KPIBox title="Total Days" value={volume.totalJoursFormation} color={COLORS[0]} />
      </div>

      {/* Financial KPIs */}
      <div style={{ display: "flex", gap: 20, marginTop: 20, flexWrap: "wrap" }}>
        <KPIBox title="Total Cost" value={financier.coutTotalFormation} color={COLORS[1]} />
        <KPIBox title="Avg Cost/Day" value={financier.coutMoyenParJour} color={COLORS[2]} />
        <KPIBox title="Avg Cost/Participant" value={financier.coutMoyenParParticipant} color={COLORS[0]} />
        <KPIBox title="Reimbursed" value={financier.montantRembourse} color={COLORS[1]} />
      </div>

      {/* Formations KPIs */}
      <div style={{ display: "flex", gap: 20, marginTop: 20, flexWrap: "wrap" }}>
        <KPIBox title="Formations Distinctes" value={formations.nombreFormationsDistinctes} color={COLORS[2]} />
        <KPIBox title="Most Attended Formation" value={formations.formationLaPlusSuivie || "-"} color={COLORS[0]} />
        <KPIBox title="Main Family" value={formations.familleFormationPrincipale || "-"} color={COLORS[1]} />
        <KPIBox title="Internal %" value={formations.pourcentageInterne.toFixed(2)} color={COLORS[2]} />
        <KPIBox title="External %" value={formations.pourcentageExterne.toFixed(2)} color={COLORS[0]} />
      </div>

      {/* Efficacy */}
      <div style={{ display: "flex", gap: 20, marginTop: 20, flexWrap: "wrap" }}>
        <KPIBox title="Efficacy %" value={efficacite.pourcentageEvalue.toFixed(2)} color={COLORS[1]} />
        <KPIBox title="Average Efficacy" value={efficacite.tauxEfficaciteMoyen.toFixed(2)} color={COLORS[2]} />
      </div>

      {/* Population Pie Charts */}
      <div style={{ display: "flex", gap: 40, marginTop: 40, flexWrap: "wrap" }}>
        {[
          { title: "CSP", data: population.repartitionCsp },
          { title: "Fonction", data: population.repartitionFonction },
          { title: "Type Contrat", data: population.repartitionTypeContrat },
          { title: "Genre", data: population.repartitionGenre },
        ].map((item, idx) => (
          <div key={idx} style={{ width: 280, height: 280, boxShadow: "0 4px 12px rgba(0,0,0,0.1)", borderRadius: 12 }}>
            <h4 style={{ textAlign: "center", marginTop: 10 }}>{item.title}</h4>
            <ResponsiveContainer width={280} height={280}>
              <PieChart>
                <Pie
                  dataKey="nombre"
                  nameKey="libelle"
                  data={item.data}
                  cx="50%"
                  cy="50%"
                  outerRadius={90}
                  label
                >
                  {item.data.map((_, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend verticalAlign="bottom" height={36} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        ))}
      </div>
    </MainLayout>
  );
}
