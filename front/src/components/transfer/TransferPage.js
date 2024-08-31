import { useState, useEffect } from 'react';
import SelectableCard from '../SelectableCard';
import Spinner from '../spinner/Spinner';
import Modal from '../modal/Modal';

import './TransferPage.css';

const TransferPage = ({ source, target, tracks, playlists,
   transferTracks, transferPlaylist, onHomeClick }) => {

  const [loading, setLoading] = useState({
    tracks: true,
    playlists: false
  });

  const [transferred, setTransferred] = useState({
    tracks: 0,
    playlists: 0
  });

  const [failed, setFailed] = useState({
    tracks: [],
    playlists: []
  });

  const [modal, setModal] = useState({ 
    show: false, 
    playlist: null 
  });

  useEffect(() => {
    const call = async () => {
      await runTrackTransfer();
      await runPlaylistTransfer();
      console.log('Transfer complete');
      console.log(transferred.tracks);
    }

    call();
  }, []);

  const runTrackTransfer = () => {
    return transferTracks().then(res => {
      console.log(res.transferred);
      setTransferred({ 
        ...transferred,
        tracks: res.transferred 
      });
      setFailed(prev => ({
        ...prev,
        tracks: failed.tracks.concat(res.failedToTransfer)
      }));
      setLoading({ tracks: false, playlists: true });
      return res;
    }).catch(() => {
      setFailed(prev => ({
        ...prev,
        tracks: failed.tracks.concat(tracks)
      }));
    });
  }

  const runPlaylistTransfer = async () => {
    setFailed({ tracks: failed.tracks, playlists: [] });
    let count = 0;
    for (let i = 0; i < playlists.length; i++) {
      const playlist = playlists[i];
      try {
        const res = await transferPlaylist(playlist);
        if (res.transferred > 0) {
          count += 1;
        }
        if (res.failedToTransfer) {
          setFailed(prev => ({
            ...prev,
            playlists: failed.playlists.concat(playlist) 
          }));
        }
      } catch (e) {
        setFailed(prev => ({
          ...prev,
          playlists: failed.playlists.concat(playlist)
        }));
      }
    }

    setTransferred(prev => ({ 
      ...prev,
      playlists: count 
    }));
    setLoading({ tracks: false, playlists: false });
  }

  useEffect(() => {
    window.onclick = event => {
      const modal = document.getElementById('modal'); 
      if (modal && event.target.id == modal.id)
        setModal({ show: false, entity: null });
    };
  }, []);

  const toggleModal = playlist => setModal({ 
    show: !modal.show, 
    playlist: playlist 
  });

  const handlePlaylistSelection = playlist => {
    if (playlist.tracks.length > 0) {
      toggleModal(playlist);
    }
  }

  const renderTracksLoading = () => {
    return (
      <div>
        <div className="row justify-content-center section">
          <h1 className="page-title">
            Transferring the Library from {source} to {target}     
          </h1>
        </div>
        <div className="row align-items-center justify-content-center section">
          { getFirst3(tracks).map((track, i) => (
              <div className="col col-md-3" key={i}>
                <SelectableCard id={`loading-track-${i}`}
                                text={track.name}
                                imageUrl={track.imageUrl} 
                                disabled={true} />
              </div>  
            )) 
          } 
        </div>
        <Spinner text={`Transferring ${tracks.length} Tracks`}/> 
      </div>
    );
  }

  const getFirst3 = array => {
    const end = (array.length < 3) ? array.length : 3;
    return array.slice(0, end);
  }

  const renderPlaylistLoading = () => {
    const totalTracks = playlists.reduce((total, curr) => total + curr.tracks.length, 0);
    return (
      <div>
        <div className="row justify-content-center section">
          <h1 className="page-title">
            Transferring the Library from {source} to {target}     
          </h1>
        </div>
        <div className="row align-items-center justify-content-center section">
          { getFirst3(playlists).map((playlist, i) => (
              <div className="col col-md-3" key={i}>
                <SelectableCard id={`loading-playlist-${i}`}
                                text={playlist.name}
                                imageUrl={playlist.imageUrl} 
                                disabled={true} />
              </div>  
            )) 
          } 
        </div>
        <Spinner text={`Transferring ${playlists.length} Playlists ` +
          `with ${totalTracks} Tracks`}/> 
      </div>
    );
  }

  const renderFailedTracks = () => {
    return modal.playlist.tracks.map((track, i) => (
      <div className="col col-md-3" key={i}>
        <SelectableCard id={`failed-track-${i}`}
                        text={track.name}
                        imageUrl={track.imageUrl}
                        disabled={true}/>
      </div>  
    ));
  }

  const renderResult = () => {
    const uniqueTracks = [...new Set(failed.tracks)];
    return (
      <div>
        <div className="row align-items-center justify-content-center section">
          <h1 className="page-title">
            {source} to {target} Transfer Result
          </h1>
        </div>
        <div className="row align-items-center justify-content-center section">
          <h3>Transferred {transferred.tracks} Tracks and {transferred.playlists} Playlists</h3>
        </div>  

        { failed.playlists.length > 0  &&
          <div>
            <div className="row align-items-center justify-content-center section">
              <h4 className="transfer-fail-text">
                Playlists Failed to Transfer. Click to See Details
              </h4>
            </div>
            <div className="row align-items-center justify-content-center">
              { failed.playlists.map((playlist, i) => (
                <div className="col col-md-3" key={i}>
                  <SelectableCard id={`failed-playlist ${i}`}
                                  text={playlist.name}
                                  imageUrl={playlist.imageUrl}
                                  onSelect={() => handlePlaylistSelection(playlist)} />
                </div>  
                )) 
              } 
            </div> 
          </div>  
        }
        { modal.show && 
          <Modal title='Tracks Failed to Transfer' 
                 content={renderFailedTracks()} 
                 onClose={() => setModal({ show: false })}/> 
        }  
        { uniqueTracks.length > 0  &&
          <div>
            <div className="row justify-content-center section">
              <h4 className="transfer-fail-text">Tracks Failed to Transfer</h4>
            </div>
            <div className="row align-items-center justify-content-center">
              { uniqueTracks.map((track, i) => (
                <div className="col col-md-3" key={i}>
                  <SelectableCard id={`failed-track-${i}`}
                                  text={track.name}
                                  imageUrl={track.imageUrl}
                                  disabled={true} />
                </div>  
                )) 
              } 
            </div>  
          </div>
        }
        <div className="row justify-content-center section">
          <button className="transfer-home-button" onClick={onHomeClick}>
            Home
          </button>
        </div>     
      </div>
    );
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { loading.tracks 
        ? renderTracksLoading()
        :  loading.playlists
          ? renderPlaylistLoading()
          : renderResult()
      }
    </div>
  );
}
  
export default TransferPage;
  