import './Modal.css';

const Modal = ({ title, content, onClose }) => {
  return (
    <div id='modal' className="modal d-block" role="dialog">
      <div className="modal-dialog modal-lg" role="document">
        <div className="modal-content">
          <div className="modal-header justify-content-center">
            <h4 className="modal-title">{title}</h4>
          </div>
          <div className="modal-body">
            <div className="row align-items-center justify-content-center">
              {content}       
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" 
                    onClick={onClose}>
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Modal;
