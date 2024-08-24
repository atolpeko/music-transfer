import './ServiceCard.css';

const ServiceCard = ({ id, name, logoUrl, onSelect }) => {

    const selectCard = (id, name) => {
    	let cards = document.querySelectorAll('.card');
    	cards.forEach(card => card.classList.remove('selected'));	
			document.getElementById(id).classList.add('selected');    
			onSelect(name);
    }

    return (
      <div id={id} className="card service-card" onClick={ () => selectCard(id, name) }>
        <img src={logoUrl} 
						 alt={name}
						 onClick={ () => selectCard(id, name) }
				 		 className="card-img-top service-img" />
        <h4 className="card-title service-card-text">{name}</h4>
      </div>
    );
  }
  
  export default ServiceCard;
