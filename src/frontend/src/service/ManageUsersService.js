import axios from "axios";

const USERS_URL = 'api/admin/users';

export const createUser = async (userDto) => {
    try {
        const response = await axios.post(USERS_URL, userDto);
        console.log("User created", response.data);
        return response.data;
    } catch (error) {
        console.log(error.message);
        throw new Error('Username is already taken');
    }
}

export const getUsers = async () => {
    try {
        const response = await axios.get(USERS_URL);
        return response.data;
    } catch (error) {
        throw new Error("Error fetching user data.");
    }
}
export const updateUser = async (username, userDto) => {
    try {
        const userCheckResponse = await axios.get(`api/${username}`);
        if (userCheckResponse.data) {
            const response = await axios.put(`${USERS_URL}/${username}`, userDto);
            return response.data;
        }
    } catch (error) {
        throw new Error(`User ${username} does not exist.`);
    }
}

export const deleteUser = async (username) => {
    try {
        await axios.delete(`${USERS_URL}/${username}`);
    } catch (error) {
        throw new Error("An error occurred while deleting the user.");
    }
}