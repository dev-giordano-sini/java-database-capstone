import { request, saveSession, session, initializeHeader, toast, initials, escapeHtml } from './api.js';

initializeHeader();

const dashboardByRole = { patient: '/patient-dashboard', doctor: '/doctor-dashboard', admin: '/admin-dashboard' };
const current = session();
if (current.token && dashboardByRole[current.role]) {
    document.querySelector('[data-login-link]')?.setAttribute('href', dashboardByRole[current.role]);
}

async function loadDoctors() {
    const container = document.querySelector('[data-public-doctors]');
    if (!container) return;
    try {
        const result = await request('/doctor');
        const doctors = result.data || [];
        container.innerHTML = doctors.length ? doctors.slice(0, 6).map(doctorCard).join('') : emptyDoctors();
    } catch (error) {
        container.innerHTML = `<div class="empty-state">The medical directory is temporarily unavailable.</div>`;
    }
}

function doctorCard(doctor) {
    const rating = Number(doctor.rating || 0);
    return `<article class="doctor-card">
        <div class="doctor-card-top">${doctorAvatar(doctor)}<span class="rating">${'★'.repeat(rating)}${'☆'.repeat(Math.max(0, 5-rating))}</span></div>
        <h3>${escapeHtml(doctor.name)}</h3><p>${escapeHtml(doctor.specialty)}</p>
        <div class="doctor-meta"><span>${escapeHtml(doctor.email)}</span><a href="#access">Book →</a></div>
    </article>`;
}
function doctorAvatar(doctor) {
    const fallback = escapeHtml(initials(doctor.name));
    return doctor.profileImageUrl
        ? `<div class="doctor-avatar"><img src="${escapeHtml(doctor.profileImageUrl)}" alt="" loading="lazy" referrerpolicy="no-referrer" onerror="this.remove()"><span>${fallback}</span></div>`
        : `<div class="doctor-avatar"><span>${fallback}</span></div>`;
}
function emptyDoctors(){ return '<div class="empty-state">Doctors will appear here as soon as the administrator adds them.</div>'; }

const tabs = document.querySelectorAll('[data-role-tab]');
const loginForm = document.querySelector('[data-login-form]');
tabs.forEach(tab => tab.addEventListener('click', () => {
    tabs.forEach(item => item.classList.toggle('active', item === tab));
    loginForm.elements.role.value = tab.dataset.roleTab;
    const input = loginForm.elements.identifier;
    const admin = tab.dataset.roleTab === 'admin';
    input.type = admin ? 'text' : 'email';
    document.querySelector('[data-identifier-label]').textContent = admin ? 'Username' : 'Email address';
}));

loginForm?.addEventListener('submit', async event => {
    event.preventDefault();
    const errorElement = document.querySelector('[data-form-error]');
    errorElement.hidden = true;
    if (!loginForm.reportValidity()) return;
    const data = Object.fromEntries(new FormData(loginForm));
    const role = data.role;
    const path = role === 'admin' ? '/admin/login' : role === 'doctor' ? '/doctor/login' : '/patients/login';
    const body = role === 'admin'
        ? { username: data.identifier, password: data.password }
        : { identifier: data.identifier, password: data.password };
    try {
        const result = await request(path, { method: 'POST', body: JSON.stringify(body) });
        if (!result.token) throw new Error('The server did not return a session token');
        saveSession(result.token, role);
        window.location.assign(dashboardByRole[role]);
    } catch (error) {
        errorElement.textContent = error.message;
        errorElement.hidden = false;
    }
});

const dialog = document.querySelector('[data-signup-dialog]');
document.querySelector('[data-signup-toggle]')?.addEventListener('click', () => dialog.showModal());
document.querySelector('[data-dialog-close]')?.addEventListener('click', () => dialog.close());
document.querySelector('[data-signup-form]')?.addEventListener('submit', async event => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;
    const errorElement = document.querySelector('[data-signup-error]');
    errorElement.hidden = true;
    const body = Object.fromEntries(new FormData(form));
    try {
        await request('/patients', { method: 'POST', body: JSON.stringify(body) });
        dialog.close(); form.reset(); toast('Account created. You can now sign in.');
    } catch (error) {
        errorElement.textContent = error.message; errorElement.hidden = false;
    }
});

loadDoctors();
