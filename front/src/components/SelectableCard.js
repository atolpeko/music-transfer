import { useEffect } from 'react';

const SelectableCard = ({ id, text, imageUrl, reversedSelection, selected, onSelect }) => {

  useEffect(() => {
    if (selected && selected == true) {
		  document.getElementById(id).classList.add('selected');
	  }
  }, []);

  const handleSelection = id => {
    if (!reversedSelection || reversedSelection == false) {    
      const cards = document.querySelectorAll('.card');
      cards.forEach(card => card.classList.remove('selected'));	
    } 

    const card = document.getElementById(id);
    const selected = card.classList.contains('selected');
    if (selected) {
      card.classList.remove('selected');
	  } else {
      card.classList.add('selected');
    }

    if (onSelect) {
      onSelect(id, !selected);
	  }
  }

  return (
    <div id={id} className="card item-card" onClick={ () => handleSelection(id) }>
      <img src={imageUrl} 
           alt={text}
           onClick={ () => handleSelection(id) }
           className="card-img-top item-card-img" />
      <h4 className="card-title item-card-text">{text}</h4>
    </div>
  );
}
  
export default SelectableCard;
