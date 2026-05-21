import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-indigo-700 text-white px-6 py-3 flex items-center justify-between shadow">
      <Link to="/dashboard" className="text-xl font-bold tracking-tight">
        BugSphere
      </Link>
      <div className="flex items-center gap-6 text-sm">
        <Link to="/dashboard" className="hover:text-indigo-200 transition-colors">Dashboard</Link>
        <Link to="/bugs" className="hover:text-indigo-200 transition-colors">Bugs</Link>
        <Link to="/projects" className="hover:text-indigo-200 transition-colors">Projects</Link>
        {isAdmin && (
          <Link to="/users" className="hover:text-indigo-200 transition-colors">Users</Link>
        )}
      </div>
      <div className="flex items-center gap-4 text-sm">
        <span className="text-indigo-200">
          Hello, {user?.username}
          {isAdmin && (
            <span className="ml-2 bg-indigo-500 text-xs px-2 py-0.5 rounded-full">Admin</span>
          )}
        </span>
        <button onClick={handleLogout}
          className="bg-indigo-600 hover:bg-indigo-500 px-3 py-1.5 rounded text-sm transition-colors">
          Logout
        </button>
      </div>
    </nav>
  );
}
