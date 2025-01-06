import axios from "axios";

export const getCurrentUserRoles = async () => {
    try {
        const response = await axios.get('/api/currentUser/roles');
        return response.data || [];
    } catch (error) {
        console.error("Error fetching user roles:", error);
        return [];
    }
};

export const isLoggedIn = async () => {
    try {
        const response = await axios.get('/api/currentUser');
        return response.status === 200;
    } catch (error) {
        return false;
    }
};

export const login = async (loginRequestDto) => {
    try {
        let response = await axios.post('/api/auth/login', loginRequestDto);
        return response.data;
    } catch (error) {
        console.error(error.message);
    }
};