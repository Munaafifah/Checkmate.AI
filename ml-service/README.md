# Checkmate.AI ML Scoring Service

Computes a resume-to-job-description fit score using sentence embeddings
(`sentence-transformers`, model `all-MiniLM-L6-v2`) instead of asking the LLM
to guess. It is called by the Spring Boot backend (`MlScoringService`) before
the Groq call.

## How it scores

1. Both resume and job description are split into lines (bullets are the
   dominant structure in both, so this is more reliable than sentence
   splitting).
2. Every JD line is treated as a candidate "requirement". Every resume line
   is embedded as candidate "evidence".
3. For each requirement, the cosine similarity against every resume line is
   computed; the best-matching resume line is kept as evidence.
4. Requirements at or above the match threshold (0.45) are "matched",
   the rest are "missing". The overall score is the mean of all
   per-requirement best-similarity scores, scaled to 0-100.
5. Years of experience is a separate regex pass over the resume text
   looking for "N years" / "N yrs" mentions.

This is heuristic (JD headers/boilerplate lines count as "requirements"
too), which is an acceptable tradeoff for an MVP — it needs no maintained
skills taxonomy and generalizes to any JD wording.

## Running locally

```bash
cd ml-service
python -m venv .venv
.venv\Scripts\activate        # Windows
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

First request after startup will be slow (~few seconds) while the
embedding model loads and is cached in memory.

## API

`POST /score`

```json
{ "resumeText": "...", "jobDescription": "..." }
```

```json
{
  "overallScore": 72.4,
  "yearsOfExperience": 3,
  "matchedRequirements": [
    { "requirement": "5+ years of Java experience", "similarity": 0.81, "evidence": "3 years building Java microservices..." }
  ],
  "missingRequirements": [
    { "requirement": "Experience with Kubernetes", "similarity": 0.28, "evidence": "..." }
  ]
}
```

`GET /health` → `{ "status": "ok" }`
