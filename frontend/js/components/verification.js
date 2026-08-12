import { CONFIG } from '../config/config.js';

export function initVerification() {
    console.log('Initializing Independent Verification Hub Component...');

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
