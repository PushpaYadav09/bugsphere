import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/AppNavbar';

export default function ProjectsPage() {
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingProject, setEditing] = useState(null);
  const [form, setForm] = useState({ name: '', description: '' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    api.get('/projects').then((res) => setProjects(res.data)).finally(() => setLoading(false));
  }, []);

  const openCreate = () => { setEditing(null); setForm({ name: '', description: '' }); setError(''); setShowModal(true); };
  const openEdit = (p) => { setEditing(p); setForm({ name: p.name, description: p.description || '' }); setError(''); setShowModal(true); };

  const handleSave = async () => {
    setSaving(true); setError('');
    try {
      if (editingProject) {
        const res = await api.put('/projects/' + editingProject.id, form);
        setProjects(projects.map((p) => p.id === editingProject.id ? res.data : p));
      } else {
        const res = await api.post('/projects', form);
        setProjects([...projects, res.data]);
      }
      setShowModal(false);
    } catch (err) { setError(err.response?.data?.name || 'Failed to save'); }
    finally { setSaving(false); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this project and all its bugs?')) return;
    try { await api.delete('/projects/' + id); setProjects(projects.filter((p) => p.id !== id)); }
    catch { alert('Failed to delete project'); }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-900">Projects</h1>
          {isAdmin && <button onClick={openCreate} className="bg-indigo-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors">+ New Project</button>}
        </div>
        {loading ? <p className="text-gray-500 text-sm">Loading...</p> : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {projects.map((project) => (
              <div key={project.id} className="bg-white border border-gray-200 rounded-xl p-5 hover:shadow-sm transition-shadow">
                <div className="flex items-start justify-between mb-2">
                  <h2 className="font-semibold text-gray-900">{project.name}</h2>
                  <span className="text-xs bg-indigo-50 text-indigo-700 px-2 py-0.5 rounded-full">{project.bugCount} bugs</span>
                </div>
                <p className="text-sm text-gray-500 mb-4">{project.description || 'No description'}</p>
                <div className="flex gap-2">
                  <button onClick={() => navigate('/bugs?project=' + project.id)} className="text-xs border border-gray-300 px-3 py-1.5 rounded-lg hover:bg-gray-50">View bugs</button>
                  {isAdmin && <>
                    <button onClick={() => openEdit(project)} className="text-xs border border-gray-300 px-3 py-1.5 rounded-lg hover:bg-gray-50">Edit</button>
                    <button onClick={() => handleDelete(project.id)} className="text-xs border border-red-200 text-red-600 px-3 py-1.5 rounded-lg hover:bg-red-50">Delete</button>
                  </>}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl p-6 w-full max-w-md shadow-xl">
            <h2 className="text-lg font-semibold mb-4">{editingProject ? 'Edit Project' : 'New Project'}</h2>
            {error && <div className="text-red-600 text-sm mb-3">{error}</div>}
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="Project name"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm mb-3 focus:outline-none focus:ring-2 focus:ring-indigo-500" />
            <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Description (optional)" rows={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm mb-4 focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none" />
            <div className="flex gap-3">
              <button onClick={handleSave} disabled={saving} className="flex-1 bg-indigo-600 text-white rounded-lg py-2 text-sm hover:bg-indigo-700 disabled:opacity-50">{saving ? 'Saving...' : 'Save'}</button>
              <button onClick={() => setShowModal(false)} className="flex-1 border border-gray-300 rounded-lg py-2 text-sm hover:bg-gray-50">Cancel</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
