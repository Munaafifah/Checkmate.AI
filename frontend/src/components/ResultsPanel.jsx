function ResultsPanel({ result }) {
  const technical = result.questions.filter((q) => q.type === "technical");
  const behavioral = result.questions.filter((q) => q.type === "behavioral");

  return (
    <section className="results">
      <h2>Fit Summary</h2>
      <p className="summary">{result.summary}</p>

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
