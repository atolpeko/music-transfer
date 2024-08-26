import { useState, useEffect } from 'react';
import SelectableCard from '../SelectableCard';
import Spinner from '../spinner/Spinner';

import './ServiceSelectionPage.css';

const ServiceSelectionPage = ({ source, loadServices, 
  onSourceSelection, onTargetSelection, onBackClick }) => {

  const [sourceServices, setSourceServices] = useState([]);
  const [targetServices, setTargetServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [entity, setEntity] = useState('Source');
  const [selected, setSelected] = useState('');
 
  useEffect(() => {
    loadServices().then(data => {
      const targets = source 
        ? data.target.filter(service => service.visibleName != source.visibleName)
        : data.target;
      targets.forEach(service => service.available = true);
      data.source.forEach(service => service.available = true);
     
      setSourceServices(data.source);
      setTargetServices(data.target);
      setLoading(false);
    })
    .then(() => {
      if (source) {
        setEntity('Target');
      }
    })
    .catch(loadError => {
      setError(`Failed to load services: ${loadError}`);
      setLoading(false);
    });
  }, []);
  
  const handleCardSelection = id => {
    const i = id.replace('service-card-', '');
    const service = (entity == 'Source') 
      ? sourceServices.at(i).visibleName
      : targetServices.at(i).visibleName;
    setSelected(service);  
    setError('');
  }

  const handleBackClick = () => {
    setEntity('Source');
    setSelected('');
    setError('');
    resetSelection();
    targetServices.forEach(service => service.available = true);
    onBackClick();
  }

  const resetSelection = () => {
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => card.classList.remove('selected'));	
  }

  const handleNextClick = () => {
    if (entity == 'Source' && selected == '') {
      setError('Please select source');
    } else if (entity == 'Target' && selected == '') {
      setError('Please select target');
    } else if (entity == 'Source') {
      sourceDone();
    } else {
      targetDone();
    }
  }

  const sourceDone = () => {
    removeFromTargets(selected);
    onSourceSelection(sourceServices.find(svc => svc.visibleName == selected))
    resetSelection();
    setEntity('Target');
    setError('');
  }

  const removeFromTargets = source => {
    var target = targetServices.find(service => service.visibleName == source);
    if (target) {
      target.available = false;
    }
  }

  const targetDone = () => {
    const service = targetServices.find(svc => svc.visibleName == selected);
    onTargetSelection(service);
    setError('');
  }

  const renderServices = services => {
    return services.map((service, i) => {
      return service.available == true
        ? ( <div className="col col-md-3" key={i}>
              <SelectableCard key={i}
                              id={"service-card-" + i}
                              text={service.visibleName}
                              imageUrl={service.logoUrl} 
                              onSelect={handleCardSelection} />
            </div> )
        : (<div key={i}/>)
    });
  }
  
  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
        { sourceServices.length > 0 
          ? <h1 className="page-title">Select {entity}</h1>
          : <h1 className="page-title">No services available now</h1>
        }
        </div>
        <div className="row justify-content-center section">
          { entity == 'Source' 
            ? renderServices(sourceServices) 
            : renderServices(targetServices)
          }
        </div>   
        <div className="row justify-content-center error-section"> 
          { error && <p style= {{ color: 'red' }}> {error} </p> }
        </div>    
        <div className="row justify-content-center"> 
          { entity == 'Target' &&
            <button className="service-page-button" onClick={handleBackClick}>
              Back
            </button>
          }
          { sourceServices.length > 0 &&
            <button className="service-page-button" onClick={handleNextClick}>
              Next
            </button>
          }
        </div>
      </div>
    );
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { loading 
        ? <Spinner text='Loading services...' />
        : renderContent() }
    </div>
  );
}
  
export default ServiceSelectionPage;
