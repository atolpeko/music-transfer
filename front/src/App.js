import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/header/Header';
import Footer from './components/footer/Footer';
import HomePage from './components/home/HomePage';
import TransferPage from './components/transfer/TransferPage';

function App() {
  
  const handleStartClick = () => {
    window.location = '/transfer';
  }

  const loadServices = () => {
    console.log('Loading available services')
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
                   element={<TransferPage onLoad={loadServices} />} />        
            <Route path="*" element={<Navigate to="/home" />}/>
          </Routes>
        </main>
        <Footer />
      </div>
    </BrowserRouter>
  )
}

export default App;
