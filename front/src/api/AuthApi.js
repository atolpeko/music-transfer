export const redirectUrl = (service, redirectUrl) => {
  var url = `${window.DOMAIN}${window.AUTH_API}/redirect`;
  var redirect = encodeURIComponent(redirectUrl);
  return `${url}?service=${service}&redirectUrl=${redirect}`;
}

export const nextRedirectUrl = (service, redirectUrl, jwt) => {
  var url = `${window.DOMAIN}${window.AUTH_API}/redirect`;
  var redirect = encodeURIComponent(redirectUrl);
  return `${url}?service=${service}&jwt=${jwt}&redirectUrl=${redirect}`;
}

export const exchangeToken = async token => {
  const url = `${window.DOMAIN}${window.AUTH_API}/token?accessToken=${token}`;
  console.log(`Exchanging access token at ${url}`);
  return fetch(url, { method: 'GET' })
		.then(response => response.json());
}
