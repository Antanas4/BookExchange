import axios from "axios";

const LOGIN_URL = '/api/auth';

export const login = async (loginRequestDto) => {
    try {
        const response = await axios.post(`${LOGIN_URL}/login`, loginRequestDto);
        return response.data;
    } catch (error) {
        console.log(error.response);
    }
};