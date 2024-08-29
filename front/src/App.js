import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { fetchServices } from './api/ServiceApi';
import { nextRedirectUrl, redirectUrl, exchangeToken } from './api/AuthApi';
import { fetchTracks, fetchPlaylists, transferTracks, transferPlaylist } from './api/TransferApi';

import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './components/home/HomePage';
import ContactPage from './components/contact/ContactPage';
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
    console.log('Selected ' + tracks.length + ' tracks ' + 
      'and ' + playlists.length + ' playlists to transfer');
    setToTransfer({
      tracks: tracks,
      playlists: playlists
    })
  }

  const runTracksTransfer = async () => {
    console.log('Transferring tracks from ' + services.source.visibleName +
     ' to ' + services.target.visibleName);
    return transferTracks(
      services.source.internalName, 
      services.target.internalName,
      toTransfer.tracks,
      getJwt()
    );
  }

  const runPlaylistTransfer = async () => {
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
  }

  const handleHomeClick = () => {
    window.location = '/home';
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
            (authenticating)
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
