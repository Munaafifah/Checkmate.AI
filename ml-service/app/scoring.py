from functools import lru_cache
from typing import List

from sentence_transformers import SentenceTransformer, util

from app.models import RequirementMatch, ScoreResponse
from app.text_utils import extract_years_of_experience, split_into_lines, split_into_requirements

MODEL_NAME = "all-MiniLM-L6-v2"
MATCH_THRESHOLD = 0.45
MAX_ITEMS_PER_LIST = 8


@lru_cache(maxsize=1)
def get_model() -> SentenceTransformer:
    # Loaded lazily and cached so the first request pays the (one-time) load
    # cost instead of blocking service startup.
    return SentenceTransformer(MODEL_NAME)


def score_resume_against_jd(resume_text: str, job_description: str) -> ScoreResponse:
    requirement_lines = split_into_requirements(job_description)
    resume_lines = split_into_lines(resume_text)
    years_of_experience = extract_years_of_experience(resume_text)

    if not requirement_lines or not resume_lines:
        return ScoreResponse(
            overallScore=0.0,
            yearsOfExperience=years_of_experience,
            matchedRequirements=[],
            missingRequirements=[],
        )

    model = get_model()
    requirement_embeddings = model.encode(
        requirement_lines, convert_to_tensor=True, normalize_embeddings=True
    )
    resume_embeddings = model.encode(
        resume_lines, convert_to_tensor=True, normalize_embeddings=True
    )

    # similarity_matrix[i][j] = how well resume line j supports JD requirement i
    similarity_matrix = util.cos_sim(requirement_embeddings, resume_embeddings)

    matched: List[RequirementMatch] = []
    missing: List[RequirementMatch] = []
    best_scores: List[float] = []

    for i, requirement in enumerate(requirement_lines):
        row = similarity_matrix[i]
        best_idx = int(row.argmax())
        best_score = float(row[best_idx])
        best_scores.append(best_score)

        match = RequirementMatch(
            requirement=requirement,
            similarity=round(best_score, 3),
            evidence=resume_lines[best_idx],
        )
        (matched if best_score >= MATCH_THRESHOLD else missing).append(match)

    overall_score = round((sum(best_scores) / len(best_scores)) * 100, 1)

    matched.sort(key=lambda m: m.similarity, reverse=True)
    missing.sort(key=lambda m: m.similarity)

    return ScoreResponse(
        overallScore=overall_score,
        yearsOfExperience=years_of_experience,
        matchedRequirements=matched[:MAX_ITEMS_PER_LIST],
        missingRequirements=missing[:MAX_ITEMS_PER_LIST],
    )
