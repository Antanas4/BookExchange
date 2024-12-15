import axios from "axios";

const COMMENT_URL = '/api/comment';

export const getPublicationComments = async (publication) =>{
    const response = await axios.get(`${COMMENT_URL}/getPublicationComments/${publication.id}`);
    return response.data;
}