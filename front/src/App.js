import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { fetchServices } from './api/ServiceApi';
import { nextRedirectUrl, redirectUrl, exchangeToken } from './api/AuthApi';
import { fetchTracks, fetchPlaylists } from './api/TransferApi';

import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './components/home/HomePage';
import ServiceSelectionPage from './components/service/ServiceSelectionPage';
import TransferSetupPage from './components/transfer/TransferSetupPage';
import TransferPage from './components/transfer/TransferPage';
import Spinner from './components/spinner/Spinner';

const App = () => {
  
  const getParam = param => new URLSearchParams(window.location.search).get(param);
  
  const getServiceParam = service => {
    const param = getParam(service);
    if (param) {
      return JSON.parse(atob(param));
    }
  }

  const [token, setToken] = useState(getParam('accessToken'));  

  const [services, setServices] = useState({ 
    source: getServiceParam('source'),
    target: getServiceParam('target')
  });

  const [authenticating, setAuthenticating] = useState(true);
  const [toTransfer, setToTransfer] = useState({ tracks: undefined, playlists: undefined });

  const getJwt = () => sessionStorage.getItem('jwt');
  const setJwt = jwt => sessionStorage.setItem('jwt', jwt);

  useEffect(() => {
    if (token) {
      obtainJwt();
    } else {
      setAuthenticating(false);
    }
  }, []);

  const obtainJwt = async () => {
    console.log('Exchanging access token');
    await exchangeToken(token)
      .then(res => {
        setJwt(res.value);
        removeTokenParam();
        setToken(null);
        setAuthenticating(false);
        const service = (services.target) ? services.target : services.source;
        console.log(`Authencticated into ${service.visibleName}`);
      })
      .catch(error => { 
        setServices({ source: undefined, target: undefined });
        setAuthenticating(false);
        showError(error);
      });  
  }

  const removeTokenParam = () => {
    const url = new URL(window.location.href);
    url.searchParams.delete('accessToken');
    window.history.pushState(null, '', url.toString());
  }

  const showError = error => {
    console.error(error);
    alert(error);
  }

  const handleStartClick = () => {
    window.location = '/transfer';
  }

  const loadServices = () => {
    console.log('Fetching available services');
    return fetchServices();
  }

  const handleSourceSelection = service => {
    console.log(`Source ${service.visibleName} selected`);
    setAuthenticating(true);
    setServices({ source: service });
    setParam('source', btoa(JSON.stringify(service)));
    
    const url = redirectUrl(service.internalName, window.location.href);
    console.log(`Redirecting to ${service.visibleName} auth URL`);
    window.location = url;
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
  
    const url = nextRedirectUrl(service.internalName, window.location.href, getJwt());
    console.log(`Redirecting to ${service.visibleName} auth URL`);
    window.location = url;
  }

  const handleBackClick = () => {
    setServices({ source: undefined, target: undefined });
    setJwt(undefined);
    setToken(undefined);
  }

  const loadFromSource = async () => {
    return {
      tracks: await loadTracks(),
      playlists: await loadPlaylists()
    }
  }

  const loadTracks = async () => {
    console.log(`Loading tracks from ${services.source.visibleName}`);
    return fetchTracks(services.source.internalName, getJwt());
  }

  const loadPlaylists = async () => {
    console.log(`Loading playlists from ${services.source.visibleName}`);
    return fetchPlaylists(services.source.internalName, getJwt());
  }

  const handleTransferClick = (tracks, playlists) => {
    console.log(`Selected ${tracks.length} tracks and ${playlists.length} playlists to transfer`);
    setToTransfer({
      tracks: tracks,
      playlists: playlists
    })
  }

  const delay = ms => new Promise(res => setTimeout(res, ms));

  const runTransfer = async () => {
    console.log(`Running transfer from ${services.source.visibleName}
       to ${services.target.visibleName}`);
    await delay(3000);
    return {
      'tracksCount' : toTransfer.tracks.length - 3,
      'playlistsCount': toTransfer.playlists.length,
      'failed': toTransfer.tracks.slice(0, 3)
    }
  }

  return (  
   <BrowserRouter>
    <div className="d-flex flex-column vh-100">
      <Header homeUrl='/home'
              aboutUrl='/about'
              contactUrl='/contact'
              supportUrl='/support' />
      <main className="center-container align-items-center justify-content-center">
        <Routes>
          <Route path='/home' element={
            <HomePage onStartClick={handleStartClick} />
          } />
          <Route path='/transfer' element={
            (authenticating)
              ? <Spinner text='Waiting for authorization...'/> 
              : (!services.source || !services.target)
                ? <ServiceSelectionPage source={services.source}
                                        loadServices={loadServices}
                                        onSourceSelection={handleSourceSelection}
                                        onTargetSelection={handleTargetSelection} 
                                        onBackClick={handleBackClick}/>  
                : (!toTransfer.tracks && !toTransfer.playlists) 
                  ? <TransferSetupPage source={services.source.visibleName}
                                       target={services.target.visibleName}
                                       load={loadFromSource}
                                       onTransferClick={handleTransferClick} />
                  : <TransferPage source={services.source.visibleName}
                                  target={services.target.visibleName} 
                                  run={runTransfer}/>
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
