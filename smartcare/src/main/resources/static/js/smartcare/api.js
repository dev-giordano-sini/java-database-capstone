export const API = '/api';

export function session() {
    return {
        token: localStorage.getItem('smartcare.token'),
        role: localStorage.getItem('smartcare.role')
    };
}

export function saveSession(token, role) {
    localStorage.setItem('smartcare.token', token);
    localStorage.setItem('smartcare.role', role);
}

export function clearSession() {
    localStorage.removeItem('smartcare.token');
    localStorage.removeItem('smartcare.role');
}

export async function request(path, options = {}) {
    const headers = new Headers(options.headers || {});
    if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
    const { token } = session();
    if (token && !headers.has('Authorization')) headers.set('Authorization', `Bearer ${token}`);

    const response = await fetch(`${API}${path}`, { ...options, headers });
    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : await response.text();
    if (!response.ok) {
        const message = payload?.message || payload?.errors?.join(', ') || payload || 'Request failed';
        const error = new Error(message);
        error.status = response.status;
        throw error;
    }
    return payload;
}

export function requireRole(expectedRole) {
    const current = session();
    if (!current.token || current.role !== expectedRole) {
        clearSession();
        window.location.replace('/#access');
        return false;
    }
    return true;
}

export function initializeHeader() {
    const current = session();
    const role = document.querySelector('[data-session-role]');
    const logout = document.querySelector('[data-logout]');
    const login = document.querySelector('[data-login-link]');
    if (current.token && current.role) {
        if (role) { role.textContent = current.role; role.hidden = false; }
        if (logout) logout.hidden = false;
        if (login) login.hidden = true;
    }
    logout?.addEventListener('click', () => {
        clearSession();
        window.location.assign('/#access');
    });
}

export function toast(message) {
    const element = document.querySelector('[data-toast]');
    if (!element) return;
    element.textContent = message;
    element.hidden = false;
    window.clearTimeout(element.toastTimer);
    element.toastTimer = window.setTimeout(() => { element.hidden = true; }, 3200);
}

export function initials(name = '') {
    return name.split(/\s+/).filter(Boolean).slice(0, 2).map(part => part[0]).join('').toUpperCase() || 'SC';
}

export function escapeHtml(value = '') {
    const node = document.createElement('div');
    node.textContent = String(value);
    return node.innerHTML;
}
