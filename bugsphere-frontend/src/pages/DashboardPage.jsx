import { useEffect, useState } from "react";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/AppNavbar";
import BugCard from "../components/BugCard";

function StatCard({ label, value, color }) {
  return (
    <div className="bg-white rounded-lg border border-gray-200 p-4">
      <p className="text-sm text-gray-500 mb-1">{label}</p>
      <p className={"text-3xl font-bold " + color}>{value}</p>
    </div>
  );
}

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [myBugs, setMyBugs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, bugsRes] = await Promise.all([
          api.get("/bugs/stats"),
          api.get("/bugs/my"),
        ]);
        setStats(statsRes.data);
        setMyBugs(bugsRes.data);
      } catch (err) {
        setError("Failed to load dashboard data");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto px-4 py-8">
        <div className="mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Welcome back, {user && user.username}</h1>
          <p className="text-sm text-gray-500 mt-1">Here is what is happening in BugSphere</p>
        </div>
        {loading && <p className="text-gray-500 text-sm">Loading dashboard...</p>}
        {error && <div className="bg-red-50 text-red-700 text-sm p-3 rounded-lg mb-4">{error}</div>}
        {stats && (
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-8">
            <StatCard label="Total bugs"  value={stats.total}      color="text-gray-800" />
            <StatCard label="Open"        value={stats.open}       color="text-red-600" />
            <StatCard label="In progress" value={stats.inProgress} color="text-yellow-600" />
            <StatCard label="Resolved"    value={stats.resolved}   color="text-green-600" />
            <StatCard label="Closed"      value={stats.closed}     color="text-gray-400" />
          </div>
        )}
        <div>
          <h2 className="text-lg font-semibold text-gray-800 mb-3">Assigned to me ({myBugs.length})</h2>
          {myBugs.length === 0 && !loading ? (
            <p className="text-sm text-gray-400 bg-white border border-gray-200 rounded-lg p-6 text-center">
              No bugs assigned to you yet.
            </p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {myBugs.map((bug) => <BugCard key={bug.id} bug={bug} />)}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
