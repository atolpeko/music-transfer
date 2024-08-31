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
  const [trackTransferred, setTrackTransferred] = useState(0);
  const [playlistsTransferred, setPlaylistsTransferred] = useState(0);
  const [tracksFailed, setTracksFailed] = useState([]);
  const [playlistsFailed, setPlaylistsFailed] = useState([]);
  const [modal, setModal] = useState({ show: false, playlist: null });

  useEffect(() => {
    window.onclick = event => {
      const modal = document.getElementById('modal'); 
      if (modal && event.target.id == modal.id)
        setModal({ show: false, entity: null });
    };
  }, []);

  useEffect(() => {
    const call = async () => {
      await runTrackTransfer();
      runPlaylistTransfer();
    }

    call();
  }, []);

  const runTrackTransfer = () => {
    return transferTracks().then(res => {
      setTrackTransferred(res.transferred);
      setTracksFailed(tracksFailed.concat(res.failedToTransfer));
      setLoading({ tracks: false, playlists: true });
      return res;
    }).catch(() => {
      setTracksFailed(tracksFailed.concat(tracks));
    });
  }

  const runPlaylistTransfer = async () => {
    setPlaylistsFailed([]);
    let transferred = 0;
    for (let i = 0; i < playlists.length; i++) {
      const playlist = playlists[i];
      try {
        const res = await transferPlaylist(playlist);
        if (res.transferred > 0) {
          transferred += 1;
        }
        if (res.failedToTransfer) {
          setPlaylistsFailed(playlistsFailed.concat(playlist));
        }
      } catch (e) {
        setPlaylistsFailed(playlistsFailed.concat(playlist))
      }
    }

    console.log(transferred);
    setPlaylistsTransferred(transferred);
    setLoading({ tracks: false, playlists: false });
  }

  const toggleModal = playlist => setModal({ show: !modal.show, playlist: playlist });

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
                <SelectableCard id={`track-${i}`}
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
                <SelectableCard id={`playlist-${i}`}
                                text={playlist.name}
                                imageUrl={playlist.imageUrl} 
                                disabled={true} />
              </div>  
            )) 
          } 
        </div>
        <Spinner text={`Transferring ${playlists.length} Playlists with ${totalTracks} Tracks`}/> 
      </div>
    );
  }

  const renderFailedTracks = () => {
    return modal.playlist.tracks.map((track, i) => (
      <div className="col col-md-3" key={i}>
        <SelectableCard id={"track-" + i}
                        text={[track.name, <br key={i}/>, 'ID: ', track.id]}
                        imageUrl={track.imageUrl}
                        disabled={true}/>
      </div>  
    ));
  }

  const renderResult = () => {
    const uniqueTracks = [...new Set(tracksFailed)];
    return (
      <div>
        <div className="row align-items-center justify-content-center section">
          <h1 className="page-title">
            {source} to {target} Transfer Result
          </h1>
        </div>
        <div className="row align-items-center justify-content-center section">
          <h3>Transferred {trackTransferred} Tracks and {playlistsTransferred} Playlists</h3>
        </div>  
        { playlistsFailed.length > 0  &&
          <div>
            <div className="row align-items-center justify-content-center section">
              <h4 className="transfer-fail-text">
                Playlists Failed to Transfer. Click to See Details
              </h4>
            </div>
            <div className="row align-items-center justify-content-center">
              { playlistsFailed.map((playlist, i) => (
                <div className="col col-md-3" key={i}>
                  <SelectableCard id={"failed-" + i}
                                  text={[playlist.name, <br key={i}/>, 'ID: ', playlist.id]}
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
        { modal.show && <div className="modal-backdrop fade show"/> }  

        { uniqueTracks.length > 0  &&
          <div>
            <div className="row justify-content-center section">
              <h4 className="transfer-fail-text">Tracks Failed to Transfer</h4>
            </div>
            <div className="row align-items-center justify-content-center">
              { uniqueTracks.map((track, i) => (
                <div className="col col-md-3" key={i}>
                  <SelectableCard id={"track-" + i}
                                  text={[track.name, <br key={i}/>, 'ID: ', track.id]}
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
  