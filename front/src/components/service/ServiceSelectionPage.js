import { useState, useEffect } from 'react';
import SelectableCard from '../SelectableCard';
import Spinner from '../spinner/Spinner';

import './ServiceSelectionPage.css';

const ServiceSelectionPage = ({ source, loadServices, 
  onSourceSelection, onTargetSelection, onBackClick }) => {

  const [entity, setEntity] = useState('Source');
  const [selected, setSelected] = useState('');
  const [error, setError] = useState('');
  const [services, setServices] = useState({ 
    source: [], 
    target: [], 
    loading: true,
    failed: false 
  });
 
  useEffect(() => {
    loadServices().then(data => {
      const targets = source 
        ? data.target.filter(service => service.visibleName != source.visibleName)
        : data.target;
      targets.forEach(service => service.available = true);
      data.source.forEach(service => service.available = true);

      setServices({
        source: data.source,
        target: data.target,
        loading: false,
        failed: false
      });
      
      if (source) {
        setEntity('Target');
      }
    })
    .catch(() => {
      setServices({ failed: true, loading: false })
    }); 
  }, []);
  
  const handleCardSelection = id => {
    const i = id.replace('service-card-', '');
    const service = (entity == 'Source') 
      ? services.source.at(i).visibleName
      : services.target.at(i).visibleName;
    setSelected(service);  
    setError('');
  }

  const handleBackClick = () => {
    setEntity('Source');
    setSelected('');
    setError('');
    resetSelection();
    services.target.forEach(service => service.available = true);
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
    onSourceSelection(services.source.find(svc => svc.visibleName == selected))
    resetSelection();
    setEntity('Target');
    setError('');
  }

  const removeFromTargets = source => {
    var target = services.target.find(service => service.visibleName == source);
    if (target) {
      target.available = false;
    }
  }

  const targetDone = () => {
    const service = services.target.find(svc => svc.visibleName == selected);
    onTargetSelection(service);
    setError('');
  }

  const renderServices = services => {
    return services.map((service, i) => {
      return service.available == true &&
        <div className="col col-md-3" key={i}>
          <SelectableCard key={i}
                          id={"service-card-" + i}
                          text={service.visibleName}
                          imageUrl={service.logoUrl} 
                          onSelect={handleCardSelection} />
         </div>
    });
  }
  
  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
        { services.source.length > 0 
          ? <h1 className="page-title">Select {entity}</h1>
          : <h1 className="page-title">No services available now</h1>
        }
        </div>
        <div className="row justify-content-center section">
          { entity == 'Source' 
            ? renderServices(services.source) 
            : renderServices(services.target)
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
          { services.source.length > 0 &&
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
      { services.loading
        ? <Spinner text='Loading services...' />
        : !services.failed
           ? renderContent() 
           : <div/>
      }
    </div>
  );
}
  
export default ServiceSelectionPage;
