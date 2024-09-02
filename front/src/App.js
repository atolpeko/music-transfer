import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { fetchServices } from './api/ServiceApi';
import { getAuthUrl, getNextAuthUrl, exchangeToken } from './api/AuthApi';
import { fetchTracks, fetchPlaylists, transferTracks, transferPlaylist } from './api/TransferApi';

import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './components/home/HomePage';
import ContactPage from './components/contact/ContactPage';
import ServiceSelectionPage from './components/service/ServiceSelectionPage';
import TransferSetupPage from './components/transfer/TransferSetupPage';
import TransferPage from './components/transfer/TransferPage';
import Spinner from './components/spinner/Spinner';
import ErrorPage from './components/error/ErrorPage';

const App = () => {
  
  const getParam = param => new URLSearchParams(window.location.search).get(param);

  const getSourceParam = (service, token) => {
    const param = getParam(service);
    if (!param) {
      return null;
    } else if (!token) {
      removeParams('source');
      return null;
    }

    return JSON.parse(atob(param));
  }

  const getTargetParam = () => {
    const param = getParam('target');
    if (!param) {
      return null;
    } else if (!getParam('source')) {
      removeParams('target');
      return null;
    }

    return JSON.parse(atob(param));
  }

  const getJwt = () => sessionStorage.getItem('jwt');
  const setJwt = jwt => sessionStorage.setItem('jwt', jwt);

  const getSourceTracks = () => {
    const saved = sessionStorage.getItem('tracks');
    return saved ? JSON.parse(saved) : null;
  };

  const getSourcePlaylists = () => {
    const saved = sessionStorage.getItem('playlists');
    return saved ? JSON.parse(saved) : null;
  };

  const setSourceTracks = tracks => {
    const json = JSON.stringify(tracks);
    sessionStorage.setItem('tracks', json);
  };

  const setSourcePlaylists = playlists => {
    const json = JSON.stringify(playlists);
    sessionStorage.setItem('playlists', json);
  };

  const clearSourceData = () => {
    sessionStorage.removeItem('tracks');
    sessionStorage.removeItem('playlists');
  }

  const [token, setToken] = useState(getParam('accessToken'));  
  const [authenticating, setAuthenticating] = useState(true);
  const [toTransfer, setToTransfer] = useState({ tracks: null, playlists: null });  
  const [error, setError] = useState(null);
  
  const [services, setServices] = useState({ 
    source: getSourceParam('source', token || getJwt()),
    target: getTargetParam('target')
  });

  useEffect(() => {
    if (token) {
      obtainJwt();
    } else {
      setAuthenticating(false);
    }
  }, []);

  const obtainJwt = () => {
    console.log('Exchanging access token');
    withErrorHandling(() => {
      const service = (services.target) ? services.target : services.source;
      exchangeToken(token).then(res => { 
        setJwt(res.value);
        removeParams('accessToken');
        setToken(null);
        setAuthenticating(false);
        console.log(`Authencticated into ${service.visibleName}`);
      })
      .catch(error => { 
        setServices({ source: null, target: null });
        removeParams('accessToken', service);
        setAuthenticating(false);
        setError(error);
      })
    });  
  }

  const withErrorHandling = async func => {
    try {
      return await func();
    } catch (e) {
      setError(e);
    }
  }

  function removeParams() {
    const url = new URL(window.location.href);
    for (let i = 0; i < arguments.length; i++) {
      url.searchParams.delete(arguments[i]);
    }

    window.history.pushState(null, '', url.toString());
  }

  const handleStartClick = () => { 
    clearSourceData();
    window.location = '/transfer';
  }

  const handleHomeClick = () => {
    clearSourceData();
    window.location = '/home';
  }
  
  const loadServices = () => { 
    return withErrorHandling(() => {
      console.log('Fetching available services');
      return fetchServices();
    });
  }

  const handleSourceSelection = service => {
    console.log(`Source ${service.visibleName} selected`);
    setAuthenticating(true);
    setServices({ source: service });
    setParam('source', btoa(JSON.stringify(service)));
      
    withErrorHandling(async () => {
      const url = await getAuthUrl(service.internalName, window.location.href);
      console.log(`Redirecting to ${service.visibleName} auth URL`);
      window.location = url;
    });
  }

  const setParam = (key, value) => {
    const url = new URL(window.location.href);
    url.searchParams.append(key, value);
    window.history.pushState(null, '', url.toString());
  }

  const handleTargetSelection = service => {  
    console.log(`Target ${service.visibleName} selected`);
    setAuthenticating(true);
    setServices({ source: services.source, target: service });
    setParam('target', btoa(JSON.stringify(service)));
  
    withErrorHandling(async () => {  
      const url = await getNextAuthUrl(service.internalName, window.location.href, getJwt());
      console.log(`Redirecting to ${service.visibleName} auth URL`);
      window.location = url;
    });
  }

  const handleBackClick = () => {
    setServices({ source: null, target: null });
    removeParams('source', 'target');
    setJwt(null);
    setToken(null);
  }

  const loadFromSource = async () => {
    const tracks = getSourceTracks();
    const playlists = getSourcePlaylists();

    return {
      tracks: tracks ? tracks : await loadTracks(),
      playlists: playlists ? playlists :await loadPlaylists()
    }
  }

  const loadTracks = () => {
    return withErrorHandling(async () => {
      console.log(`Loading tracks from ${services.source.visibleName}`);
      const tracks = await fetchTracks(services.source.internalName, getJwt());
      setSourceTracks(tracks);
      return tracks;
    });
  }

  const loadPlaylists = () => {
    return withErrorHandling(async () => {
      console.log(`Loading playlists from ${services.source.visibleName}`);
      const playlists = await fetchPlaylists(services.source.internalName, getJwt());
      setSourcePlaylists(playlists);
      return playlists;
    });
  }

  const handleTransferClick = (tracks, playlists) => {
    console.log(`Selected ${tracks.length} tracks and ${playlists.length}` + 
      ' playlists to transfer');
    setToTransfer({
      tracks: tracks,
      playlists: playlists
    })
  }

  const runTracksTransfer = () => {
    return withServerDownHandling(async () => {
      console.log(`Transferring tracks from  ${services.source.visibleName} ` +
        `to ${services.target.visibleName}`);
      const chunks = splitToChunks(toTransfer.tracks, 25);
      let result = {
        transferred: 0,
        failedToTransfer: []
      }
      for (let i = 0; i < chunks.length; i++) {
        const res = await transferTracks(
          services.source.internalName, 
          services.target.internalName,
          chunks[i],
          getJwt()
        );
        
        result = {
          transferred: result.transferred + res.transferred,
          failedToTransfer: result.failedToTransfer.concat(res.failedToTransfer)
        }
      }

      return result;
    })
  }

  const withServerDownHandling = async func => {
    try {
      return await func();
    } catch (e) {
      if (e == 'Server is unavailable') {
        setError(e);
      } else {
        return Promise.reject(e);
      }
    }
  }

  const splitToChunks = (array, chunkSize) => {
    const chunks = [];
    for (let i = 0; i < array.length; i += chunkSize) {
      const chunk = array.slice(i, i + chunkSize);
      chunks.push(chunk);
    }

    return chunks;
  }

  const runPlaylistTransfer = playlist => {
    return withServerDownHandling(() => {
      console.log(`Transferring playlist ${playlist.id} from `  
        + `${services.source.visibleName} to ${services.target.visibleName}`);
        return transferPlaylist(
        services.source.internalName, 
        services.target.internalName,
        playlist,
        getJwt()
      );
    })
  }

  const handleListenNowClick = () => window.location.href = services.target.homeUrl;

  return (
    <BrowserRouter>
      <div className="d-flex flex-column vh-100">
        <Header homeUrl='/home'
                contactUrl='/contact' />
        <main className="center-container align-items-center justify-content-center">
          <Routes>
            <Route path='/home' element={
              <HomePage onStartClick={handleStartClick} />
            } />
            <Route path='/contact' element={
              <ContactPage />
            } />
            <Route path='/transfer' element={
              (error)
                ? <ErrorPage error={error} onHomeClick={handleHomeClick} />
                : (authenticating)
                  ? <Spinner text='Waiting for Authorization...' /> 
                  : (!services.source || !services.target)
                    ? <ServiceSelectionPage source={services.source}
                                            loadServices={loadServices}
                                            onSourceSelection={handleSourceSelection}
                                            onTargetSelection={handleTargetSelection} 
                                            onBackClick={handleBackClick} />  
                    : (!toTransfer.tracks && !toTransfer.playlists) 
                      ? <TransferSetupPage source={services.source.visibleName}
                                           target={services.target.visibleName}
                                           load={loadFromSource}
                                           onTransferClick={handleTransferClick} />
                      : <TransferPage source={services.source}
                                      target={services.target} 
                                      tracks={toTransfer.tracks}
                                      playlists={toTransfer.playlists}
                                      transferTracks={runTracksTransfer}
                                      transferPlaylist={runPlaylistTransfer}
                                      onHomeClick={handleHomeClick}
                                      onListenNowClick={handleListenNowClick} />
              } />      
            <Route path="*" element={<Navigate to="/home" />}/>
          </Routes>
        </main>
        <Footer />
      </div>
    </BrowserRouter>
  )
}

export default App;
