import { request, requireRole, initializeHeader, toast, initials, escapeHtml } from './api.js';

const page = document.body.dataset.page;
if (!requireRole(page)) throw new Error('Authentication required');
initializeHeader();

const formatDateTime = value => new Intl.DateTimeFormat('en', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value));
const today = () => new Date().toISOString().slice(0, 10);
const empty = message => `<div class="empty-state">${escapeHtml(message)}</div>`;

function doctorCard(doctor, actions = '') {
    return `<article class="doctor-card" data-search-value="${escapeHtml(`${doctor.name} ${doctor.specialty}`.toLowerCase())}">
        <div class="doctor-card-top">${doctorAvatar(doctor)}<span class="rating">${'★'.repeat(Number(doctor.rating || 0))}</span></div>
        <h3>${escapeHtml(doctor.name)}</h3><p>${escapeHtml(doctor.specialty)}</p>
        <div class="doctor-meta"><span>${escapeHtml(doctor.email)}</span><span>${escapeHtml(doctor.phone || '')}</span></div>
        ${actions}
    </article>`;
}

function doctorAvatar(doctor) {
    const fallback = escapeHtml(initials(doctor.name));
    return doctor.profileImageUrl
        ? `<div class="doctor-avatar"><img src="${escapeHtml(doctor.profileImageUrl)}" alt="" loading="lazy" referrerpolicy="no-referrer" onerror="this.remove()"><span>${fallback}</span></div>`
        : `<div class="doctor-avatar"><span>${fallback}</span></div>`;
}

function wireSearch(input, container) {
    input?.addEventListener('input', () => {
        const query = input.value.trim().toLowerCase();
        container.querySelectorAll('[data-search-value]').forEach(card => {
            card.hidden = !card.dataset.searchValue.includes(query);
        });
    });
}

async function loadPatientDashboard() {
    const profile = document.querySelector('[data-patient-profile]');
    const appointments = document.querySelector('[data-patient-appointments]');
    const doctorsContainer = document.querySelector('[data-patient-doctors]');
    try {
        const [patientResult, appointmentResult, doctorResult] = await Promise.all([
            request('/patients/me'), request('/patients/me/appointments'), request('/doctor')
        ]);
        const patient = patientResult.data;
        document.querySelector('[data-patient-name]').textContent = patient.name.split(' ')[0];
        profile.innerHTML = `<strong>${escapeHtml(patient.name)}</strong><span>${escapeHtml(patient.email)}</span><span>${escapeHtml(patient.phone || '')}</span>`;
        renderPatientAppointments(appointmentResult.data || [], appointments);
        const doctors = doctorResult.data || [];
        doctorsContainer.innerHTML = doctors.length ? doctors.map(doctor => doctorCard(doctor,
            `<div class="doctor-actions"><button class="button button-secondary button-block" data-book-doctor="${doctor.id}" data-doctor-name="${escapeHtml(doctor.name)}">Book appointment</button></div>`)).join('') : empty('No doctors are available yet.');
        wireSearch(document.querySelector('[data-doctor-search]'), doctorsContainer);
        doctorsContainer.addEventListener('click', event => {
            const button = event.target.closest('[data-book-doctor]');
            if (button) openBooking(button.dataset.bookDoctor, button.dataset.doctorName);
        });
    } catch (error) {
        if (error.status === 401) return logoutExpired();
        toast(error.message);
    }
}

function renderPatientAppointments(items, container) {
    const future = items.filter(item => new Date(item.appointmentTime) >= new Date()).sort((a,b) => new Date(a.appointmentTime)-new Date(b.appointmentTime));
    container.innerHTML = future.length ? future.map(item => `<article class="schedule-item">
        <div class="schedule-time">${new Date(item.appointmentTime).toLocaleTimeString([], {hour:'2-digit',minute:'2-digit'})}</div>
        <div><h3>${escapeHtml(item.doctorName)}</h3><p>${formatDateTime(item.appointmentTime)}</p></div>
        <button class="button button-danger" data-cancel-appointment="${item.id}">Cancel</button>
    </article>`).join('') : empty('No upcoming appointments. Choose a doctor below to get started.');
    container.onclick = async event => {
        const button = event.target.closest('[data-cancel-appointment]');
        if (!button || !confirm('Cancel this appointment?')) return;
        try { await request(`/appointments/${button.dataset.cancelAppointment}`, { method: 'DELETE' }); toast('Appointment cancelled.'); loadPatientDashboard(); }
        catch (error) { toast(error.message); }
    };
}

