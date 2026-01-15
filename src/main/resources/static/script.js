const API_URL = 'http://localhost:8080/api';
let globalRooms = [];
let globalResidents = [];

document.addEventListener('DOMContentLoaded', () => {
    loadAllData();
    setupForms();
});

// === API КЛИЕНТ ===
async function apiRequest(endpoint, method = 'GET', body = null) {
    try {
        const options = { method, headers: { 'Content-Type': 'application/json' } };
        if (body) options.body = JSON.stringify(body);
        const response = await fetch(`${API_URL}${endpoint}`, options);

        // Исправление: более детальная обработка ошибок (например, 404 Not Found)
        if (!response.ok) {
            const errText = await response.text();
            throw new Error(errText || `Ошибка сервера: ${response.status}`);
        }

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
    globalRooms = rooms || [];
    globalResidents = residents || [];
    updateDashboard();
    renderRooms();
    renderResidents();
}

// === ОТРИСОВКА КОМНАТ ===
function renderRooms() {
    const grid = document.getElementById('rooms-grid');
    const select = document.getElementById('select-room-resident');
    if(!grid || !select) return;

    grid.innerHTML = '';
    select.innerHTML = '<option value="" disabled selected>Выберите комнату</option>';

    globalRooms.forEach((room , index) => {
        const occupied = room.residents ? room.residents.length : 0;
        const opt = document.createElement('option');
        opt.value = room.id;
        opt.textContent = `${room.name} (${occupied}/${room.maxResidents})`;
        if(occupied >= room.maxResidents) opt.disabled = true;
        select.appendChild(opt);

        const card = document.createElement('div');
        card.className = 'card room-card clickable';
        card.setAttribute('draggable', 'true');
        card.dataset.index = index;
        card.onclick = (e) => {
            if(!e.target.closest('button')) showRoomDetails(room.id);
        };
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
            <div style="display:flex; justify-content:space-between; align-items:center; margin-top:10px;">
                <small>Занято: ${occupied} / ${room.maxResidents}</small>
                <button class="btn-sm" onclick="openAddResidentToRoom(${room.id})">+ Житель</button>
            </div>
        `;
        addDragAndDropHandlers(card);
        grid.appendChild(card);
    });
}
// === ЛОГИКА ПЕРЕМЕЩЕНИЯ (DRAG & DROP) ===
let dragSrcEl = null;

function addDragAndDropHandlers(el) {
    el.addEventListener('dragstart', function(e) {
        dragSrcEl = this;
        e.dataTransfer.effectAllowed = 'move';
        this.style.opacity = '0.4';
    });

    el.addEventListener('dragover', function(e) {
        if (e.preventDefault) e.preventDefault();
        return false;
    });

    el.addEventListener('dragenter', function() { this.classList.add('over'); });
    el.addEventListener('dragleave', function() { this.classList.remove('over'); });

    el.addEventListener('drop', function(e) {
        if (e.stopPropagation) e.stopPropagation();

        // Внутри el.addEventListener('drop', function(e) ... для комнат
        if (dragSrcEl !== this) {
            const fromIdx = parseInt(dragSrcEl.dataset.index);
            const toIdx = parseInt(this.dataset.index);

            const temp = globalRooms[fromIdx];
            globalRooms.splice(fromIdx, 1);
            globalRooms.splice(toIdx, 0, temp);

            // СОХРАНЯЕМ ПОРЯДОК КОМНАТ
            localStorage.setItem('roomsOrder', JSON.stringify(globalRooms.map(r => r.id)));

            renderRooms();
        }

        return false;
    });

    el.addEventListener('dragend', function() {
        this.style.opacity = '1';
        document.querySelectorAll('.room-card').forEach(card => card.classList.remove('over'));
    });
}

function showRoomDetails(roomId) {
    const room = globalRooms.find(r => r.id === roomId);
    const residents = globalResidents.filter(res => res.accommodationId === roomId);
    const content = document.getElementById('room-details-content');
    content.innerHTML = `
        <h2>${room.name} <small>(${room.type})</small></h2>
        <div class="info-grid">
            <div class="info-section">
                <h4>Информация</h4>
                <p>Емкость: ${room.maxResidents} мест</p>
                <p>Цена/чел: ${room.perPersonPrice} сом</p>
                <p>Полная аренда: ${room.fullRentPrice} сом</p>
            </div>
        </div>
        <hr>
        <h4>Жители комнаты:</h4>
        <div class="table-container">
            <table>
                ${residents.length ? residents.map(r => `
                    <tr>
                        <td><b>${r.fullName}</b></td>
                        <td align="right"><button class="action-btn" onclick="showResidentDetails(${r.id})">👁️</button></td>
                    </tr>
                `).join('') : '<tr><td>Пусто</td></tr>'}
            </table>
        </div>
        <button class="btn-primary" style="margin-top:20px; width:100%" onclick="openAddResidentToRoom(${room.id})">+ Поселить сюда</button>
    `;
    openModal('modal-room-details');
}

// === ЛОГИКА ОПЛАТ И ЖИТЕЛЕЙ ===
function getMonthlyPaymentStats(res, room) {
    const price = room ? room.perPersonPrice : 0;
    const now = new Date();
    const currentMonthStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;

    const paidThisMonth = (res.payments || [])
        .filter(p => {
            const pDate = new Date(p.paidAt);
            return `${pDate.getFullYear()}-${String(pDate.getMonth() + 1).padStart(2, '0')}` === currentMonthStr;
        })
        .reduce((sum, p) => sum + p.amount, 0);

    return { paid: paidThisMonth, total: price, isFullyPaid: price > 0 && paidThisMonth >= price };
}

function renderResidents() {
    const tbody = document.getElementById('residents-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    globalResidents.forEach((res,index) => {
        const room = globalRooms.find(r => r.id === res.accommodationId);
        const stats = getMonthlyPaymentStats(res, room);
        const hasCollateral = res.collateral && res.collateral.description;
        const isReturned = res.collateral && res.collateral.returned;


        const tr = document.createElement('tr');
        tr.setAttribute('draggable', 'true'); // Разрешаем перетаскивание строки
        tr.dataset.index = index;
        addResidentDragHandlers(tr);
        tr.innerHTML = `
            <td><b>${res.fullName}</b></td>
            <td>${room ? room.name : '---'}</td>
            <td>
                <span class="badge ${stats.isFullyPaid ? 'badge-green' : (stats.paid > 0 ? 'badge-orange' : 'badge-red')}">
                    ${stats.isFullyPaid ? 'Оплачено' : (stats.paid > 0 ? 'Частично' : 'Долг')}
                </span>
                <div class="amount-progress">${stats.paid} / ${stats.total} сом</div>
            </td>
            <td>
                ${hasCollateral ? `
                    <button class="badge ${isReturned ? 'badge-gray' : 'badge-blue'}" onclick="toggleCollateral(${res.id}, ${!isReturned})">
                        ${isReturned ? '✅ Возвращен' : '📦 У нас'}
                    </button>
                ` : '---'}
            </td>
            <td>
                <div class="actions-group">
                    <button class="action-btn" onclick="showResidentDetails(${res.id})">👁️</button>
                    <button class="action-btn" onclick="openPayModal(${res.id}, '${res.fullName}')">💰</button>
                    <button class="action-btn btn-del" onclick="deleteResident(${res.id})">🗑️</button>
                    <button class="action-btn" onclick="prepareEditResident(${res.id})">✏️</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}
let dragSrcResEl = null;

function addResidentDragHandlers(el) {
    el.addEventListener('dragstart', function(e) {
        dragSrcResEl = this;
        this.classList.add('dragging-row');
    });

    el.addEventListener('dragover', function(e) {
        e.preventDefault();
        return false;
    });

    el.addEventListener('drop', function(e) {
        if (dragSrcResEl !== this) {
            const fromIdx = parseInt(dragSrcResEl.dataset.index);
            const toIdx = parseInt(this.dataset.index);

            // Меняем в массиве
            const temp = globalResidents[fromIdx];
            globalResidents.splice(fromIdx, 1);
            globalResidents.splice(toIdx, 0, temp);

            // Сохраняем новый порядок ID в localStorage
            const newOrder = globalResidents.map(r => r.id);
            localStorage.setItem('residentsOrder', JSON.stringify(newOrder));

            renderResidents();
        }
    });

    el.addEventListener('dragend', function() {
        this.classList.remove('dragging-row');
    });
}
function sortElementsBySavedOrder(elements, storageKey) {
    const savedOrder = JSON.parse(localStorage.getItem(storageKey) || '[]');
    if (savedOrder.length === 0) return elements;

    return [...elements].sort((a, b) => {
        let indexA = savedOrder.indexOf(a.id);
        let indexB = savedOrder.indexOf(b.id);

        // Если элемента нет в сохраненном порядке (например, новый),
        // ставим его в конец (9999)
        if (indexA === -1) indexA = 9999;
        if (indexB === -1) indexB = 9999;

        return indexA - indexB;
    });
}
async function loadAllData() {
    const [rooms, residents] = await Promise.all([
        apiRequest('/accommodation/get/all'),
        apiRequest('/resident/getAll')
    ]);

    // Сохраняем и сортируем комнаты
    const unsortedRooms = rooms || [];
    globalRooms = sortElementsBySavedOrder(unsortedRooms, 'roomsOrder');
    // Если порядка еще нет, сохраним текущий (начальный)
    if (!localStorage.getItem('roomsOrder')) {
        localStorage.setItem('roomsOrder', JSON.stringify(globalRooms.map(r => r.id)));
    }

    // Сохраняем и сортируем жителей
    const unsortedResidents = residents || [];
    globalResidents = sortElementsBySavedOrder(unsortedResidents, 'residentsOrder');
    if (!localStorage.getItem('residentsOrder')) {
        localStorage.setItem('residentsOrder', JSON.stringify(globalResidents.map(r => r.id)));
    }

    updateDashboard();
    renderRooms();
    renderResidents();
}
function prepareEditResident(id) {
    const res = globalResidents.find(r => r.id === id);
    const f = document.getElementById('form-resident');

    f.fullName.value = res.fullName;
    f.phoneNumber.value = res.phoneNumber;
    f.arrivalDate.value = res.arrivalDate ? res.arrivalDate.split('T')[0] : '';
    f.accommodationId.value = res.accommodationId;
    f.dataset.editId = id; // Запоминаем ID для сохранения

    document.getElementById('modal-resident-title').innerText = "Редактировать жителя";
    openModal('modal-resident');
}

function showResidentDetails(resId) {
    const res = globalResidents.find(r => r.id === resId);
    if(!res) return;
    const room = globalRooms.find(r => r.id === res.accommodationId);
    const stats = getMonthlyPaymentStats(res, room);
    const content = document.getElementById('resident-details-content');
    const dateFormatted = res.arrivalDate ? new Date(res.arrivalDate).toLocaleDateString() : 'Не указана';

    content.innerHTML = `
        <h2 xmlns="http://www.w3.org/1999/html">${res.fullName}</h2>
        <div class="info-grid">
            <div class="info-section">
                <h4>📇 Данные</h4>
                <p><b>Телефон:</b> ${res.phoneNumber}</p>
                <p><b>Комната:</b> ${room ? room.name : '---'}</p>
                <p><b>Оплата:</b> ${stats.paid} / ${stats.total}</p>
                <p><b>День приезда:</b> ${dateFormatted}</p>
                
            </div>
            <div class="info-section">
                <h4>📦 Залог</h4>
                ${res.collateral ? `
                    <p>${res.collateral.description}</p>
                    <button class="btn-submit ${res.collateral.returned ? 'btn-gray' : ''}" onclick="toggleCollateral(${res.id}, ${!res.collateral.returned})">
                        ${res.collateral.returned ? 'Вернуть "У нас"' : 'Вернуть залог жителю'}
                    </button>
                    <button class="btn-sm" style="margin-top:10px; color:red" onclick="deleteCollateral(${res.id})">Удалить запись</button>
                ` : `<button class="btn-sm" onclick="openColModal(${res.id}, '${res.fullName}')">+ Добавить залог</button>`}
            </div>
        </div>
        <hr>
        <h4>💰 История платежей</h4>
        <div class="history-list">
            ${(res.payments || []).map(p => `<div class="history-item"><span>${new Date(p.paidAt).toLocaleDateString()}</span><b>+ ${p.amount} сом</b></div>`).join('')}
        </div>
    `;
    openModal('modal-resident-details');
}

// === УДАЛЕНИЕ И ПЕРЕКЛЮЧЕНИЕ (ГЛОБАЛЬНЫЕ) ===
async function deleteRoom(id) { if(confirm("Удалить комнату?")) { if(await apiRequest(`/accommodation/delete/${id}`, 'DELETE')) loadAllData(); } }
async function deleteResident(id) { if(confirm("Удалить жителя?")) { if(await apiRequest(`/resident/delete/${id}`, 'DELETE')) loadAllData(); } }

async function deleteCollateral(resId) {
    if(!confirm("Вы уверены, что хотите ПОЛНОСТЬЮ УДАЛИТЬ запись о залоге?")) return;
    // Путь /collateral/delete/{resId} должен существовать на бэкенде
    if (await apiRequest(`/collateral/delete/${resId}`, 'DELETE')) {
        await loadAllData();
        if (document.getElementById('modal-resident-details').style.display === 'flex') showResidentDetails(resId);
    }
}

async function toggleCollateral(resId, status) {
    if (await apiRequest(`/collateral/${resId}?returned=${status}`, 'PATCH')) {
        await loadAllData();
        if (document.getElementById('modal-resident-details').style.display === 'flex') showResidentDetails(resId);
    }
}

// === ФОРМЫ ===
function setupForms() {
    document.getElementById('form-resident').onsubmit = async (e) => {
        e.preventDefault();
        const f = e.target;
        const id = f.dataset.editId;
        const body = { fullName: f.fullName.value, phoneNumber: f.phoneNumber.value, arrivalDate: f.arrivalDate.value, accommodationId: parseInt(f.accommodationId.value) };
        if (await apiRequest(id ? `/resident/${id}` : '/resident/create', id ? 'PATCH' : 'POST', body)) { closeAllModals(); loadAllData(); }
    };

    document.getElementById('form-accommodation').onsubmit = async (e) => {
        e.preventDefault();
        const f = e.target;
        const id = f.dataset.editId;
        const body = { name: f.name.value, type: f.type.value, maxResidents: parseInt(f.maxResidents.value), perPersonPrice: parseInt(f.perPersonPrice.value), fullRentPrice: parseInt(f.fullRentPrice.value) };
        if (await apiRequest(id ? `/accommodation/${id}` : '/accommodation/create', id ? 'PATCH' : 'POST', body)) { closeAllModals(); loadAllData(); }
    };

    document.getElementById('form-payment').onsubmit = async (e) => {
        e.preventDefault();
        const resId = parseInt(e.target.residentId.value);
        const body = { residentId: resId, amount: parseInt(e.target.amount.value) };
        // Исправление пути: проверьте, что на бэкенде путь именно /payment/create
        if (await apiRequest('/payment/create', 'POST', body)) {
            closeAllModals();
            await loadAllData();
            showResidentDetails(resId);
        }
    };

    document.getElementById('form-collateral').onsubmit = async (e) => {
        e.preventDefault();
        const resId = parseInt(e.target.residentId.value);
        const body = { residentId: resId, description: e.target.description.value };
        if (await apiRequest('/collateral/create', 'POST', body)) {
            closeAllModals();
            await loadAllData();
            showResidentDetails(resId);
        }
    };
}

// === УПРАВЛЕНИЕ МОДАЛКАМИ ===
function openModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeAllModals() { document.querySelectorAll('.modal').forEach(m => m.style.display = 'none'); }
function switchPage(id) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById('page-' + id).classList.add('active');
    document.querySelectorAll('.menu-item').forEach(m => m.classList.remove('active'));
    if(event) event.currentTarget.classList.add('active');
}
function openPayModal(id, name) {
    document.getElementById('pay-res-id').value = id;
    document.getElementById('pay-res-name').innerText = name;
    openModal('modal-payment');
}
function openColModal(id, name) {
    document.getElementById('col-res-id').value = id;
    document.getElementById('col-res-name').innerText = name;
    openModal('modal-collateral');
}
function openAddResidentToRoom(roomId) {
    const f = document.getElementById('form-resident'); f.reset(); delete f.dataset.editId;
    f.accommodationId.value = roomId;
    openModal('modal-resident');
}
function openAddRoomModal() {
    const f = document.getElementById('form-accommodation'); f.reset(); delete f.dataset.editId;
    openModal('modal-accommodation');
}
function prepareEditRoom(id) {
    const room = globalRooms.find(r => r.id === id);
    const f = document.getElementById('form-accommodation');
    f.name.value = room.name; f.type.value = room.type;
    f.maxResidents.value = room.maxResidents; f.perPersonPrice.value = room.perPersonPrice;
    f.fullRentPrice.value = room.fullRentPrice; f.dataset.editId = id;
    openModal('modal-accommodation');
}
function updateDashboard() {
    // 1. Общее количество жильцов
    document.getElementById('stat-total-residents').innerText = globalResidents.length;

    // 2. Расчет кассы ТОЛЬКО за ТЕКУЩИЙ МЕСЯЦ
    const now = new Date();
    const currentMonthStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;

    const totalMoneyThisMonth = globalResidents.reduce((sum, resident) => {
        // Считаем платежи конкретного жителя только за этот месяц
        const residentPaidThisMonth = (resident.payments || [])
            .filter(p => {
                const pDate = new Date(p.paidAt);
                const pMonthStr = `${pDate.getFullYear()}-${String(pDate.getMonth() + 1).padStart(2, '0')}`;
                return pMonthStr === currentMonthStr;
            })
            .reduce((s, p) => s + p.amount, 0);

        return sum + residentPaidThisMonth;
    }, 0);

    // Обновляем текст в кассе
    document.getElementById('stat-total-money').innerText = totalMoneyThisMonth + " сом";

    // 3. Свободные места
    const freePlaces = globalRooms.reduce((s, r) => s + (r.maxResidents - (r.residents?.length || 0)), 0);
    document.getElementById('stat-free-places').innerText = freePlaces;
}
window.onclick = (e) => { if(e.target.classList.contains('modal')) closeAllModals(); };