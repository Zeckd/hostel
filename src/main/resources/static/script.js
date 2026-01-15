const API_URL = 'http://localhost:8080/api';
let globalRooms = [];
let globalResidents = [];

document.addEventListener('DOMContentLoaded', () => {
    loadAllData();
    setupForms();
});

async function apiRequest(endpoint, method = 'GET', body = null) {
    try {
        const options = { method, headers: { 'Content-Type': 'application/json' } };
        if (body) options.body = JSON.stringify(body);
        const response = await fetch(`${API_URL}${endpoint}`, options);
        if (!response.ok) throw new Error(await response.text());
        const text = await response.text();
        return text ? JSON.parse(text) : true;
    } catch (e) {
        console.error(e);
        alert("Ошибка: " + e.message);
        return null;
    }
}

async function loadAllData() {
    const [rooms, residents] = await Promise.all([
        apiRequest('/accommodation/get/all'),
        apiRequest('/resident/getAll')
    ]);
    if (rooms) globalRooms = rooms;
    if (residents) globalResidents = residents;
    updateDashboard();
    renderRooms();
    renderResidents();
}

// === ОТРИСОВКА ===
function renderRooms() {
    const grid = document.getElementById('rooms-grid');
    const select = document.getElementById('select-room-resident');
    if(!grid || !select) return;

    grid.innerHTML = '';
    select.innerHTML = '<option value="" disabled selected>Выберите комнату</option>';

    globalRooms.forEach(room => {
        const occupied = room.residents ? room.residents.length : 0;
        const isFull = occupied >= room.maxResidents;

        const opt = document.createElement('option');
        opt.value = room.id;
        opt.textContent = `${room.name} (${occupied}/${room.maxResidents})`;
        if(isFull) opt.disabled = true;
        select.appendChild(opt);

        const card = document.createElement('div');
        card.className = 'card room-card';
        card.innerHTML = `
            <div class="room-top">
                <h3>${room.name} <span class="badge ${room.type === 'APARTMENT' ? 'badge-blue' : 'badge-gray'}">${room.type}</span></h3>
                <div class="room-actions">
                    <button class="action-btn" onclick="prepareEditRoom(${room.id})">✏️</button>
                    <button class="action-btn btn-del" onclick="deleteRoom(${room.id})">&times;</button>
                </div>
            </div>
            <p><b>${room.perPersonPrice}</b> сом/чел</p>
            <div class="progress-bar"><div class="fill" style="width: ${(occupied/room.maxResidents)*100}%"></div></div>
            <small>Занято: ${occupied} из ${room.maxResidents}</small>
        `;
        grid.appendChild(card);
    });
}

