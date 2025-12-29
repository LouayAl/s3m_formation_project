import api from "./axios";

export const fetchAdminSessions = async () => {
  const response = await api.get("/admin/sessions");
  return response.data;
};

export const fetchAdminSessionById = async (id) => {
  const response = await api.get(`/sessions/${id}`);
  return response.data;
};

export const demarrerSession = (id) =>
  api.post(`/admin/sessions/${id}/demarrer`);

export const terminerSession = (id) =>
  api.post(`/admin/sessions/${id}/terminer`);

export const annulerSession = (id) =>
  api.post(`/admin/sessions/${id}/annuler`);
