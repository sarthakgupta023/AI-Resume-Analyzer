function StatCard({ title, value, subtitle }) {
    return (
      <div className="statCard">
        <p>{title}</p>
        <h3>{value}</h3>
        <span>{subtitle}</span>
      </div>
    );
  }
  
  export default StatCard;