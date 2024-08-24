import { useState, useEffect } from 'react';
import ServiceCard from './ServiceCard';

import './ServicePage.css';

const ServiceSelectionPage = ({ sourceServices, targetServices, onLoad, onDoneClick }) => {

  const [select, setSelect] = useState('source');
  const [source, setSource] = useState('');
  const [target, setTarget] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    onLoad();
  }, [])

  const handleCardSelection = service => {
    if (select == 'source') {
      setSource(service);
    } else {
      setTarget(service);
    }

    setError('');
  }

  const handleBackClick = () => {
    if (select != 'source') {
      setSelect('source');
      setSource('');
      setError('');
      resetSelection();

      targetServices.forEach(service => service.available = 'true');
    }
  }

  const resetSelection = () => {
    let cards = document.querySelectorAll('.card');
    cards.forEach(card => card.classList.remove('selected'));	
  }

  const handleNextClick = () => {
    if (select == 'source' && source == '') {
      setError('Please select source');
    } else if (select == 'target' && target == '') {
      setError('Please select target');
    } else if (select == 'source') {
      nextClicked();
    } else {
      doneClicked();
    }
  }

  const nextClicked = () => {
    var target = targetServices.find(service => service.visibleName === source);
    if (target != null && target != undefined) {
      target.available = false;
    }
    resetSelection();
    setSelect('target');
    setError('');
  }

  const doneClicked = () => {
    onDoneClick(source, target);
    setError('');
  }

  const renderServices = services => {
    return services.map((service, i) => {
      return service.available == 'true'
        ? ( <div className="col col-md-3" key={i}>
              <ServiceCard key={i}
                           id={"service-card-" + i}
                           name={service.visibleName}
                           logoUrl={service.logoUrl} 
                           onSelect={handleCardSelection} />
            </div> )
        : (<div key={i}/>)  
    })
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      <div className="row justify-content-center section">
        <h1 className="service-title">
          Select {select}
        </h1>
      </div>
      <div className="row justify-content-center section">
        { select == 'source' 
          ? renderServices(sourceServices) 
          : renderServices(targetServices)
        }
      </div>   
      <div className="row justify-content-center error-section"> 
        { error && <p style= {{ color: 'red' }}> {error} </p> }
      </div>    
      <div className="row justify-content-center"> 
        { select == 'target' &&
          <button className="service-page-button" onClick={handleBackClick}>
            Back
          </button>
        }
        <button className="service-page-button" onClick={handleNextClick}>
          Next
        </button>
      </div>
    </div>
  );
}
  
export default ServiceSelectionPage;
