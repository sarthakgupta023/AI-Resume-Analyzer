import { Clock, FileText, Loader2 } from "lucide-react";
import { useEffect, useState } from "react";
import API from "../api/api";
import Navbar from "../components/Navbar";

function History() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);

  const getHistory = async () => {
    try {
      const res = await API.get("/history");
      setHistory(res.data);
    } catch (err) {
      console.log(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getHistory();
  }, []);

  return (
    <div className="appShell">
      <Navbar />

      <main className="dashboard">
        <section className="pageHeader">
          <div>
            <h1>Analysis History</h1>
            <p>Review your previously analyzed resumes and ATS scores.</p>
          </div>
        </section>

        <div className="historyWrapper">
          {loading && (
            <div className="loadingState smallLoader">
              <Loader2 className="spin" size={26} />
              <p>Loading history...</p>
            </div>
          )}

          {!loading && history.length === 0 && (
            <div className="glassCard emptyHistory">
              <FileText size={44} />
              <h2>No analysis history yet</h2>
              <p>Analyze your first resume to see it here.</p>
            </div>
          )}

          {!loading &&
            history.map((item) => (
              <div className="historyCard" key={item.id}>
                <div className="historyTop">
                  <div>
                    <h3>{item.fileName}</h3>
                    <p>
                      <Clock size={15} />
                      {item.createdAt
                        ? new Date(item.createdAt).toLocaleString()
                        : "Recently"}
                    </p>
                  </div>

                  <div className="miniScore">{item.atsScore}/100</div>
                </div>

                <p className="historySummary">{item.summary}</p>

                <div className="historyTags">
                  {item.missingSkills?.slice(0, 5).map((skill, index) => (
                    <span key={index}>{skill}</span>
                  ))}
                </div>
              </div>
            ))}
        </div>
      </main>
    </div>
  );
}

export default History;