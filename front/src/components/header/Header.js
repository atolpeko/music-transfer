import './Header.css';

const Header = ({ homeUrl, aboutUrl, contactUrl, supportUrl }) => {
  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light header">
      <div className="d-flex align-items-center justify-content-center">
        <button className="navbar-toggler"
                type="button"
                data-toggle="collapse"
                data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent"
                aria-expanded="false"
                aria-label="Navigation">
          <span className="navbar-toggler-icon" />
        </button>

        <a class="navbar-brand" href={homeUrl}>Home</a>
        <div className="collapse navbar-collapse"
            id="navbarSupportedContent">
          <ul className="navbar-nav mr-auto">
            <li className="nav-item">
              <a className="nav-link" href={aboutUrl}>About</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href={contactUrl}>Contact</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href={supportUrl}>Support</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Header;
