import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/AppNavbar';
import BugCard from '../components/BugCard';

export default function BugListPage() {
  const navigate = useNavigate();
  const [bugs, setBugs] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatus] = useState('ALL');

  useEffect(() => {
    api.get('/bugs').then((res) => { setBugs(res.data); setFiltered(res.data); })
      .catch(() => {}).finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    let result = bugs;
    if (statusFilter !== 'ALL') result = result.filter((b) => b.status === statusFilter);
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter((b) => b.title.toLowerCase().includes(q) || b.description?.toLowerCase().includes(q));
    }
    setFiltered(result);
  }, [search, statusFilter, bugs]);

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-5xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-900">All Bugs</h1>
          <button onClick={() => navigate('/bugs/new')}
            className="bg-indigo-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors">
            + New Bug
          </button>
        </div>
        <div className="flex gap-3 mb-6 flex-wrap">
          <input type="text" placeholder="Search bugs..." value={search} onChange={(e) => setSearch(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 flex-1 min-w-40" />
          <select value={statusFilter} onChange={(e) => setStatus(e.target.value)}
            className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500">
            <option value="ALL">All statuses</option>
            <option value="OPEN">Open</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CLOSED">Closed</option>
          </select>
        </div>
        <p className="text-sm text-gray-500 mb-4">Showing {filtered.length} bug{filtered.length !== 1 ? 's' : ''}</p>
        {loading ? <p className="text-gray-500 text-sm">Loading bugs...</p>
          : filtered.length === 0 ? <p className="text-sm text-gray-400 text-center py-12">No bugs found.</p>
          : <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filtered.map((bug) => <BugCard key={bug.id} bug={bug} />)}
            </div>}
      </div>
    </div>
  );
}
