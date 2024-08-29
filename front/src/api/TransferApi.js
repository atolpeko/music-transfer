export const fetchTracks = async (service, authToken) => {
	const url = `${window.DOMAIN}${window.TRANSFER_API}/tracks?service=${service}`;
	return fetchAll(url, authToken);
}

export const fetchAll = async (baseUrl, authToken) => {
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
	const playlists = await fetchAll(url, authToken);
	for (let i = 0; i < playlists.length; i++) {
		const playlist = playlists[i];
		playlist.tracks = await fetchPlaylistTracks(service, authToken, playlist.id);
	}
	
	return playlists;	 
}

export const fetchPlaylistTracks = async (service, authToken, playlistId) => {
	const endpoint = `${window.DOMAIN}${window.TRANSFER_API}/playlists`;
	const url = `${endpoint}/${playlistId}/tracks?service=${service}`;
	return fetchAll(url, authToken);
}

export const transferTracks = async (source, target, tracks, authToken) => {
  const url = `${window.DOMAIN}${window.TRANSFER_API}/tracks?source=${source}&target=${target}`;
  return fetch(url, { 
		method: 'POST',
		headers: {
			Authorization: `Bearer ${authToken}`,
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ tracks: tracks })
	})
	.then(response => response.json());
}

export const transferPlaylist = async (source, target, playlist, authToken) => {
  const url = `${window.DOMAIN}${window.TRANSFER_API}/playlists?source=${source}&target=${target}`;
  return fetch(url, { 
		method: 'POST',
		headers: {
			Authorization: `Bearer ${authToken}`,
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(playlist)
	})
	.then(response => response.json());
}

