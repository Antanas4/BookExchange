import axios from 'axios';

const PUBLICATIONS_URL = '/api/publications';

export const createPublicationService = async (publicationDto, ownerUsername) => {
    try {
        const userCheckResponse = await axios.get(`/api/users/${ownerUsername}`);
        if (userCheckResponse.data) {
            await axios.post(PUBLICATIONS_URL, publicationDto);
        }
    } catch (error) {
        throw new Error;
    }
}

export const getPublicationsService = async (publications) => {
    try {
        const response = await axios.get(PUBLICATIONS_URL);
        publications.value = response.data;
    } catch (error) {
        throw new Error;
    }
};

export const updatePublicationService = async (publication) => {
    try {
        await axios.put(`${PUBLICATIONS_URL}/${publication.id}`, publication);
    } catch (error) {
        throw new Error;
    }
};

export const deletePublicationService = async (publicationId) => {
    try {
        await axios.delete(`${PUBLICATIONS_URL}/${publicationId}`);
    } catch (error) {
        throw new Error;
    }
};
