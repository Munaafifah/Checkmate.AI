from fastapi import FastAPI

from app.models import ScoreRequest, ScoreResponse
from app.scoring import score_resume_against_jd

# Internal service only, called server-to-server by the Spring Boot backend
# (never directly from the browser), so no CORS middleware is needed here.
app = FastAPI(title="Checkmate.AI ML Scoring Service")


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/score", response_model=ScoreResponse)
def score(request: ScoreRequest) -> ScoreResponse:
    return score_resume_against_jd(request.resumeText, request.jobDescription)
