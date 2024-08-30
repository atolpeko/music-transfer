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
  const getJwt = () => sessionStorage.getItem('jwt');
  const setJwt = jwt => sessionStorage.setItem('jwt', jwt);

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

  const handleStartClick = () => window.location = '/transfer';
  const handleHomeClick = () => window.location = '/home'
  
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
    setJwt(null);
    setToken(null);
  }

  const loadFromSource = async () => {
    return {
      tracks: await loadTracks(),
      playlists: await loadPlaylists()
    }
  }

  const loadTracks = () => {
    return withErrorHandling(() => {
      console.log(`Loading tracks from ${services.source.visibleName}`);
      return fetchTracks(services.source.internalName, getJwt());
    });
  }

  const loadPlaylists = () => {
    return withErrorHandling(() => {
      console.log(`Loading playlists from ${services.source.visibleName}`);
      return fetchPlaylists(services.source.internalName, getJwt());
    });
  }

  const handleTransferClick = (tracks, playlists) => {
    console.log('Selected ' + tracks.length + ' tracks ' + 
      'and ' + playlists.length + ' playlists to transfer');
    setToTransfer({
      tracks: tracks,
      playlists: playlists
    })
  }

  const runTracksTransfer = () => {
    return withErrorHandling(() => {
      console.log('Transferring tracks from ' + services.source.visibleName +
      ' to ' + services.target.visibleName);
      return transferTracks(
        services.source.internalName, 
        services.target.internalName,
        toTransfer.tracks,
        getJwt()
      );
    });
  }

  const runPlaylistTransfer = () => {
    return withErrorHandling(async () => {
      console.log('Transferring playlists from ' + services.source.visibleName +
        ' to ' + services.target.visibleName);
      let results = []; 
      let failed = []; 
      for (let i = 0; i < toTransfer.playlists.length; i++) {
        const result = await transferPlaylist(
          services.source.internalName, 
          services.target.internalName,
          toTransfer.playlists[i],
          getJwt()
        );

        results.push(result);
        if (result.failed) {
          failed.push(result.failed.tracks);
        }
      } 

      return {
        transferred: results.length,
        failedToTransfer: failed
      }
    });
  }

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
                  ? <Spinner text='Waiting for authorization...' /> 
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
                      : <TransferPage source={services.source.visibleName}
                                      target={services.target.visibleName} 
                                      transferTracks={runTracksTransfer}
                                      transferPlaylists={runPlaylistTransfer}
                                      onHomeClick={handleHomeClick} />
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
