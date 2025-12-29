import axios from 'axios';


export const whoAmI = async () => {
    try {
        const response = await axios.get("/test/whoami");
        return response.data;
    } catch (error) {
        console.error('Error calling /api/test/whoami:', error);
        throw error;
    }
};