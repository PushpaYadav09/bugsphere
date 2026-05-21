import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/axios";

export default function RegisterPage() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    role: "ROLE_USER",
    adminCode: ""       // only sent when registering as admin
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Generic handler for all text inputs
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  // Role toggle handler — also clears adminCode when switching back to User
  // so a half-typed wrong code doesn't linger
  const handleRoleSelect = (selectedRole) => {
    setForm({
      ...form,
      role: selectedRole,
      adminCode: ""  // reset admin code whenever role changes
    });
    setError(""); // clear any previous "Invalid admin code" error
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // Send full form including role and adminCode
      // If role is ROLE_USER, backend ignores adminCode completely
      // If role is ROLE_ADMIN, backend checks adminCode against the secret
      await api.post("/auth/register", form);
      setSuccess("Account created! Redirecting to login...");
      setTimeout(() => navigate("/login"), 1500);
    } catch (err) {
      // Handles both validation errors (400) and wrong admin code (403)
      setError(err.response?.data || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 w-full max-w-sm">

        <div className="text-center mb-6">
          <h1 className="text-2xl font-bold text-indigo-700">BugSphere</h1>
          <p className="text-sm text-gray-500 mt-1">Create your account</p>
        </div>

        {error && (
          <div className="bg-red-50 text-red-700 text-sm px-3 py-2 rounded-lg mb-4 border border-red-200">
            {error}
          </div>
        )}
        {success && (
          <div className="bg-green-50 text-green-700 text-sm px-3 py-2 rounded-lg mb-4 border border-green-200">
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
            <input name="username" value={form.username} onChange={handleChange}
              required minLength={3}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="At least 3 characters" />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input type="email" name="email" value={form.email} onChange={handleChange}
              required
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="you@example.com" />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
            <input type="password" name="password" value={form.password} onChange={handleChange}
              required minLength={6}
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              placeholder="At least 6 characters" />
          </div>

          {/* Role selector */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Register as</label>
            <div className="grid grid-cols-2 gap-3">

              <button type="button" onClick={() => handleRoleSelect("ROLE_USER")}
                className={"rounded-lg border-2 py-3 text-sm font-medium transition-all " +
                  (form.role === "ROLE_USER"
                    ? "border-indigo-600 bg-indigo-50 text-indigo-700"
                    : "border-gray-200 text-gray-500 hover:border-gray-300")}>
                User
                <p className="text-xs font-normal mt-0.5 opacity-70">Report bugs</p>
              </button>

              <button type="button" onClick={() => handleRoleSelect("ROLE_ADMIN")}
                className={"rounded-lg border-2 py-3 text-sm font-medium transition-all " +
                  (form.role === "ROLE_ADMIN"
                    ? "border-indigo-600 bg-indigo-50 text-indigo-700"
                    : "border-gray-200 text-gray-500 hover:border-gray-300")}>
                Admin
                <p className="text-xs font-normal mt-0.5 opacity-70">Manage everything</p>
              </button>

            </div>
          </div>

          {/* Admin code field — only appears when Admin is selected */}
          {/* In React, {condition && <JSX>} means: only render if condition is true */}
          {form.role === "ROLE_ADMIN" && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Admin Code
                <span className="text-red-500 ml-1">*</span>
              </label>
              <input
                type="password"       // hide the code as they type
                name="adminCode"
                value={form.adminCode}
                onChange={handleChange}
                required              // required only when this field is visible
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                placeholder="Enter admin secret code" />
              <p className="text-xs text-gray-400 mt-1">
                Contact your system administrator for the code
              </p>
            </div>
          )}

          <button type="submit" disabled={loading}
            className="w-full bg-indigo-600 text-white rounded-lg py-2 text-sm font-medium hover:bg-indigo-700 disabled:opacity-50 transition-colors">
            {loading ? "Creating account..." : "Create account"}
          </button>

        </form>

        <p className="text-center text-sm text-gray-500 mt-4">
          Already have an account?{" "}
          <Link to="/login" className="text-indigo-600 hover:underline">Sign in</Link>
        </p>

      </div>
    </div>
  );
}
