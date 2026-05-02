import { BrainCircuit, Lock, Mail } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/api";

function Login() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await API.post("/api/auth/login", form);

      localStorage.setItem("token", res.data.token);
      localStorage.setItem("name", res.data.name);
      localStorage.setItem("email", res.data.email);

      navigate("/dashboard");
    } catch (err) {
      setError(err.response?.data?.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="authPage">
      <div className="authLeft">
        <div className="authBrand">
          <div className="brandIcon big">
            <BrainCircuit size={30} />
          </div>
          <h1>ResumeIQ</h1>
        </div>

        <h2>Welcome back</h2>
        <p>
          Analyze your resume against real job descriptions and get recruiter-level
          feedback in seconds.
        </p>

        <div className="authHighlights">
          <span>ATS Score</span>
          <span>Skill Gap Analysis</span>
          <span>AI Suggestions</span>
        </div>
      </div>

      <div className="authCard">
        <h2>Login</h2>
        <p className="authSub">Continue to your resume intelligence dashboard.</p>

        {error && <div className="errorBox">{error}</div>}

        <form onSubmit={handleLogin}>
          <label>Email</label>
          <div className="inputBox">
            <Mail size={18} />
            <input
              name="email"
              type="email"
              placeholder="you@example.com"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>

          <label>Password</label>
          <div className="inputBox">
            <Lock size={18} />
            <input
              name="password"
              type="password"
              placeholder="••••••••"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <button className="primaryBtn full" disabled={loading}>
            {loading ? "Signing in..." : "Sign in"}
          </button>
        </form>

        <p className="switchText">
          New here? <Link to="/register">Create account</Link>
        </p>
      </div>
    </div>
  );
}

export default Login;