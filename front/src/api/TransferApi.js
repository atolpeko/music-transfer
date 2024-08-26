export const fetchTracks = async (service, authToken) => {
	const url = `${window.DOMAIN}${window.TRANSFER_API}/tracks?service=${service}`;
	return fetchAll(url, authToken, 'tracks');
}

export const fetchAll = async (baseUrl, authToken, entity) => {
	let all = [];
	let url = baseUrl;
	while (url) {
		console.log(`Fetching ${entity} from ${url}`);
		const response = await fetch(url, { 
			method: 'GET',
			headers: {Authorization: `Bearer ${authToken}`} 
		}).then(res => res.json());

		all = all.concat(response.items);
		url = (response.next) 
			? `${baseUrl}&next=${response.next}`
			: null;
	}

	console.log(all);
	return all;
}

export const fetchPlaylists = async (service, authToken) => {
	const url = `${window.DOMAIN}${window.TRANSFER_API}/playlists?service=${service}`;
	const playlists = await fetchAll(url, authToken, 'playlists');
	playlists.forEach(playlist =>
		 playlist.tracks = fetchPlaylistTracks(service, authToken, playlist.id));

}

export const fetchPlaylistTracks = async (service, authToken, playlistId) => {
	const endpoint = `${window.DOMAIN}${window.TRANSFER_API}/tracks`;
	const url = `${endpoint}?service=${service}&playlistId=${playlistId}`;
	return fetchAll(url, authToken, 'playlist tracks');
}
