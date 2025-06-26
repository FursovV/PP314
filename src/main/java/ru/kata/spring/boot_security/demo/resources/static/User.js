document.getElementById('logoutForm')
    .addEventListener('submit', async (event) => {
        event.preventDefault();
        try {
            const response = await fetch('/logout', {
                method: 'POST',
                credentials: 'include'
            });

            if (response.ok) {
                window.location.href = '/login';
            }
        } catch (error) {
            console.error('Error logging out:', error);
        }
    });

function loadUser() {
    fetch('/api/loadUser')
        .then(response => response.json())
        .then(user => {
            const loadUserEmail = document
                .getElementById('loadUserEmail');
            loadUserEmail.textContent = user.email;
            loadUserEmail.dataset.userId = user.id;
            document.getElementById('loadUserRoles')
                .textContent = user.formattedRoles;
            renderUsersTable(user);
        })
        .catch(error => {
            console.error('Error loading user:', error);
        })
}

function createCell(content) {
    const td = document.createElement('td');
    td.textContent = content;
    return td;
}

function renderUsersTable(user) {
    const tb = document.getElementById('usersTableBodyById');
    tb.innerHTML = '';

    const tr = document.createElement('tr');
    tr.appendChild(createCell(user.id));
    tr.appendChild(createCell(user.name));
    tr.appendChild(createCell(user.surname));
    tr.appendChild(createCell(user.age));
    tr.appendChild(createCell(user.email));
    tr.appendChild(createCell(user.formattedRoles));
    tb.appendChild(tr);
}

document.addEventListener('DOMContentLoaded', async () => {
    try {
        await loadUser();
    } catch (error) {
        console.error('Error initializing page:', error);
    }
});