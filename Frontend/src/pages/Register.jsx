import { BrainCircuit, Lock, Mail, User } from "lucide-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/api";

function Register() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
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

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await API.post("/auth/register", form);

      localStorage.setItem("token", res.data.token);
      localStorage.setItem("name", res.data.name);
      localStorage.setItem("email", res.data.email);

      navigate("/dashboard");
    } catch (err) {
      setError(err.response?.data?.message || "Registration failed");
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

        <h2>Build a sharper resume</h2>
        <p>
          Upload your resume, compare it with job descriptions, and receive
          structured AI feedback made for real hiring workflows.
        </p>

        <div className="authHighlights">
          <span>Recruiter Style Feedback</span>
          <span>Keyword Matching</span>
          <span>Saved History</span>
        </div>
      </div>

      <div className="authCard">
        <h2>Create Account</h2>
        <p className="authSub">Start analyzing resumes with AI-powered insights.</p>

        {error && <div className="errorBox">{error}</div>}

        <form onSubmit={handleRegister}>
          <label>Name</label>
          <div className="inputBox">
            <User size={18} />
            <input
              name="name"
              type="text"
              placeholder="Sarthak Gupta"
              value={form.name}
              onChange={handleChange}
              required
            />
          </div>

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
              placeholder="Minimum 6 characters"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          <button className="primaryBtn full" disabled={loading}>
            {loading ? "Creating account..." : "Create account"}
          </button>
        </form>

        <p className="switchText">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;