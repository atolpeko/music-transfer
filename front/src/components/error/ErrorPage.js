import './ErrorPage.css';

const ErrorPage = ({ error, onHomeClick }) => {

  const handleReloadClick = () => window.location.reload();

  return (
    <div className="container center-container align-items-center justify-content-center">
      <div className="row justify-content-center section">
        <h1 className="page-heading">
          Something went wrong :(
        </h1>
      </div>
      
      <div className="row justify-content-center section">
        { error && <h4>{error}</h4> }
      </div>
      <div className="row justify-content-center">
      <button className="error-button" onClick={handleReloadClick}>
          Reload
        </button>
        <button className="error-button" onClick={onHomeClick}>
          Home
        </button>
      </div>
    </div>
  );
}
  
export default ErrorPage;
  