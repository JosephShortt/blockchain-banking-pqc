import axios from "axios";

const api = axios.create();

export function getBankApi(bankApiUrl) {
  return axios.create({
    baseURL: bankApiUrl
  });
}

export default api;
