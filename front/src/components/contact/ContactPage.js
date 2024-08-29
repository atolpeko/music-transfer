import './ContactPage.css';

const ContactPage = () => {
  return (
    <div className="container center-container">
      <div className="row section align-items-center justify-content-center">
        <h1 className="contact-heading">
          Contacts:
        </h1>
      </div>
      <div className="row section"> 
        <img src={`${process.env.PUBLIC_URL}/assets/email_icon.svg`}
             alt="Email"
             className="contact-img" />
        <h4>
          <b>Email:</b> alextolpeko@gmail.com
        </h4>
      </div>
      <div className="row section"> 
        <img src={`${process.env.PUBLIC_URL}/assets/telegram_icon.svg`}
             alt="Telegram"
             className="contact-img" />
        <h4>
          <b>Telegram: </b> @atolpeko
        </h4>
      </div>
      <div className="row section"> 
        <img src={`${process.env.PUBLIC_URL}/assets/github_icon.svg`}
             alt="Source"
             className="contact-img" />
        <h4>
          <b>Source: </b> 
          <a href='https://github.com/atolpeko/music-transfer'>
             Take part in development
          </a>
        </h4>
      </div>
    </div>
  );
}

export default ContactPage;
