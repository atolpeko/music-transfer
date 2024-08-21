import { useEffect } from 'react';
import './HomePage.css';

const TransferPage = ({ onLoad }) => {

  useEffect(() => {
    onLoad();
  }, [])

  return (
    <div className="container center-container align-items-center justify-content-center">
      <div className="row justify-content-center section">
        <h1 className="heading">
          Transfer Your Library Between Music Streaming Services For Free
        </h1>
      </div>
      <div className="row justify-content-center service-images">
        <img src={`${process.env.PUBLIC_URL}/spotify_icon.svg`}
             alt="Spotify"
             className="service-img" />
        <img src={`${process.env.PUBLIC_URL}/apple_icon.svg`}
             alt="Apple Music"
             className="service-img" />
        <img src={`${process.env.PUBLIC_URL}/yt_icon.svg`}
             alt="YouTube Music"
             className="service-img" />   
        <img src={`${process.env.PUBLIC_URL}/yandex_icon.svg`}
             alt="Yandex Music"
             className="service-img" />         
        <img src={`${process.env.PUBLIC_URL}/vk_icon.svg`}
             alt="VK Music"
             className="service-img" />
      </div>       
      <div className="row justify-content-center section">
        <h5 className="text">
          No purchases or subscribtions. No registration.
          <p/>
          Watch a quick advertisement and transfer your music for free
        </h5>
       </div>
       <div className="row justify-content-center">
          <button className="start-button" 
                  onClick={onStartClick}>
            Let's start
          </button>
			</div>
    </div>
  );
}
  
export default TransferPage;
  