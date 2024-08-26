export const fetchServices = async () => {
  let url = window.DOMAIN + window.SERVICE_API + '/services';
  console.log(`Fetching services from ${url}`);
  return fetch(url, { method: 'GET' })
		.then(response => response.json());
}
