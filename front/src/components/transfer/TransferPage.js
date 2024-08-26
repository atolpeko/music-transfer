import { useState, useEffect } from 'react';
import ClipLoader from "react-spinners/ClipLoader";
import SelectableCard from '../SelectableCard';

import './TransferPage.css';

const TransferPage = ({ source, target, run }) => {

  const [running, setRunning] = useState(true);
  const [result, setResult] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    run().then(res => {
      setResult(res);
      setRunning(false);
    }).catch(error => {
      setError(`Transfer error: ${error}`)
      setRunning(false);
    });
  }, [])

  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
          <h2 className="transfer-title">
            Transferred {result.tracksCount || 0} tracks
             and {result.playlistsCount || 0} playlists from {source} to {target}
          </h2>  
        </div>
        <div className="row justify-content-center section">
          { result.failed && result.failed.length > 0 &&
            <div className="row align-items-center justify-content-center section">
              <h4 className="transfer-fail-text">Tracks Failed to Transfer</h4>
              <div className="row align-items-center justify-content-center">
                { result.failed.map((track, i) => (
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
          <div className="row justify-content-center error-section"> 
            { error && <p style= {{ color: 'red' }}> {error} </p> }
          </div>    
        </div>   
      </div>
    )
  }

  const renderSpinner = () => {
    return (
      <div className="row justify-content-center section">
        <div className="container justify-content-center section">
          <h2 className="transfer-spinner-text">
            Transferring your Library from {source} to {target}...
          </h2>
        </div>
        <div className="row justify-content-center section">
          <ClipLoader loading={running}
                      className="spinner"
                      aria-label="Loading"
                      data-testid="loader" />
        </div>
      </div>
    )
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { running == true 
        ? renderSpinner()
        : renderContent()
      }
    </div>
  );
}
  
export default TransferPage;
  