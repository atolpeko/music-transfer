import React from 'react';
import ReactDOM from 'react-dom/client';
import ErrorBoundary from './ErrorBoundary';
import ErrorPage from './components/error/ErrorPage';
import App from './App';

import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
<ErrorBoundary fallback={<ErrorPage onHomeClick={() => window.location='/home' } /> }>
	<App />
</ErrorBoundary>
)

