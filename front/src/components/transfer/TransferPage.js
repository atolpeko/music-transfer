import { useState, useEffect } from 'react';
import SelectableCard from '../SelectableCard';
import Spinner from '../spinner/Spinner';

import './TransferPage.css';

const TransferPage = ({ source, target, transferTracks, transferPlaylists, onHomeClick }) => {

  const [tracksResult, setTracksResult] = useState({ data: undefined, loading: true });
  const [playlistsResult, setPlaylistsResult] = useState({ data: undefined, loading: true });
  const [failed, setFailed] = useState([]);
  const [error, setError] = useState(undefined);

  useEffect(() => {
    transferTracks().then(res => {
      setTracksResult({ data: res, loading: false });
      setFailed(failed.concat(res.failedToTransfer));
    }).catch(error => {
      setError(`Track transfer error: ${error}`)
      setTracksResult({ loading: false });
    });

    transferPlaylists().then(res => {
      setPlaylistsResult({ data: res, loading: false });
      setFailed(failed.concat(res.failedToTransfer));
    }).catch(error => {
      setError(`Playlist transfer error: ${error}`)
      setPlaylistsResult({ loading: false });
    });
  }, []);

  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
          { !error
            ?  <h1 className="page-title">
                Transferred {tracksResult.data.transferred || 0} tracks
                and {playlistsResult.data.transferred || 0} playlists from {source} to {target}
               </h1>
            :  <h1 className="page-title"><p style= {{ color: 'red' }}> {error} </p></h1>
          }    
        </div>
        <div className="row justify-content-center section">
          <button onClick={onHomeClick}>
            Home
          </button>
        </div>
      
        { !error &&
          <div className="row justify-content-center section">
            { failed.length > 0  &&
              <div className="row align-items-center justify-content-center section">
                <h4 className="transfer-fail-text">Tracks Failed to Transfer</h4>
                <div className="row align-items-center justify-content-center">
                  { failed.map((track, i) => (
                    <div className="col col-md-3" key={i}>
                      <SelectableCard id={"track-" + i}
                                      text={track.name + ' ·  ID ' + track.id}
                                      imageUrl={track.imageUrl} />
                    </div>  
                  )) 
                  } 
                </div>
              </div>
            }  
          </div>   
        }
      </div>
    );
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { tracksResult.loading 
        ? <Spinner text={`Transferring tracks from ${source} to ${target}...`} />
        :  playlistsResult.loading
          ? <Spinner text={`Transferring playlists from ${source} to ${target}...`} />
          : renderContent() 
      }
    </div>
  );
}
  
export default TransferPage;
  