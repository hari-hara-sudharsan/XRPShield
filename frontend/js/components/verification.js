import { CONFIG } from '../config/config.js';

export function initVerification() {
    console.log('Initializing Independent Verification Hub Component...');

    // Attach robust click listeners to all explorer links in verification table
    const tableBody = document.getElementById('verification-table-body');
    if (tableBody) {
        tableBody.querySelectorAll('a').forEach(link => {
            link.addEventListener('click', (e) => {
                const targetUrl = link.getAttribute('href');
                if (targetUrl && targetUrl.startsWith('http')) {
                    e.preventDefault();
                    window.open(targetUrl, '_blank', 'noopener,noreferrer');
                }
            });
        });
    }

    // Attempt to fetch live indexed Web3 events from Spring Boot indexer
    fetch('/api/v1/indexer/events')
        .then(res => res.json())
        .then(data => {
            if (data && data.success && data.data && data.data.length > 0) {
                console.log('Fetched live indexed events for verification:', data.data.length);
            }
        })
        .catch(err => {
            console.warn('Could not fetch indexer events for verification hub:', err);
        });
}

window.initVerification = initVerification;
