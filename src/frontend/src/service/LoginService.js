import axios from "axios";

const LOGIN_URL = '/api/auth/login';

export const login = async (loginRequestDto) => {
    try {
        let response = await axios.post(LOGIN_URL, loginRequestDto);
        return response.data;
    } catch (error) {
        console.log("ERROR:", error.message);
    }
};