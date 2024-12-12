import axios from 'axios';

const PUBLICATIONS_URL = '/api/publications';

export const createPublication = async (publicationDto, ownerUsername) => {
    try {
        const userCheckResponse = await axios.get(`/api/${ownerUsername}`);
        if (userCheckResponse.data) {
            await axios.post(PUBLICATIONS_URL, publicationDto);
        }
    } catch (error) {
        console.error("Error in creating publication:", error);
        throw new Error;
    }
}

export const getPublications = async (publications) => {
    try {
        const response = await axios.get(PUBLICATIONS_URL);
        publications.value = response.data;
    } catch (error) {
        throw new Error;
    }
};

export const updatePublication = async (publication) => {
    try {
        await axios.put(`${PUBLICATIONS_URL}/${publication.id}`, publication);
    } catch (error) {
        throw new Error;
    }
};

export const deletePublication = async (publicationId) => {
    try {
        await axios.delete(`${PUBLICATIONS_URL}/${publicationId}`);
    } catch (error) {
        throw new Error;
    }
};

export const getPublicationsShop = async () => {
    try {
        const response = await axios.get(`${PUBLICATIONS_URL}/shop`);
        console.log(response.data);
        return response.data;
    } catch (error) {
        console.error("Error fetching publications: ", error.message);
    }
};

export const buyPublication = async (publication) => {
    try {
       await axios.put(`${PUBLICATIONS_URL}/shop/buy/${publication.id}`);
    } catch (error) {
        console.error("Error fetching publications: ", error.message);
        throw new Error("Error fetching publications: " + error.message);
    }
};

export const borrowPublication = async (publication) => {
    try {
        await axios.put(`${PUBLICATIONS_URL}/shop/borrow/${publication.id}`);
    } catch (error) {
        console.error("Error borrowing publications: ", error.message);
        throw new Error("Error borrowing publications: " + error.message);
    }
};

export const returnPublication = async (publicationId) => {
    try {
        await axios.put(`${PUBLICATIONS_URL}/shop/return/${publicationId}`);
    } catch (error) {
        console.error("Error returning publications: ", error.message);
        throw new Error("Error returning publications: " + error.message);
    }
};

export const getMyPublications = async (myPublications) =>{
    try{
        const response = await axios.get(`${PUBLICATIONS_URL}/myPublications`);
        myPublications.value = response.data;
    } catch (error){
        console.error(error.message);
    }
}
export const getMyBoughtPublications = async (boughtPublications) =>{
    try{
        const response =  await axios.get(`${PUBLICATIONS_URL}/myPublications/bought`);
        boughtPublications.value = response.data;
    } catch (error){
        console.error(error.message);
    }
}

export const getMyBorrowedPublications = async () =>{
    try{
        return await axios.get(`${PUBLICATIONS_URL}/myPublications/borrowed`);
    } catch (error){
        console.error(error.message);
    }
}