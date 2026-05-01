function ResultCard({ title, items, type }) {
    return (
      <div className={`resultCard ${type || ""}`}>
        <h3>{title}</h3>
  
        {items && items.length > 0 ? (
          <ul>
            {items.map((item, index) => (
              <li key={index}>{item}</li>
            ))}
          </ul>
        ) : (
          <p className="muted">No data available</p>
        )}
      </div>
    );
  }
  
  export default ResultCard;