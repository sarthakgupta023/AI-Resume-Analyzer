import {
    FileText,
    Loader2,
    ShieldCheck,
    Sparkles,
    Target,
    UploadCloud,
} from "lucide-react";
import { useState } from "react";
import API from "../api/api";
import Navbar from "../components/Navbar";
import ResultCard from "../components/ResultCard";
import StatCard from "../components/StatCard";

function Dashboard() {
  const [file, setFile] = useState(null);
  const [jobDescription, setJobDescription] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [dragging, setDragging] = useState(false);
  const [error, setError] = useState("");

  const handleAnalyze = async () => {
    if (!file) {
      setError("Please upload your resume PDF.");
      return;
    }

    if (!jobDescription.trim()) {
      setError("Please paste the job description.");
      return;
    }

    setError("");
    setLoading(true);
    setResult(null);

    try {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("jobDescription", jobDescription);

      const res = await API.post("/analyze", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      setResult(res.data);
    } catch (err) {
      setError(err.response?.data?.message || "Analysis failed");
    } finally {
      setLoading(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragging(false);

    const uploadedFile = e.dataTransfer.files[0];

    if (uploadedFile && uploadedFile.type === "application/pdf") {
      setFile(uploadedFile);
    } else {
      setError("Only PDF files are allowed.");
    }
  };

  const scoreClass =
    result?.atsScore >= 80
      ? "scoreGood"
      : result?.atsScore >= 60
      ? "scoreMedium"
      : "scoreLow";

  return (
    <div className="appShell">
      <Navbar />

      <main className="dashboard">
        <section className="heroPanel">
          <div>
            <div className="badge">
              <Sparkles size={16} />
              AI Powered Resume Intelligence
            </div>

            <h1>Analyze your resume against any job description.</h1>

            <p>
              Upload your PDF resume, paste a role description, and get a clean
              ATS-style score with missing skills, strengths, weaknesses, and
              improvement suggestions.
            </p>
          </div>

          <div className="heroStats">
            <StatCard title="Analysis Type" value="ATS + AI" subtitle="Role based" />
            <StatCard title="Privacy" value="Secure" subtitle="JWT protected" />
          </div>
        </section>

        <section className="workGrid">
          <div className="glassCard uploadPanel">
            <div className="sectionTitle">
              <FileText size={20} />
              <h2>Resume Input</h2>
            </div>

            <div
              className={`dropZone ${dragging ? "dragging" : ""}`}
              onDragOver={(e) => {
                e.preventDefault();
                setDragging(true);
              }}
              onDragLeave={() => setDragging(false)}
              onDrop={handleDrop}
            >
              <UploadCloud size={42} />
              <h3>{file ? file.name : "Drop your resume here"}</h3>
              <p>PDF only, max 10MB recommended</p>

              <label className="secondaryBtn">
                Choose PDF
                <input
                  type="file"
                  accept="application/pdf"
                  hidden
                  onChange={(e) => setFile(e.target.files[0])}
                />
              </label>
            </div>

            <div className="sectionTitle small">
              <Target size={19} />
              <h2>Job Description</h2>
            </div>

            <textarea
              className="jdBox"
              placeholder="Paste the complete job description here..."
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
            />

            {error && <div className="errorBox">{error}</div>}

            <button className="primaryBtn analyzeBtn" onClick={handleAnalyze} disabled={loading}>
              {loading ? (
                <>
                  <Loader2 className="spin" size={19} />
                  Analyzing Resume...
                </>
              ) : (
                <>
                  <Sparkles size={19} />
                  Analyze Resume
                </>
              )}
            </button>
          </div>

          <div className="glassCard resultPanel">
            {!result && !loading && (
              <div className="emptyState">
                <ShieldCheck size={54} />
                <h2>Your analysis will appear here</h2>
                <p>
                  You’ll get ATS score, missing skills, improvement areas, and
                  recruiter-style feedback.
                </p>
              </div>
            )}

            {loading && (
              <div className="loadingState">
                <div className="loaderRing"></div>
                <h2>Reading your resume</h2>
                <p>AI is comparing your resume with the target job description.</p>
              </div>
            )}

            {result && (
              <div className="analysisResult">
                <div className="scoreHeader">
                  <div>
                    <p>ATS Match Score</p>
                    <h2>Resume Analysis</h2>
                  </div>

                  <div className={`scoreCircle ${scoreClass}`}>
                    {result.atsScore}
                    <span>/100</span>
                  </div>
                </div>

                <div className="summaryBox">
                  <h3>Recruiter Summary</h3>
                  <p>{result.summary}</p>
                </div>

                <div className="resultGrid">
                  <ResultCard
                    title="Strengths"
                    items={result.strengths}
                    type="green"
                  />

                  <ResultCard
                    title="Weaknesses"
                    items={result.weaknesses}
                    type="orange"
                  />

                  <ResultCard
                    title="Missing Skills"
                    items={result.missingSkills}
                    type="blue"
                  />

                  <ResultCard
                    title="Suggestions"
                    items={result.suggestions}
                    type="purple"
                  />
                </div>
              </div>
            )}
          </div>
        </section>
      </main>
    </div>
  );
}

export default Dashboard;