import ClipLoader from "react-spinners/ClipLoader";
import './Spinner.css';

const Spinner = ({ text }) => {
  return (
    <div className="container justify-content-center section">
      <div className="row justify-content-center section">
        <h2 className="spinner-text">{text}</h2>
      </div>
      <div className="row justify-content-center section">
        <ClipLoader loading={true}
                    className="spinner"
                    aria-label="Loading"
                    data-testid="loader" />
      </div>
    </div>
  );
}

export default Spinner;
