import { useEffect } from 'react';
import './SelectableCard.css';

const SelectableCard = ({ id, text, imageUrl, disabled, 
  reversedSelection, selected, onSelect }) => {

  const getThis = () => document.getElementById(id);
  const getAll = () => document.querySelectorAll('.selectable-card');

  useEffect(() => {
    if (disabled) {
      getThis().classList.remove('selectable-card_enabled');	
    }
    if (selected) {
		  getThis().classList.add('selected');
	  }
  }, []);

  const handleSelection = id => {
    if (!reversedSelection) {    
      getAll().forEach(card => card.classList.remove('selected'));	
    } 

    const card = getThis();
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
    <div id={id}
         className="card selectable-card selectable-card_enabled" 
         onClick={() => handleSelection(id)}>
      <img src={imageUrl} 
           alt={text}
           onClick={ () => handleSelection(id) }
           className="card-img-top selectable-card-img" />
      <h4 className="card-title selectable-card-text">{text}</h4>
    </div>
  );
}
  
export default SelectableCard;
