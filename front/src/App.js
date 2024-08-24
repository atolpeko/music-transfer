import { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './components/home/HomePage';
import ServiceSelectionPage from './components/service/ServicePage';

function App() {
  
  const [sourceServices, setSourceServices] = useState([]);
  const [targetServices, setTargetServices] = useState([]);

  const handleStartClick = () => {
    window.location = '/transfer';
  }

  const loadServices = () => {
    console.log('Loading available services');
    setSourceServices([
      {
        "visibleName": "Spotify",
        "internalName": "SPOTIFY",
        "available": "true",
        "logoUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Spotify_icon.svg/1024px-Spotify_icon.svg.png?20220821125323"
      },
      {
        "visibleName": "YouTube Music",
        "internalName": "YT_MUSIC",
        "available": "true",
        "logoUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Youtube_Music_icon.svg/1024px-Youtube_Music_icon.svg.png?20230802004652"
      }
    ]);
    setTargetServices([
      {
        "visibleName": "Spotify",
        "internalName": "SPOTIFY",
        "available": "true",
        "logoUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Spotify_icon.svg/1024px-Spotify_icon.svg.png?20220821125323"
      },
      {
        "visibleName": "YouTube Music",
        "internalName": "YT_MUSIC",
        "available": "true",
        "logoUrl": "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Youtube_Music_icon.svg/1024px-Youtube_Music_icon.svg.png?20230802004652"
      }
    ]);
  }

  const handleServicesSelected = (source, target) => {
    console.log(`Source ${source} and target ${target} selected`);
  }

  return (
    <BrowserRouter>
      <div className="d-flex flex-column vh-100">
        <Header homeUrl='/home'
                aboutUrl='/about'
                contactUrl='/contact'
                supportUrl='/support' />
        <main className="container center-container align-items-center justify-content-center">
          <Routes>
            <Route path='/home' 
                   element={<HomePage onStartClick={handleStartClick} />} />
            <Route path='/transfer' 
                   element={<ServiceSelectionPage onLoad={loadServices} 
                                                  onDoneClick={handleServicesSelected}
                                                  sourceServices={sourceServices}
                                                  targetServices={targetServices} />} />      
            <Route path="*" element={<Navigate to="/home" />}/>
          </Routes>
        </main>
        <Footer />
      </div>
    </BrowserRouter>
  )
}

export default App;
