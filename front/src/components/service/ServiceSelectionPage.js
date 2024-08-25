import { useState, useEffect } from 'react';
import ClipLoader from "react-spinners/ClipLoader";
import SelectableCard from '../SelectableCard';

import './ServiceSelectionPage.css';

const ServiceSelectionPage = ({ loadServices, onDoneClick }) => {

  const [sourceServices, setSourceServices] = useState([]);
  const [targetServices, setTargetServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [select, setSelect] = useState('Source');
  const [source, setSource] = useState('');
  const [target, setTarget] = useState('');
  const [error, setError] = useState('');
 
  useEffect(() => {
    loadServices().then(data => {
      data.source.forEach(service => service.available = true)
      data.target.forEach(service => service.available = true)  
      setSourceServices(data.source);
      setTargetServices(data.target);
      setLoading(false);
    })
    .catch(loadError => {
      setError(`Failed to load services: ${loadError}`);
      setLoading(false);
    });
  }, [])
  
  const handleCardSelection = id => {
    var i = id.replace('service-card-', '');
    if (select == 'Source') {
      setSource(sourceServices.at(i).visibleName);
    } else {;
      setTarget(targetServices.at(i).visibleName);
    }

    setError('');
  }

  const handleBackClick = () => {
    if (select != 'Source') {
      setSelect('Source');
      setSource('');
      setError('');
      resetSelection();

      targetServices.forEach(service => service.available = true);
    }
  }

  const resetSelection = () => {
    let cards = document.querySelectorAll('.card');
    cards.forEach(card => card.classList.remove('selected'));	
  }

  const handleNextClick = () => {
    if (select == 'Source' && source == '') {
      setError('Please select source');
    } else if (select == 'Target' && target == '') {
      setError('Please select target');
    } else if (select == 'Source') {
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
    setSelect('Target');
    setError('');
  }

  const doneClicked = () => {
    onDoneClick(source, target);
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
    })
  }
  
  const renderContent = () => {
    return (
      <div>
        <div className="row justify-content-center section">
        { sourceServices.length > 0 
          ? <h1 className="service-title">Select {select}</h1>
          : <h1 className="service-title">No services available now</h1>
        }
        </div>
        <div className="row justify-content-center section">
          { select == 'Source' 
            ? renderServices(sourceServices) 
            : renderServices(targetServices)
          }
        </div>   
        <div className="row justify-content-center error-section"> 
          { error && <p style= {{ color: 'red' }}> {error} </p> }
        </div>    
        <div className="row justify-content-center"> 
          { select == 'Target' &&
            <button className="service-page-button" onClick={handleBackClick}>
              Back
            </button>
          }
          { sourceServices.length != 0 &&
            <button className="service-page-button" onClick={handleNextClick}>
              Next
            </button>
          }
        </div>
      </div>
    )
  }

  const renderSpinner = () => {
    return (
      <div className="row justify-content-center section">
        <ClipLoader loading={loading}
                    aria-label="Loading"
                    data-testid="loader" />
      </div>
    )
  }

  return (
    <div className="container center-container align-items-center justify-content-center">
      { loading == true 
        ? renderSpinner()
        : renderContent()
      }
    </div>
  );
}
  
export default ServiceSelectionPage;
