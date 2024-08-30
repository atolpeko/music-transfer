import { execute, ping } from './ApiUtils';

export const getAuthUrl = (service, redirectUrl) => {
  return getUrl(service, redirectUrl);
}

const getUrl = (service, redirectUrl) => { 
  const redirect = encodeURIComponent(redirectUrl);
  const url = `${window.AUTH_API}/redirect?service=${service}&redirectUrl=${redirect}`;
  return ping(url);
}

export const getNextAuthUrl = async (service, redirectUrl, jwt) => {
  try {
    var url = await getUrl(service, redirectUrl);
    return `${url}&jwt=${jwt}`;
  } catch (e) {
    return Promise.reject(e);
  }
}

export const exchangeToken = async token => {
  const url = `${window.AUTH_API}/token?accessToken=${token}`;
  const request = { method: 'GET' };
  return execute(url, request);
}