function renderResidents() {
    const tbody = document.getElementById('residents-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    globalResidents.forEach(res => {
        const room = globalRooms.find(r => r.id === res.accommodationId);
        const monthlyRate = room ? (room.perPersonPrice || 0) : 0;
        const paidThisMonth = (res.payments || []).reduce((sum, p) => sum + p.amount, 0); // Упрощено для примера
        const isFullyPaid = paidThisMonth >= monthlyRate;

        const hasCollateral = res.collateral && res.collateral.description;
        const isReturned = res.collateral && res.collateral.returned;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><b>${res.fullName}</b><br><small>${res.phoneNumber}</small></td>
            <td>${room ? room.name : '---'}</td>
            <td>
                <span class="badge ${isFullyPaid ? 'badge-green' : 'badge-red'}">${isFullyPaid ? 'Оплачено' : 'Долг'}</span>
                <div class="amount-progress">${paidThisMonth} / ${monthlyRate}</div>
            </td>
            <td>
                ${hasCollateral ? `
                    <div style="display:flex; align-items:center; gap:5px">
                        <span class="badge ${isReturned ? 'badge-gray' : 'badge-blue'}" onclick="toggleCollateral(${res.id}, ${!isReturned})" style="cursor:pointer">
                            ${isReturned ? '✅ Возврат' : '📦 У нас'}
                        </span>
                        <button onclick="deleteCollateral(${res.id})" style="border:none;background:none;color:red;cursor:pointer">&times;</button>
                    </div>
                ` : '---'}
            </td>
            <td>
                <div class="actions-group">
                    <button class="action-btn" onclick="showResidentDetails(${res.id})">👁️</button>
                    <button class="action-btn" onclick="prepareEditResident(${res.id})">✏️</button>
                    <button class="action-btn" onclick="openPayModal(${res.id}, '${res.fullName}')">💰</button>
                    <button class="action-btn" onclick="openColModal(${res.id}, '${res.fullName}')">📦</button>
                    <button class="action-btn btn-del" onclick="deleteResident(${res.id})">🗑️</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// === УПРАВЛЕНИЕ ФОРМАМИ ===
function setupForms() {
    // Житель
    document.getElementById('form-resident').onsubmit = async (e) => {
        e.preventDefault();
        const f = e.target;
        const id = f.dataset.editId;
        const body = {
            fullName: f.fullName.value,
            phoneNumber: f.phoneNumber.value,
            arrivalDate: f.arrivalDate.value,
            accommodationId: parseInt(f.accommodationId.value)
        };
        if (await apiRequest(id ? `/resident/${id}` : '/resident/create', id ? 'PATCH' : 'POST', body)) {
            closeAllModals(); loadAllData();
        }
    };

    // Комната
    document.getElementById('form-accommodation').onsubmit = async (e) => {
        e.preventDefault();
        const f = e.target;
        const id = f.dataset.editId;
        const body = {
            name: f.name.value,
            type: f.type.value,
            maxResidents: parseInt(f.maxResidents.value),
            perPersonPrice: parseInt(f.perPersonPrice.value),
            fullRentPrice: parseInt(f.fullRentPrice.value)
        };
        if (await apiRequest(id ? `/accommodation/${id}` : '/accommodation/create', id ? 'PATCH' : 'POST', body)) {
            closeAllModals(); loadAllData();
        }
    };

    // Оплата и Залог
    document.getElementById('form-payment').onsubmit = async (e) => {
        e.preventDefault();
        const body = { residentId: parseInt(e.target.residentId.value), amount: parseInt(e.target.amount.value) };
        if (await apiRequest('/payment/create', 'POST', body)) { closeAllModals(); loadAllData(); }
    };

    document.getElementById('form-collateral').onsubmit = async (e) => {
        e.preventDefault();
        const body = { residentId: parseInt(e.target.residentId.value), description: e.target.description.value };
        if (await apiRequest('/collateral/create', 'POST', body)) { closeAllModals(); loadAllData(); }
    };
}

// === ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ===
function openAddRoomModal() {
    const f = document.getElementById('form-accommodation');
    f.reset(); delete f.dataset.editId;
    document.getElementById('modal-room-title').innerText = "Создать комнату";
    openModal('modal-accommodation');
}

function prepareEditRoom(id) {
    const room = globalRooms.find(r => r.id === id);
    const f = document.getElementById('form-accommodation');
    f.name.value = room.name;
    f.type.value = room.type;
    f.maxResidents.value = room.maxResidents;
    f.perPersonPrice.value = room.perPersonPrice;
    f.fullRentPrice.value = room.fullRentPrice;
    f.dataset.editId = id;
    document.getElementById('modal-room-title').innerText = "Изменить комнату";
    openModal('modal-accommodation');
}

function openAddResidentModal() {
    const f = document.getElementById('form-resident');
    f.reset(); delete f.dataset.editId;
    document.getElementById('modal-resident-title').innerText = "Новый житель";
    openModal('modal-resident');
}

function prepareEditResident(id) {
    const res = globalResidents.find(r => r.id === id);
    const f = document.getElementById('form-resident');
    f.fullName.value = res.fullName;
    f.phoneNumber.value = res.phoneNumber;
    f.arrivalDate.value = res.arrivalDate || '';
    f.accommodationId.value = res.accommodationId;
    f.dataset.editId = id;
    document.getElementById('modal-resident-title').innerText = "Изменить жителя";
    openModal('modal-resident');
}

async function deleteRoom(id) { if(confirm("Удалить?")) { await apiRequest(`/accommodation/delete/${id}`, 'DELETE'); loadAllData(); } }
async function deleteResident(id) { if(confirm("Удалить?")) { await apiRequest(`/resident/delete/${id}`, 'DELETE'); loadAllData(); } }
async function toggleCollateral(resId, status) { await apiRequest(`/collateral/${resId}?returned=${status}`, 'PATCH'); loadAllData(); }
async function deleteCollateral(resId) { if(confirm("Удалить залог?")) { await apiRequest(`/collateral/delete/${resId}`, 'DELETE'); loadAllData(); } }

function openModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeAllModals() { document.querySelectorAll('.modal').forEach(m => m.style.display = 'none'); }
function switchPage(id) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById('page-' + id).classList.add('active');
    document.querySelectorAll('.menu-item').forEach(m => m.classList.remove('active'));
    event.currentTarget.classList.add('active');
}

function openPayModal(id, name) { document.getElementById('pay-res-id').value = id; document.getElementById('pay-res-name').innerText = name; openModal('modal-payment'); }
function openColModal(id, name) { document.getElementById('col-res-id').value = id; document.getElementById('col-res-name').innerText = name; openModal('modal-collateral'); }

function updateDashboard() {
    document.getElementById('stat-total-residents').innerText = globalResidents.length;
    document.getElementById('stat-total-money').innerText = globalResidents.reduce((s, r) => s + (r.payments || []).reduce((ss, p) => ss + p.amount, 0), 0) + " c";
    document.getElementById('stat-free-places').innerText = globalRooms.reduce((s, r) => s + (r.maxResidents - (r.residents?.length || 0)), 0);
}

function showResidentDetails(id) {
    const res = globalResidents.find(r => r.id === id);
    alert(`Житель: ${res.fullName}\nТелефон: ${res.phoneNumber}\nЗаезд: ${res.arrivalDate}`);
}