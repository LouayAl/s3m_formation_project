// import { useState, useEffect } from "react";
// import Navbar from "../../components/Navbar";
// import SidebarMenu from "../../components/SidebarMenu";
// import KPIBox from "../../components/KPIBox";
// import LoadingSkeleton from "../../components/LoadingSkeleton";
// import { getClientKpis } from "../../api/kpiApi";
// import { useAuth } from "../../context/AuthContext";

// const COLORS = ["#2381C0", "#F0813C", "#16496E"];

// export default function DashboardPage() {
//   const { user } = useAuth();
//   const [kpis, setKpis] = useState(null);
//   const [loading, setLoading] = useState(true);
//   const [activePage, setActivePage] = useState("Dashboard");

//   useEffect(() => {
//     getClientKpis(user?.entrepriseId)
//       .then(data => setKpis(data))
//       .finally(() => setLoading(false));
//   }, [user]);

//   return (
//     <div style={{ display: "flex" }}>
//       <SidebarMenu activePage={activePage} onSelect={setActivePage} />
//       <div style={{ flex: 1 }}>
//         <Navbar user={user} />
//         <main style={{ padding: 20 }}>
//           <h2>Welcome back, {user?.email}!</h2>

//           <section style={{ display: "flex", gap: 20, flexWrap: "wrap", marginTop: 20 }}>
//             {loading
//               ? Array(4).fill(0).map((_, idx) => <LoadingSkeleton key={idx} height={80} />)
//               : kpis?.volume && Object.entries(kpis.volume).map(([key, value], idx) => (
//                   <KPIBox key={idx} title={key.replace(/([A-Z])/g, ' $1')} value={value} color={COLORS[idx % COLORS.length]} />
//                 ))
//             }
//           </section>

//           {/* Charts and other sections go here */}
//         </main>
//       </div>
//     </div>
//   );
// }
