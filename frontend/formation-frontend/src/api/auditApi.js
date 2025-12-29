import api from "./axios";

export const fetchSessionAudit = async (sessionId) => {
  const response = await api.get(`/admin/sessions/${sessionId}/audit`);
  return response.data;
};
