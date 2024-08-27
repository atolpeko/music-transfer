export const fetchServices = async () => {
  const url = `${window.DOMAIN}${window.SERVICE_API}/services`;
  return fetch(url, { method: 'GET' })
		.then(response => response.json());
}
