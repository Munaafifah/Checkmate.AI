from typing import List, Optional

from pydantic import BaseModel


class ScoreRequest(BaseModel):
    resumeText: str
    jobDescription: str


class RequirementMatch(BaseModel):
    requirement: str
    similarity: float
    evidence: Optional[str] = None


class ScoreResponse(BaseModel):
    overallScore: float
    yearsOfExperience: Optional[int] = None
    matchedRequirements: List[RequirementMatch]
    missingRequirements: List[RequirementMatch]
