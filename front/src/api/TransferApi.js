export const fetchTracks = async (service, authToken) => {
	const url = `${window.DOMAIN}${window.TRANSFER_API}/tracks?service=${service}`;
	return fetchAll(url, authToken, 'tracks');
}

export const fetchAll = async (baseUrl, authToken, entity) => {
	let all = [];
	let url = baseUrl;
	while (url) {
		const response = await fetch(url, { 
			method: 'GET',
			headers: {Authorization: `Bearer ${authToken}`} 
		}).then(res => res.json());

		all = all.concat(response.items);
		url = (response.next) 
			? `${baseUrl}&next=${response.next}`
			: null;
	}

	return all;
}

export const fetchPlaylists = async (service, authToken) => {
	const url = `${window.DOMAIN}${window.TRANSFER_API}/playlists?service=${service}`;
	const playlists = await fetchAll(url, authToken, 'playlists');
	playlists.forEach(playlist => {
		fetchPlaylistTracks(service, authToken, playlist.id)
			.then(tracks => playlist.tracks = tracks);
	});
	
	return playlists;	 
}

export const fetchPlaylistTracks = async (service, authToken, playlistId) => {
	const endpoint = `${window.DOMAIN}${window.TRANSFER_API}/playlists`;
	const url = `${endpoint}/${playlistId}/tracks?service=${service}`;
	return fetchAll(url, authToken, 'playlist tracks');
}
