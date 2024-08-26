export const fetchServices = async () => {
  const url = `${window.DOMAIN}${window.SERVICE_API}/services`;
  console.log(`Fetching services from ${url}`);
  return fetch(url, { method: 'GET' })
		.then(response => response.json());
}