const bookingDialog = document.querySelector('[data-booking-dialog]');
const bookingForm = document.querySelector('[data-booking-form]');
function openBooking(id, name) {
    bookingForm.reset(); bookingForm.elements.doctorId.value = id; bookingForm.elements.doctorName.value = name;
    bookingForm.elements.date.min = today(); document.querySelector('[data-booking-doctor]').textContent = name;
    bookingDialog.showModal();
}
bookingDialog?.querySelector('[data-dialog-close]')?.addEventListener('click', () => bookingDialog.close());
bookingForm?.elements.date?.addEventListener('change', async () => {
    const select = bookingForm.elements.time;
    select.innerHTML = '<option value="">Loading…</option>';
    try {
        const result = await request(`/doctor/${bookingForm.elements.doctorId.value}/availability?date=${bookingForm.elements.date.value}`);
        select.innerHTML = result.data.length
            ? '<option value="">Select a time</option>' + result.data.map(time => `<option>${escapeHtml(time)}</option>`).join('')
            : '<option value="">No availability</option>';
    } catch (error) { select.innerHTML = '<option value="">Unavailable</option>'; toast(error.message); }
});
bookingForm?.addEventListener('submit', async event => {
    event.preventDefault(); if (!bookingForm.reportValidity()) return;
    const data = Object.fromEntries(new FormData(bookingForm));
    try {
        await request('/appointments', { method: 'POST', body: JSON.stringify({
            doctorId: Number(data.doctorId), doctorName: data.doctorName,
            appointmentTime: `${data.date}T${data.time}:00`, status: 0
        }) });
        bookingDialog.close(); toast('Appointment booked.'); loadPatientDashboard();
    } catch (error) { const element=document.querySelector('[data-booking-error]'); element.textContent=error.message;element.hidden=false; }
});

async function loadAdminDashboard() {
    const container = document.querySelector('[data-admin-doctors]');
    try {
        const [result, statistics] = await Promise.all([request('/doctor'), request('/admin/statistics/appointments')]);
        const doctors = result.data || [];
        container.innerHTML = doctors.length ? doctors.map(doctor => doctorCard(doctor,
            `<div class="doctor-actions"><button class="button button-danger" data-delete-doctor="${doctor.id}">Remove</button></div>`)).join('') : empty('No doctors in the directory.');
        document.querySelector('[data-doctor-count]').textContent = doctors.length;
        document.querySelector('[data-specialty-count]').textContent = new Set(doctors.map(item => item.specialty)).size;
        wireSearch(document.querySelector('[data-doctor-search]'), container);
        renderStatistics(statistics);
        container.onclick = async event => {
            const button = event.target.closest('[data-delete-doctor]');
            if (!button || !confirm('Remove this doctor and related appointments?')) return;
            try { await request(`/doctor/${button.dataset.deleteDoctor}`, { method:'DELETE' }); toast('Doctor removed.'); loadAdminDashboard(); }
            catch(error){ toast(error.message); }
        };
    } catch(error) { if(error.status===401)return logoutExpired(); container.innerHTML=empty(error.message); }
}

function renderStatistics(statistics) {
    const container = document.querySelector('[data-appointment-statistics]');
    if (!container) return;
    if (!statistics.length) {
        container.innerHTML = empty('Monthly appointment activity will appear after the first booking.');
        return;
    }
    const maximum = Math.max(...statistics.map(item => item.appointments), 1);
    container.innerHTML = statistics.slice(-12).map(item => {
        const label = new Intl.DateTimeFormat('en', { month: 'short', year: 'numeric' })
            .format(new Date(item.year, item.month - 1, 1));
        return `<div class="statistics-row"><span>${escapeHtml(label)}</span><div class="statistics-track"><i style="width:${Math.max(4, item.appointments / maximum * 100)}%"></i></div><strong>${item.appointments}</strong></div>`;
    }).join('');
}

const doctorDialog = document.querySelector('[data-doctor-dialog]');
document.querySelector('[data-open-doctor-form]')?.addEventListener('click', () => doctorDialog.showModal());
doctorDialog?.querySelector('[data-dialog-close]')?.addEventListener('click', () => doctorDialog.close());
document.querySelector('[data-doctor-form]')?.addEventListener('submit', async event => {
    event.preventDefault(); const form=event.currentTarget;if(!form.reportValidity())return;
    const data=Object.fromEntries(new FormData(form));
    data.rating=Number(data.rating);data.profileImageUrl=data.profileImageUrl.trim() || null;data.availableTimes=data.availableTimes.split(',').map(item=>item.trim()).filter(Boolean);
    try{await request('/doctor',{method:'POST',body:JSON.stringify(data)});doctorDialog.close();form.reset();toast('Doctor added.');loadAdminDashboard();}
    catch(error){const element=document.querySelector('[data-doctor-error]');element.textContent=error.message;element.hidden=false;}
});

