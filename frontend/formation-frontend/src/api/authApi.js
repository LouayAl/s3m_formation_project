import api from "./axios";

export async function login() {
  const response = await api.post("/auth/login");
  return response.data.token;
}
