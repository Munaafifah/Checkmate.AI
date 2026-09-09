function ResultsPanel({ result }) {
  const technical = result.questions.filter((q) => q.type === "technical");
  const behavioral = result.questions.filter((q) => q.type === "behavioral");

  return (
    <section className="results">
      <h2>Fit Summary</h2>
      <p className="summary">{result.summary}</p>

      <div className="question-columns">
        <div className="question-column">
          <h3>Technical (gap areas)</h3>
          <ol>
            {technical.map((q, i) => (
              <li key={i}>{q.question}</li>
            ))}
          </ol>
        </div>
        <div className="question-column">
          <h3>Behavioral (strengths)</h3>
          <ol>
            {behavioral.map((q, i) => (
              <li key={i}>{q.question}</li>
            ))}
          </ol>
        </div>
      </div>
    </section>
  );
}

export default ResultsPanel;
