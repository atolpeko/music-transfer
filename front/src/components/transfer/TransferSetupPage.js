import { useState, useEffect } from 'react';
import Spinner from '../spinner/Spinner';
import SelectableCard from '../SelectableCard';
import Modal from '../modal/Modal';

import './TransferPage.css';

const TransferSetupPage = ({ source, target, load, onTransferClick }) => {

  const [sourceData, setSourceData] = useState({ 
    tracks: [], 
    playlists: [],
    loading: true,
    failed: false 
  });
  const [modal, setModal] = useState({ show: false, entity: null });
  const [error, setError] = useState('');

  useEffect(() => {
    load().then(data => {
      data.tracks.forEach(track => track.selected = true);
      data.playlists.forEach(playlist => playlist.selected = true);
      setSourceData({
        tracks: data.tracks,
        playlists: data.playlists,
        loading: false,
        failed: false
      });
    }).catch(() => {
      setSourceData({ loading: false, failed: true });
    });
  }, []);

  useEffect(() => {
    window.onclick = event => {
      const modal = document.getElementById('modal'); 
      if (modal && event.target.id == modal.id)
        setModal({ show: false, entity: null });
    };
  }, []);

  const toggleModal = selected => setModal({
    show: !modal.show,
    entity: selected
  });

  const handleTrackSelection = (id, isSelected) => {    
    const i = id.replace('track-', '');
    sourceData.tracks.at(i).selected = isSelected;
  }
  
  const handlePlaylistSelection = (id, isSelected) => {
    const i = id.replace('playlist-', '');
    sourceData.playlists.at(i).selected = isSelected;
  }

  const handleTransferClick = () => {
    const tracks = sourceData.tracks.filter(track => track.selected == true);
    const playlists = sourceData.playlists.filter(playlist => playlist.selected == true);
    if (tracks.length == 0) {
      setError('Select at least one track');
    } else {
      onTransferClick(tracks, playlists);
    }
  }

  const renderTracks = () => {
    return sourceData.tracks.map((track, i) => (
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
    return sourceData.playlists.map((playlist, i) => (
      <div className="col col-md-3" key={i}>
        <SelectableCard id={"playlist-" + i}
                        text={[playlist.name, <br key={i}/>, playlist.tracks.length, ' tracks']}
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
          <div className="transfer-info-card align-items-center justify-content-center
            shadow-sm p-6 mb-5 bg-white rounded">
            <h4 className="section">Found {sourceData.tracks.length} Liked Tracks
              and {sourceData.playlists.length} Playlists</h4> 
              <div className="row align-items-center justify-content-center section">
                <button className="transfer-page-button"
                        onClick={ () => toggleModal('Tracks') }>
                  View Tracks
                </button>
                <button className="transfer-page-button"
                        onClick={() => toggleModal('Playlists') }>
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
        </div>   
        { modal.show && ( 
          <Modal title={`Select ${modal.entity} to Transfer`} 
                 content={modal.entity == 'Tracks'  ? renderTracks() : renderPlaylists() }
                 onClose={toggleModal} />)
        }
      </div>
    );
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { sourceData.loading  
        ? <Spinner text={`Loading tracks and playlists from ${source}...`}/> 
        : !sourceData.failed
          ? renderContent() 
          : <div/>
        }
    </div>
  );
}
  
export default TransferSetupPage;
  