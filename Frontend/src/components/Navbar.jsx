import { BrainCircuit, History, LayoutDashboard, LogOut } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";

function Navbar() {
  const navigate = useNavigate();
  const name = localStorage.getItem("name") || "User";

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("name");
    localStorage.removeItem("email");
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="brand">
        <div className="brandIcon">
          <BrainCircuit size={22} />
        </div>
        <div>
          <h2>ResumeIQ</h2>
          <span>AI Resume Intelligence</span>
        </div>
      </div>

      <div className="navLinks">
        <Link to="/dashboard" className="navItem">
          <LayoutDashboard size={17} />
          Dashboard
        </Link>

        <Link to="/history" className="navItem">
          <History size={17} />
          History
        </Link>

        <div className="profileChip">{name.charAt(0).toUpperCase()}</div>

        <button className="logoutBtn" onClick={logout}>
          <LogOut size={17} />
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;