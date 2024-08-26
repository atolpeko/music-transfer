import './HomePage.css';

const HomePage = ({ onStartClick }) => {
  return (
    <div className="container center-container align-items-center justify-content-center">
      <div className="row justify-content-center section">
        <h1 className="home-heading">
          Transfer Your Library Between Music Streaming Services For Free
        </h1>
      </div>
      <div className="row justify-content-center">
        <img src={`${process.env.PUBLIC_URL}/assets/spotify_icon.svg`}
             alt="Spotify"
             className="home-img" />
        <img src={`${process.env.PUBLIC_URL}/assets/apple_icon.svg`}
             alt="Apple Music"
             className="home-img" />
        <img src={`${process.env.PUBLIC_URL}/assets/yt_icon.svg`}
             alt="YouTube Music"
             className="home-img" />   
        <img src={`${process.env.PUBLIC_URL}/assets/yandex_icon.svg`}
             alt="Yandex Music"
             className="home-img" />         
        <img src={`${process.env.PUBLIC_URL}/assets/vk_icon.svg`}
             alt="VK Music"
             className="home-img" />
      </div>       
      <div className="row justify-content-center section">
        <h5 className="home-text">
          No purchases or subscribtions. No registration.
          <p/>
          Watch a quick advertisement and transfer your music for free
        </h5>
      </div>
      <div className="row justify-content-center">
        <button className="home-button" 
                onClick={onStartClick}>
          Let's start
        </button>
      </div>
    </div>
  );
}
  
export default HomePage;
  