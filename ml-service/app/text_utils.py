import re
from typing import List, Optional

_BULLET_PREFIX = re.compile(r"^[\s\-\*•▪●·]+")
_YEARS_PATTERN = re.compile(r"(\d+)\+?\s*(?:years?|yrs?)\b", re.IGNORECASE)
_SENTENCE_SPLIT = re.compile(r"(?<=[.;])\s+")

MIN_LINE_LENGTH = 8
MAX_REQUIREMENT_LENGTH = 160


def split_into_lines(text: str) -> List[str]:
    """Split resume/JD text into candidate requirement or evidence lines.

    Splits on newlines rather than sentences because resumes and job
    descriptions are dominated by bullet points, not prose sentences.
    """
    lines: List[str] = []
    for raw_line in re.split(r"[\r\n]+", text):
        cleaned = _BULLET_PREFIX.sub("", raw_line).strip()
        if len(cleaned) >= MIN_LINE_LENGTH:
            lines.append(cleaned)
    return lines


def split_into_requirements(text: str) -> List[str]:
    """Like split_into_lines, but further breaks any line longer than
    MAX_REQUIREMENT_LENGTH into sentences, so a JD that mixes prose
    paragraphs in with bullets doesn't produce paragraph-length "requirement"
    chips in the UI.
    """
    requirements: List[str] = []
    for line in split_into_lines(text):
        if len(line) <= MAX_REQUIREMENT_LENGTH:
            requirements.append(line)
            continue
        for sentence in _SENTENCE_SPLIT.split(line):
            sentence = sentence.strip()
            if len(sentence) < MIN_LINE_LENGTH:
                continue
            if len(sentence) > MAX_REQUIREMENT_LENGTH:
                sentence = sentence[:MAX_REQUIREMENT_LENGTH].rstrip() + "..."
            requirements.append(sentence)
    return requirements


def extract_years_of_experience(text: str) -> Optional[int]:
    """Best-effort extraction of the highest "N years" mention in the resume."""
    matches = [int(m.group(1)) for m in _YEARS_PATTERN.finditer(text)]
    return max(matches) if matches else None
