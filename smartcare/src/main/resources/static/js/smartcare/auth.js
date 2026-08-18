import { request, saveSession, session, initializeHeader, toast, initials, escapeHtml } from './api.js';

initializeHeader();

const dashboardByRole = { patient: '/patient-dashboard', doctor: '/doctor-dashboard', admin: '/admin-dashboard' };
const current = session();
if (current.token && dashboardByRole[current.role]) {
    document.querySelector('[data-login-link]')?.setAttribute('href', dashboardByRole[current.role]);
    document.querySelectorAll('[data-auth-guest]').forEach(element => { element.hidden = true; });
    const authenticatedCard = document.querySelector('[data-auth-session]');
    const dashboardLink = document.querySelector('[data-dashboard-link]');
    const authenticatedRole = document.querySelector('[data-auth-role]');
    if (authenticatedCard) authenticatedCard.hidden = false;
    if (dashboardLink) dashboardLink.href = dashboardByRole[current.role];
    if (authenticatedRole) authenticatedRole.textContent = current.role;
}

const directoryState = { page: 0, size: 5, specialty: '' };

async function loadDoctors() {
    const container = document.querySelector('[data-public-doctors]');
    if (!container) return;
    try {
        const params = new URLSearchParams({ page: directoryState.page, size: directoryState.size });
        if (directoryState.specialty) params.set('specialty', directoryState.specialty);
        const result = await request(`/doctor?${params}`);
        const doctors = result.data || [];
        container.innerHTML = doctors.length ? doctors.map(doctorCard).join('') : emptyDoctors();
        updateDirectoryPagination(result);
    } catch (error) {
        container.innerHTML = `<div class="empty-state">The medical directory is temporarily unavailable.</div>`;
    }
}

async function initializeDirectoryControls() {
    const specialty = document.querySelector('[data-doctor-specialty]');
    const pageSize = document.querySelector('[data-doctor-page-size]');
    try {
        const specialties = await request('/doctor/specialties');
        specialty.innerHTML += specialties.map(value => `<option value="${escapeHtml(value)}">${escapeHtml(value)}</option>`).join('');
    } catch (error) {
        specialty.disabled = true;
    }
    specialty?.addEventListener('change', () => {
        directoryState.specialty = specialty.value;
        directoryState.page = 0;
        loadDoctors();
    });
    pageSize?.addEventListener('change', () => {
        directoryState.size = Math.min(Number(pageSize.value), 10);
        directoryState.page = 0;
        loadDoctors();
    });
    document.querySelector('[data-doctor-previous]')?.addEventListener('click', () => {
        if (directoryState.page > 0) { directoryState.page--; loadDoctors(); }
    });
    document.querySelector('[data-doctor-next]')?.addEventListener('click', () => {
        directoryState.page++;
        loadDoctors();
    });
}

function updateDirectoryPagination(result) {
    const totalPages = Number(result.totalPages || 0);
    const previous = document.querySelector('[data-doctor-previous]');
    const next = document.querySelector('[data-doctor-next]');
    const status = document.querySelector('[data-doctor-page-status]');
    previous.disabled = result.page <= 0;
    next.disabled = totalPages === 0 || result.page + 1 >= totalPages;
    status.textContent = totalPages ? `Page ${result.page + 1} of ${totalPages} · ${result.totalElements} doctors` : 'No doctors found';
}

function doctorCard(doctor) {
    const rating = Number(doctor.rating || 0);
    const bookingHref = current.token && current.role === 'patient' ? '/patient-dashboard' : '#access';
    const phone = doctor.phone
        ? `<span class="doctor-phone"><span aria-hidden="true">☎</span><span>Phone: ${escapeHtml(doctor.phone)}</span></span>`
        : '';
    return `<article class="doctor-card">
        <div class="doctor-card-top">${doctorAvatar(doctor)}<span class="rating">${'★'.repeat(rating)}${'☆'.repeat(Math.max(0, 5-rating))}</span></div>
        <h3>${escapeHtml(doctor.name)}</h3>${specialtyBadge(doctor.specialty)}
        <div class="doctor-meta"><span class="doctor-contact"><span>${escapeHtml(doctor.email)}</span>${phone}</span><a href="${bookingHref}">Book →</a></div>
    </article>`;
}

function specialtyBadge(specialty) {
    const presentations = {
        cardiology: ['♥', 'coral'], dermatology: ['✦', 'gold'], pediatrics: ['★', 'sky'], neurology: ['⌁', 'violet'],
        orthopedics: ['◆', 'sage'], ophthalmology: ['◉', 'sky'], psychiatry: ['☼', 'violet'], gynecology: ['♀', 'coral'],
        endocrinology: ['⚗', 'gold'], gastroenterology: ['≈', 'sage'], pulmonology: ['♧', 'sky'], urology: ['◇', 'violet'],
        otolaryngology: ['♪', 'gold'], 'general medicine': ['✚', 'sage']
    };
    const [icon, tone] = presentations[String(specialty || '').toLowerCase()] || ['✚', 'sage'];
    return `<span class="specialty-badge specialty-${tone}"><span aria-hidden="true">${icon}</span>${escapeHtml(specialty)}</span>`;
}
function doctorAvatar(doctor) {
    const fallback = escapeHtml(initials(doctor.name));
    const source = doctor.profileImageUrl || '/assets/images/doctor_default.svg';
    return `<div class="doctor-avatar"><img src="${escapeHtml(source)}" alt="" loading="lazy" referrerpolicy="no-referrer" onerror="this.onerror=null;this.src='/assets/images/doctor_default.svg'"><span>${fallback}</span></div>`;
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

initializeDirectoryControls();
loadDoctors();
