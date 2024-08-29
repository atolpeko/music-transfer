import React from 'react';
import ReactDOM from 'react-dom/client';
import ErrorBoundary from './ErrorBoundary';
import App from './App';

import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'))
root.render(
<ErrorBoundary fallback={<p>Something went wrong</p>}>
	<App />
</ErrorBoundary>
)

