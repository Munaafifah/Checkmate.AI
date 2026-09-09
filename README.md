# Checkmate.AI

Resume-to-Interview Question Generator. Upload a resume and a target job description; get a fit summary plus tailored interview questions — technical questions on gap areas, behavioral questions on strengths.

**Status:** Phase 1 (AI-only MVP).

## Architecture

```
frontend/  React (Vite) — resume/JD form, results display
backend/   Spring Boot — PDF parsing, Groq API integration
```

The frontend calls the backend, which extracts resume text (PDFBox) and calls Groq's chat completions API to generate the fit summary and questions.

## Prerequisites

- Java 21+
- Node 20+
- A [Groq API key](https://console.groq.com/keys) (free tier)

## Running locally

### Backend

```
cd backend
GROQ_API_KEY=your_key_here ./mvnw spring-boot:run
```

Runs on `http://localhost:8080`. Health check: `GET /api/health`.

### Frontend

```
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173`. The API base URL is read from `VITE_API_BASE_URL` (see `frontend/.env`, defaults to `http://localhost:8080`).

## API

- `POST /api/resume/upload` — multipart form, either a `file` (PDF) or `text` part. Returns extracted `resumeText`.
- `POST /api/analyze` — JSON body `{ resumeText, jobDescription }`. Returns `{ summary, questions: [{ type, question }] }` (5 `technical`, 5 `behavioral`).