async function loadDoctorSchedule() {
    const container=document.querySelector('[data-doctor-schedule]');
    const date=document.querySelector('[data-appointment-date]').value || today();
    const patient=document.querySelector('[data-patient-filter]').value.trim();
    try {
        const result=await request(`/appointments?date=${encodeURIComponent(date)}&patientName=${encodeURIComponent(patient)}`);
        const items=result.flat ? result.flat() : [];
        container.innerHTML=items.length ? items.sort((a,b)=>new Date(a.appointmentTime)-new Date(b.appointmentTime)).map(item=>`<article class="schedule-item">
            <div class="schedule-time">${escapeHtml(item.appointmentTimeOnly || new Date(item.appointmentTime).toLocaleTimeString([],{hour:'2-digit',minute:'2-digit'}))}</div>
            <div><h3>${escapeHtml(item.patientName)}</h3><p>${escapeHtml(item.patientEmail)} · ${escapeHtml(item.patientPhone || 'No phone')}</p><p>${escapeHtml(item.patientAddress || '')}</p></div>
            <button class="button button-secondary" data-prescription="${item.id}" data-patient-name="${escapeHtml(item.patientName)}">Prescription</button></article>`).join('') : empty('No appointments match this date and patient.');
        container.onclick = event => {
            const button = event.target.closest('[data-prescription]');
            if (button) openPrescription(button.dataset.prescription, button.dataset.patientName);
        };
    } catch(error){if(error.status===401)return logoutExpired();container.innerHTML=empty(error.message);}
}
const profileDialog=document.querySelector('[data-profile-dialog]');
document.querySelector('[data-open-profile]')?.addEventListener('click',async()=>{
    try{
        const doctor=await request('/doctor/me');
        const form=document.querySelector('[data-profile-form]');
        form.elements.specialty.value=doctor.specialty || '';
        form.elements.phone.value=doctor.phone || '';
        form.elements.profileImageUrl.value=doctor.profileImageUrl || '';
        form.elements.availableTimes.value=(doctor.availableTimes || []).join(', ');
        profileDialog.showModal();
    }catch(error){toast(error.message);}
});
profileDialog?.querySelector('[data-dialog-close]')?.addEventListener('click',()=>profileDialog.close());
document.querySelector('[data-profile-form]')?.addEventListener('submit',async event=>{
    event.preventDefault();const form=event.currentTarget;if(!form.reportValidity())return;
    const data=Object.fromEntries(new FormData(form));
    data.profileImageUrl=data.profileImageUrl.trim() || null;
    data.availableTimes=data.availableTimes.split(',').map(value=>value.trim()).filter(Boolean);
    try{await request('/doctor/me',{method:'PUT',body:JSON.stringify(data)});profileDialog.close();toast('Profile and availability updated.');}
    catch(error){const element=document.querySelector('[data-profile-error]');element.textContent=error.message;element.hidden=false;}
});
const prescriptionDialog=document.querySelector('[data-prescription-dialog]');
const prescriptionForm=document.querySelector('[data-prescription-form]');
async function openPrescription(appointmentId,patientName){
    prescriptionForm.reset();prescriptionForm.elements.appointmentId.value=appointmentId;prescriptionForm.elements.patientName.value=patientName;
    const history=document.querySelector('[data-prescription-history]');history.innerHTML='Loading existing prescriptions…';
    prescriptionDialog.showModal();
    try{
        const result=await request(`/prescription/${appointmentId}`);const items=result.data || [];
        history.innerHTML=items.length ? items.map(item=>`<article><strong>${escapeHtml(item.medication)}</strong><span>${escapeHtml(item.doctorNotes || 'No notes')}</span></article>`).join('') : '<span>No prescription recorded for this appointment.</span>';
    }catch(error){history.textContent=error.message;}
}
prescriptionDialog?.querySelector('[data-dialog-close]')?.addEventListener('click',()=>prescriptionDialog.close());
prescriptionForm?.addEventListener('submit',async event=>{
    event.preventDefault();if(!prescriptionForm.reportValidity())return;const data=Object.fromEntries(new FormData(prescriptionForm));data.appointmentId=Number(data.appointmentId);
    try{await request('/prescription',{method:'POST',body:JSON.stringify(data)});prescriptionDialog.close();toast('Prescription saved.');}
    catch(error){const element=document.querySelector('[data-prescription-error]');element.textContent=error.message;element.hidden=false;}
});
function logoutExpired(){ localStorage.clear();window.location.replace('/#access'); }

if(page==='patient') loadPatientDashboard();
if(page==='admin') loadAdminDashboard();
if(page==='doctor') {
    document.querySelector('[data-appointment-date]').value=today();
    document.querySelector('[data-load-schedule]').addEventListener('click',loadDoctorSchedule);
    loadDoctorSchedule();
}
