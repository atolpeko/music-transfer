import { execute } from "./ApiUtils";

export const fetchTracks = (service, authToken) => {
	const url = `${window.TRANSFER_API}/tracks?service=${service}`;
	return fetchAll(url, authToken);
}

export const fetchAll = async (baseUrl, authToken) => {
	let all = [];
	let url = baseUrl;
	while (url) {
		try {
			const request = { 
				method: 'GET',
				headers: {
					Authorization: `Bearer ${authToken}`
				} 
			}
			const response = await execute(url, request)
				.catch(error => { throw error; });

			all = all.concat(response.items);
			url = (response.next) 
				? `${baseUrl}&next=${response.next}`
				: null;
		} catch (e) {
			return Promise.reject(e);
		}
	}

	return all;
}

export const fetchPlaylists = async (service, authToken) => {
	try {
		const url = `${window.TRANSFER_API}/playlists?service=${service}`;
		const playlists = await fetchAll(url, authToken);
		for (let i = 0; i < playlists.length; i++) {
			const playlist = playlists[i];
			playlist.tracks = await fetchPlaylistTracks(service, authToken, playlist.id);
		}
		
		return playlists;	 
	} catch (e) {
		return Promise.reject(e);
	}
}

export const fetchPlaylistTracks = async (service, authToken, playlistId) => {
	const endpoint = `${window.TRANSFER_API}/playlists`;
	const url = `${endpoint}/${playlistId}/tracks?service=${service}`;
	return fetchAll(url, authToken);
}

export const transferTracks = async (source, target, tracks, authToken) => {
  const url = `${window.TRANSFER_API}/tracks?source=${source}&target=${target}`;
  const request = { 
		method: 'POST',
		headers: {
			Authorization: `Bearer ${authToken}`,
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ 
			tracks: tracks 
		})
	}

	return execute(url, request);
}

export const transferPlaylist = async (source, target, playlist, authToken) => {
  const url = `${window.TRANSFER_API}/playlists?source=${source}&target=${target}`;
	const request = { 
		method: 'POST',
		headers: {
			Authorization: `Bearer ${authToken}`,
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(playlist)
	}

  return execute(url, request);
}

