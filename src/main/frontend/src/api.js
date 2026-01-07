import axios from "axios";
import constants from "./constants";

const api = axios.create({
    baseURL: constants.SERVER_URL
})

api.attachOwnerHeader = (ownerToken) => ({
    headers: {
        [constants.OWNER_TOKEN_HEADER_KEY]: ownerToken
    }
})

export default api;