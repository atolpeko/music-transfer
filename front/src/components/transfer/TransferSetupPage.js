import { useState, useEffect } from 'react';
import Spinner from '../spinner/Spinner';
import SelectableCard from '../SelectableCard';

import './TransferPage.css';

const TransferSetupPage = ({ source, target, loadTracks, loadPlaylists, onTransferClick }) => {

  const [tracks, setTracks] = useState([]);
  const [playlists, setPlaylists] = useState([]);
  const [tracksLoading, setTracksLoading] = useState(true);
  const [playlistsLoading, setPlaylistsLoading] = useState(true);
  const [entity, setEntity] = useState('Tracks');    
  const [showModal, setShowModal] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadTracks().then(data => {
      data.forEach(track => track.selected = true)
      setTracks(data);
      setTracksLoading(false);
    }).catch(error => {
      setError(`Failed to load tracks: ${error}`)
      setTracksLoading(false);
    });
    
    loadPlaylists().then(data => {
      data.forEach(playlist => playlist.selected = true)
      setPlaylists(data);
      setPlaylistsLoading(false);
    }).catch(error => {
      setError(`Failed to load playlists: ${error}`)
      setPlaylistsLoading(false);
    });
  }, []);

  useEffect(() => {
    window.onclick = event => {
      const modal = document.getElementById('transferModal'); 
      if (modal && event.target.id == modal.id)
        setShowModal(false);
    };
  }, []);

  const toggleModal = () => setShowModal(!showModal);

  const handleShowTracksClick = () => {
    setEntity('Tracks');
    toggleModal();
  }

  const handleShowPlaylistsClick = () => {
    setEntity('Playlists');
    toggleModal();
  }

  const handleTrackSelection = (id, isSelected) => {    
    const i = id.replace('track-', '');
    tracks.at(i).selected = isSelected;
  }
  
  const handlePlaylistSelection = (id, isSelected) => {
    const i = id.replace('playlist-', '');
    playlists.at(i).selected = isSelected;
  }

  const handleTransferClick = () => {
    const selectedTracks = tracks.filter(track => track.selected == true);
    const selectedPlaylists = playlists.filter(playlist => playlist.selected == true);
    if (selectedTracks.length == 0) {
      setError('Select at least one track');
    } else {
      onTransferClick(selectedTracks, selectedPlaylists);
    }
  }

  const renderTracks = () => {
    return tracks.map((track, i) => (
      <div className="col col-md-3" key={i}>
        <SelectableCard id={"track-" + i}
                        text={track.name}
                        imageUrl={track.imageUrl}
                        reversedSelection={true}
                        selected={track.selected}
                        onSelect={handleTrackSelection}/>
      </div>  
    ));
  }

  const renderPlaylists = () => {
    return playlists.map((playlist, i) => (
      <div className="col col-md-3" key={i}>
        <SelectableCard id={"playlist-" + i}
                        text={playlist.name + ' · ' + playlist.tracks.length + ' tracks'}
                        imageUrl={playlist.imageUrl}
                        reversedSelection={true}
                        selected={playlist.selected}
                        onSelect={handlePlaylistSelection}/>
      </div>  
    ));
  }

  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
          <h1 className="page-title">
            Transfer from {source} to {target}
          </h1>
        </div>
        <div className="row justify-content-center section">
          { error 
            ? <p style= {{ color: 'red' }}> {error} </p>
            : <div className="card transfer-info-card align-items-center justify-content-center
                shadow-sm p-6 mb-5 bg-white rounded">
                <h4 className="section">Found {tracks.length} Liked Tracks
                  and {playlists.length} Playlists</h4> 
                <div className="row align-items-center justify-content-center section">
                  <button className="transfer-page-button"
                          onClick={handleShowTracksClick}>
                    View Tracks
                  </button>
                  <button className="transfer-page-button"
                          onClick={handleShowPlaylistsClick}>
                    View Playlists
                  </button>
                  <button className="transfer-page-button transfer-page-run-button"
                          onClick={handleTransferClick}>
                    Transfer
                  </button>
                </div>
                <div className="row justify-content-center error-section"> 
                  { error && <p style= {{ color: 'red' }}> {error} </p> }
                </div> 
              </div>   
          }
        </div>   
        { showModal && (
          <div id='transferModal' className="modal transfer-modal d-block" role="dialog">
            <div className="modal-dialog modal-lg" role="document">
              <div className="modal-content">
                <div className="modal-header justify-content-center">
                  <h4 className="modal-title">Select {entity} to Transfer</h4>
                </div>
                <div className="modal-body">
                  <div className="row align-items-center justify-content-center">
                    { entity == 'Tracks'  
                      ? renderTracks() 
                      : renderPlaylists() 
                    }
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" 
                          onClick={toggleModal}>
                    Close
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        { showModal && <div className="modal-backdrop fade show"/> }  
      </div>
    );
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { tracksLoading || playlistsLoading 
        ? <Spinner text={`Loading tracks and playlists from ${source}...`}/> 
        : renderContent() 
        }
    </div>
  );
}
  
export default TransferSetupPage;
  