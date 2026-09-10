function FitScoreCard({ score }) {
  const rounded = Math.round(score.overallScore);

  return (
    <div className="fit-score-card">
      <div className="fit-score-gauge">
        <div className="fit-score-number">{rounded}</div>
        <div className="fit-score-bar">
          <div className="fit-score-bar-fill" style={{ width: `${rounded}%` }} />
        </div>
        {score.yearsOfExperience != null && (
          <div className="fit-score-years">
            {score.yearsOfExperience}+ yrs experience detected
          </div>
        )}
      </div>

      <div className="fit-score-requirements">
        <div className="requirement-group">
          <span className="requirement-group-label matched">Matched requirements</span>
          <div className="requirement-chips">
            {score.matchedRequirements.length === 0 && <span className="requirement-empty">None</span>}
            {score.matchedRequirements.map((r, i) => (
              <span key={i} className="requirement-chip matched" title={`similarity ${r.similarity}`}>
                {r.requirement}
              </span>
            ))}
          </div>
        </div>
        <div className="requirement-group">
          <span className="requirement-group-label missing">Gap requirements</span>
          <div className="requirement-chips">
            {score.missingRequirements.length === 0 && <span className="requirement-empty">None</span>}
            {score.missingRequirements.map((r, i) => (
              <span key={i} className="requirement-chip missing" title={`similarity ${r.similarity}`}>
                {r.requirement}
              </span>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

function ResultsPanel({ result }) {
  const technical = result.questions.filter((q) => q.type === "technical");
  const behavioral = result.questions.filter((q) => q.type === "behavioral");

  return (
    <section className="results">
      <h2>Fit Summary</h2>
      <p className="summary">{result.summary}</p>

      {result.score && <FitScoreCard score={result.score} />}

      <div className="question-columns">
        <div className="question-column">
          <span className="column-badge technical">Technical — gap areas</span>
          <ol className="question-list">
            {technical.map((q, i) => (
              <li key={i}>
                <span className="question-number">{i + 1}</span>
                <span>{q.question}</span>
              </li>
            ))}
          </ol>
        </div>
        <div className="question-column">
          <span className="column-badge behavioral">Behavioral — strengths</span>
          <ol className="question-list">
            {behavioral.map((q, i) => (
              <li key={i}>
                <span className="question-number">{i + 1}</span>
                <span>{q.question}</span>
              </li>
            ))}
          </ol>
        </div>
      </div>
    </section>
  );
}

export default ResultsPanel;
