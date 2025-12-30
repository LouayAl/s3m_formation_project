import axios from "./axios"; // your axios instance
import { useAuth } from "../context/AuthContext";

export const getClientKpis = async (clientId, token) => {
  try {
    const response = await axios.get(`/clients/${clientId}/kpis`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching client KPIs: ", error);
    throw error;
  }
};
