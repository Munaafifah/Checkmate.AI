# Checkmate.AI

Resume-to-Interview Question Generator. Upload a resume and a target job description; get a computed fit score plus tailored interview questions — technical questions on gap areas, behavioral questions on strengths.

**Status:** Phase 2 (scoring model + LLM).

## Architecture

```
frontend/    React (Vite) — resume/JD form, results + fit score display
backend/     Spring Boot — PDF parsing, ML scoring client, Groq API integration
ml-service/  FastAPI + sentence-transformers — embedding-based fit scoring
```

The frontend calls the backend. The backend extracts resume text (PDFBox), calls `ml-service` to compute an embedding-similarity fit score between the resume and job description, then passes that score into the Groq prompt so the generated summary and questions are grounded in a real computed score rather than the LLM's own guess. If `ml-service` is unreachable, the backend falls back to Phase 1 behavior (LLM infers gaps from raw text) instead of failing the request.

## Prerequisites

- Java 21+
- Node 20+
- Python 3.10+
- A [Groq API key](https://console.groq.com/keys) (free tier)

## Running locally

All three services run independently; start them in any order (the backend degrades gracefully if `ml-service` isn't up yet).

### ML scoring service

```
cd ml-service
python -m venv .venv
.venv\Scripts\activate        # Windows; use `source .venv/bin/activate` on macOS/Linux
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

Runs on `http://localhost:8000`. See [ml-service/README.md](ml-service/README.md) for how the scoring works.

### Backend

```
cd backend
GROQ_API_KEY=your_key_here ./mvnw spring-boot:run
```

Runs on `http://localhost:8080`. Health check: `GET /api/health`. Points at `ml-service` via `ML_SERVICE_URL` (defaults to `http://localhost:8000`).

### Frontend

```
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173`. The API base URL is read from `VITE_API_BASE_URL` (see `frontend/.env`, defaults to `http://localhost:8080`).

## API

- `POST /api/resume/upload` — multipart form, either a `file` (PDF) or `text` part. Returns extracted `resumeText`.
- `POST /api/analyze` — JSON body `{ resumeText, jobDescription }`. Returns `{ summary, score, questions: [{ type, question }] }` (5 `technical`, 5 `behavioral`). `score` is `null` if `ml-service` was unreachable, otherwise `{ overallScore, yearsOfExperience, matchedRequirements, missingRequirements }`.
