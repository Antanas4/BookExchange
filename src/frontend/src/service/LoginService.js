import axios from "axios";

const LOGIN_URL = '/api/auth';

export const login = async (loginRequestDto) => {
    try{
        await axios.post(`${LOGIN_URL}/login`, loginRequestDto);
    } catch (error) {
        console.log(error.response);
        console.log(loginRequestDto);
    }
}