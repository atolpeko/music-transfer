import { useState, useEffect } from 'react';
import SelectableCard from '../SelectableCard';
import Spinner from '../spinner/Spinner';

import './TransferPage.css';

const TransferPage = ({ source, target, run }) => {

  const [result, setResult] = useState({ data: undefined, loading: true });
  const [error, setError] = useState('');

  useEffect(() => {
    run().then(res => {
      setResult({ data: res, loading: false });
    }).catch(error => {
      setError(`Transfer error: ${error}`)
      setResult({ loading: false });
    });
  }, []);

  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
          <h2 className="page-title">
            Transferred {result.data.tracksCount || 0} tracks
             and {result.data.playlistsCount || 0} playlists from {source} to {target}
          </h2>  
        </div>
        <div className="row justify-content-center section">
          { result.data.failed && result.data.failed.length > 0 &&
            <div className="row align-items-center justify-content-center section">
              <h4 className="transfer-fail-text">Tracks Failed to Transfer</h4>
              <div className="row align-items-center justify-content-center">
                { result.data.failed.map((track, i) => (
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
    );
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { result.loading 
        ? <Spinner text={`Transferring the Library from ${source} to ${target}...`} />
        : renderContent() 
      }
    </div>
  );
}
  
export default TransferPage;
  