const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function handleResponse(response) {
  const data = await response.json().catch(() => null);
  if (!response.ok) {
    const message = data?.error || `Request failed with status ${response.status}`;
    throw new Error(message);
  }
  return data;
}

export async function uploadResumeFile(file) {
  const formData = new FormData();
  formData.append("file", file);
  const response = await fetch(`${API_BASE_URL}/api/resume/upload`, {
    method: "POST",
    body: formData,
  });
  return handleResponse(response);
}

export async function analyze(resumeText, jobDescription) {
  const response = await fetch(`${API_BASE_URL}/api/analyze`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ resumeText, jobDescription }),
  });
  return handleResponse(response);
}
