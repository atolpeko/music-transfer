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
  
  const loadServiceParam = service => {
    const param = getParam(service);
    if (param) {
      return JSON.parse(atob(param));
    }
  }

  const [source, setSource] = useState(loadServiceParam('source'));
  const [target, setTarget] = useState(loadServiceParam('target'));
  const [token, setToken] = useState(getParam('accessToken'));  

  const [authenticating, setAuthenticating] = useState(true);
  const [tracks, setTracks] = useState(undefined);
  const [playlists, setPlaylists] = useState(undefined);

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
        const service = (target) ? target : source;
        console.log(`Authencticated into ${service.visibleName}`);
      })
      .catch(error => { 
        showError(error);
        setSource(undefined);
        setTarget(undefined);
        setAuthenticating(false);
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
    setSource(service);
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
    setTarget(service);
    setParam('target', btoa(JSON.stringify(service)));
  
    const url = nextRedirectUrl(service.internalName, window.location.href, getJwt());
    console.log(`Redirecting to ${service.visibleName} auth URL`);
    window.location = url;
  }

  const handleBackClick = () => {
    setSource(undefined);
    setTarget(undefined);
    setJwt(undefined);
    setToken(undefined);
  }

  const loadTracks = async () => {
    console.log(`Loading tracks from ${source.visibleName}`);
    return fetchTracks(source.internalName, getJwt());
  }

  const loadPlaylists = async () => {
    console.log(`Loading playlists from ${source.visibleName}`);
    return fetchPlaylists(source.internalName, getJwt());
  }

  const handleTransferClick = (tracks, playlists) => {
    console.log(`Selected ${tracks.length} tracks and ${playlists.length} playlists to transfer`);
    setTracks(tracks);
    setPlaylists(playlists);
  }

  const delay = ms => new Promise(res => setTimeout(res, ms));

  const runTransfer = async () => {
    console.log(`Running transfer from ${source.visibleName}
       to ${target.visibleName}`);
    await delay(3000);
    return {
      'tracksCount' : tracks.length - 3,
      'playlistsCount': playlists.length,
      'failed': tracks.slice(0, 3)
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
              : (!source || !target)
                ? <ServiceSelectionPage source={source}
                                        loadServices={loadServices}
                                        onSourceSelection={handleSourceSelection}
                                        onTargetSelection={handleTargetSelection} 
                                        onBackClick={handleBackClick}/>  
                : (!tracks && !playlists) 
                  ? <TransferSetupPage source={source.visibleName}
                                       target={target.visibleName}
                                       loadTracks={loadTracks}
                                       loadPlaylists={loadPlaylists}
                                       onTransferClick={handleTransferClick} />
                  : <TransferPage source={source.visibleName}
                                  target={target.visibleName} 
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
