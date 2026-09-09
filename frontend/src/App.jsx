import { useState } from "react";
import "./App.css";
import { uploadResumeFile, analyze } from "./api";
import ResultsPanel from "./components/ResultsPanel";

function App() {
  const [resumeMode, setResumeMode] = useState("file");
  const [resumeFile, setResumeFile] = useState(null);
  const [resumeText, setResumeText] = useState("");
  const [jobDescription, setJobDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [result, setResult] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setResult(null);

    if (!jobDescription.trim()) {
      setError("Please paste the target job description.");
      return;
    }
    if (resumeMode === "file" && !resumeFile) {
      setError("Please choose a PDF resume to upload.");
      return;
    }
    if (resumeMode === "text" && !resumeText.trim()) {
      setError("Please paste your resume text.");
      return;
    }

    setLoading(true);
    try {
      let finalResumeText = resumeText;
      if (resumeMode === "file") {
        const uploadResult = await uploadResumeFile(resumeFile);
        finalResumeText = uploadResult.resumeText;
      }

      const analysis = await analyze(finalResumeText, jobDescription);
      setResult(analysis);
    } catch (err) {
      setError(err.message || "Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app">
      <header>
        <h1>Checkmate.AI</h1>
        <p className="tagline">Resume-to-Interview Question Generator</p>
      </header>

      <form onSubmit={handleSubmit} className="analyze-form">
        <div className="form-grid">
          <div className="field">
            <label>Resume</label>
            <div className="mode-toggle">
              <button
                type="button"
                className={resumeMode === "file" ? "active" : ""}
                onClick={() => setResumeMode("file")}
              >
                Upload PDF
              </button>
              <button
                type="button"
                className={resumeMode === "text" ? "active" : ""}
                onClick={() => setResumeMode("text")}
              >
                Paste text
              </button>
            </div>

            {resumeMode === "file" ? (
              <input
                type="file"
                accept="application/pdf"
                onChange={(e) => setResumeFile(e.target.files[0] ?? null)}
              />
            ) : (
              <textarea
                rows={10}
                placeholder="Paste your resume text here..."
                value={resumeText}
                onChange={(e) => setResumeText(e.target.value)}
              />
            )}
          </div>

          <div className="field">
            <label htmlFor="jd">Job Description</label>
            <textarea
              id="jd"
              rows={10}
              placeholder="Paste the target job description here..."
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
            />
          </div>
        </div>

        <div className="submit-row">
          <button type="submit" disabled={loading} className="submit-button">
            {loading ? "Analyzing..." : "Generate Interview Questions"}
          </button>
        </div>
      </form>

      {error && <div className="error-banner">{error}</div>}

      {result && <ResultsPanel result={result} />}
    </div>
  );
}

export default App;
