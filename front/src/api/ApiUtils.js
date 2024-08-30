export const execute = async (url, request) => {
  try {
    const response = await fetch(url, request);
    const json = await response.json();
    if (response.ok) {
      return Promise.resolve(json);
    } else {
      return Promise.reject(json.error);
    }
  } catch(e) {    
    return Promise.reject('Server is unavailable')
  }
}

export const ping = async url => {
  try {
    var request = {
      method: 'HEAD',
      redirect: 'manual'
    }
    return await fetch(url, request).then(() => Promise.resolve(url))
  } catch(e) {    
    return Promise.reject('Server is unavailable')
  }
}
