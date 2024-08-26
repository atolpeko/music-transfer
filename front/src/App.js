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
  
  const [selectedServices, setSelectedServices] = useState(undefined);
  const [selectedTracks, setSelectedTracks] = useState(undefined);
  const [selectedPlaylists, setSelectedPlaylists] = useState(undefined);
  
  const handleStartClick = () => {
    window.location = '/transfer';
  }

  const loadServices = () => {
    console.log('Loading available services');
    return fetchServices();
  }

  const handleServicesSelection = (source, target) => {
    console.log(`Source ${source} and target ${target} selected`);
    setSelectedServices({
      'source': source,
      'target': target
    });
  }

  const loadTracks = () => {
    console.log(`Loading tracks from ${selectedServices['source']}`);
    return fetchTracks();
  }

  const loadPlaylists = () => {
    console.log(`Loading playlists from ${selectedServices['source']}`);
    return fetchPlaylists();
  }

  const handleTransferClick = (tracks, playlists) => {
    console.log(`Selected ${tracks.length} tracks and
       ${playlists.length} playlists to transfer`);
    setSelectedTracks(tracks);
    setSelectedPlaylists(playlists);
  }

  const delay = ms => new Promise(res => setTimeout(res, ms));

  const runTransfer = async () => {
    console.log(`Running transfer from ${selectedServices.source} to ${selectedServices.target}`);
    await delay(3000);
    return {
      'tracksCount' : selectedTracks.length - 3,
      'playlistsCount': selectedPlaylists.length,
      'failed': selectedTracks.slice(0, 3)
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
            (selectedServices == undefined) 
              ? <ServiceSelectionPage loadServices={loadServices}
                                      onDoneClick={handleServicesSelection} />
              : (selectedTracks == undefined && selectedPlaylists == undefined) 
                ? <TransferSetupPage source={selectedServices['source']}
                                     target={selectedServices['target']}
                                     loadTracks={loadTracks}
                                     loadPlaylists={loadPlaylists}
                                     onTransferClick={handleTransferClick} />
                : <TransferPage source={selectedServices['source']}
                                target={selectedServices['target']} 
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
