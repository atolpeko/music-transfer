import { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { fetchServices } from './api/ServiceApi';
import { fetchTracks, fetchPlaylists } from './api/TransferApi';

import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './components/home/HomePage';
import ServiceSelectionPage from './components/service/ServiceSelectionPage';
import TransferSetupPage from './components/transfer/TransferSetupPage';
import TransferPage from './components/transfer/TransferPage';

const App = () => {
  
  const [services, setServices] = useState(undefined);
  const [tracks, setTracks] = useState(undefined);
  const [playlists, setPlaylists] = useState(undefined);
  
  const handleStartClick = () => {
    window.location = '/transfer';
  }

  const loadServices = () => {
    console.log('Loading available services');
    return fetchServices();
  }

  const handleServiceSelection = (source, target) => {
    console.log(`Source ${source} and target ${target} selected`);
    setServices({
      'source': source,
      'target': target
    });
  }

  const loadTracks = () => {
    console.log(`Loading tracks from ${services['source']}`);
    return fetchTracks();
  }

  const loadPlaylists = () => {
    console.log(`Loading playlists from ${services['source']}`);
    return fetchPlaylists();
  }

  const handleTransferClick = (tracks, playlists) => {
    console.log(`Selected ${tracks.length} tracks and
       ${playlists.length} playlists to transfer`);
    setTracks(tracks);
    setPlaylists(playlists);
  }

  const delay = ms => new Promise(res => setTimeout(res, ms));

  const runTransfer = async () => {
    console.log(`Running transfer from ${services.source} to ${services.target}`);
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
            (services == undefined) 
              ? <ServiceSelectionPage loadServices={loadServices}
                                      onDoneClick={handleServiceSelection} />
              : (tracks == undefined && playlists == undefined) 
                ? <TransferSetupPage source={services['source']}
                                     target={services['target']}
                                     loadTracks={loadTracks}
                                     loadPlaylists={loadPlaylists}
                                     onTransferClick={handleTransferClick} />
                : <TransferPage source={services['source']}
                                target={services['target']} 
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
