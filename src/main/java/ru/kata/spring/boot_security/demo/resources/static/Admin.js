let loadUserId = null;
let loadUserEditId = null;
let oldPassword = null;

let editModal;
let deleteModal;

function loadUser() {
    fetch('/api/loadUser')
        .then(response => response.json())
        .then(user => {
            const loadUserEmail = document.getElementById('loadUserEmail');
            loadUserEmail.textContent = user.email;
            loadUserEmail.dataset.userId = user.id;
            document.getElementById('loadUserRoles')
                .textContent = user.formattedRoles;
        })
        .catch(error => {
            console.error('Error loading', error);
        })
}

function loadUsers() {
    fetch('/api/users', {
        credentials: "include"
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Response not OK: ${response.status} ${response.statusText}`);
            }
            return response.json();
        })
        .then(users => {
            renderUsersTable(users);
        })
        .catch(error => {
            console.error('Error loading', error)
        });
}

document.getElementById('logoutForm')
    .addEventListener('submit', async (event) => {
        event.preventDefault();
        try {
            const response = await fetch('/logout', {
                method: 'POST',
                credentials: 'include'
            })

            if (response.ok) {
                window.location.href = '/login';
            }
        } catch (error) {
            console.error('Error out', error)
        }
    });

function createCell(content) {
    const td = document.createElement('td');
    td.textContent = content;
    return td;
}

function renderUsersTable(users) {
    console.log('Пользователи, пришедшие с сервера:', users);
    const tb = document.getElementById('usersTableBody');
    tb.innerHTML = '';
    users.forEach(user => {
        const tr = document.createElement('tr');
        tr.appendChild(createCell(user.id));
        tr.appendChild(createCell(user.name));
        tr.appendChild(createCell(user.surname));
        tr.appendChild(createCell(user.age));
        tr.appendChild(createCell(user.email));
        tr.appendChild(createCell(user.formattedRoles));

        const editTd = document.createElement('td');
        const editButton = document.createElement('button');
        editButton.className = 'btn btn-primary btn-sm edit-btn';
        editButton.textContent = 'Edit';
        editButton.onclick = () => openEditModal(user.id);
        editTd.appendChild(editButton);
        tr.appendChild(editTd);

        const deleteTd = document.createElement('td');
        const deleteButton = document.createElement('button');
        deleteButton.className = 'btn btn-danger btn-sm delete-btn';
        deleteButton.textContent = 'Delete';
        deleteButton.onclick = () => openDeleteModal(user.id);
        deleteTd.appendChild(deleteButton);
        tr.appendChild(deleteTd);
        tb.appendChild(tr);
    });
}

function loadUserInfo() {
    fetch('/api/loadUser', {
        credentials: 'include'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(user => {
            const tb = document
                .getElementById('loadUserTableBody');
            tb.innerHTML = `                               
            <tr>
                <td>${user.id}</td>                            
                <td>${user.name}</td>                          
                <td>${user.surname}</td>                       
                <td>${user.age}</td>                           
                <td>${user.email}</td>                         
                <td>${user.formattedRoles}</td>               
            </tr>
        `;
        })
        .catch(error => {
            console.error('Error loading user info:', error);
        });
}

async function loadRoles() {
    try {
        const response = await fetch('/api/roles', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const roles = await response.json();
        return roles;
    } catch (error) {
        console.error('Error loading roles:', error);
        return [];
    }
}

async function openEditModal(userId) {
    try {
        const response = await fetch(`/api/users/${userId}`, {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const user = await response.json();
        loadUserEditId = userId;
        oldPassword = null;

        const form = document.getElementById('editUserForm');
        form.querySelector('input[type="hidden"][name="id"]').value = userId;
        form.querySelector('input[type="text"][name="id"]').value = userId;
        form.querySelector('input[name="firstName"]').value = user.name;
        form.querySelector('input[name="lastName"]').value = user.surname;
        form.querySelector('input[name="age"]').value = user.age;
        form.querySelector('input[name="email"]').value = user.email;


        const roleSelect = form.querySelector('select[name="roles"]');
        if (roleSelect && user.roles) {
            await loadRolesToSelect();

            Array.from(roleSelect.options).forEach(option => {
                option.selected = user.roles.some(userRole => userRole.id === parseInt(option.value));
            });
        }

        editModal.show();
    } catch (error) {
        console.error('Error opening edit modal:', error);
        alert('Error loading user data. Please try again.');
    }
}

async function openDeleteModal(userId) {
    try {
        const response = await fetch(`/api/users/${userId}`, {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const user = await response.json();
        loadUserId = userId;

        const form = document.getElementById('deleteUserForm');
        form.querySelector('input[type="hidden"][name="id"]').value = userId;
        form.querySelector('input[type="text"][name="id"]').value = userId;
        form.querySelector('input[name="firstName"]').value = user.name;
        form.querySelector('input[name="lastName"]').value = user.surname;
        form.querySelector('input[name="age"]').value = user.age;
        form.querySelector('input[name="email"]').value = user.email;

        deleteModal.show();
    } catch (error) {
        console.error('Error opening delete modal:', error);
        alert('Error loading user data. Please try again.');
    }
}

async function handleEditUserSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const userId = formData.get('id');
    const loadUserId = document.getElementById('loadUserEmail').dataset.userId;

    const rolesSelect = event.target.querySelector('select[name="roles"]');
    const selectedRoles = Array.from(rolesSelect.selectedOptions).map(option => {
        return {
            id: parseInt(option.value),
            name: 'ROLE_' + option.textContent
        };
    });


    const password = formData.get('password') || oldPassword;

    const userData = {
        id: parseInt(userId),
        email: formData.get('email'),
        name: formData.get('firstName'),
        surname: formData.get('lastName'),
        age: parseInt(formData.get('age')),
        password: password ? password : null,
        roles: selectedRoles
    };


    console.log(JSON.stringify(userData));

    try {
        const response = await fetch(`/api/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error(`Error updating user: ${response.statusText}`);
        }

        editModal.hide();
        await loadUsers();

        if (userId === loadUserId) {
            const hasAdminRole = selectedRoles.some(role => role.name === 'ADMIN' || role.name === 'ROLE_ADMIN');

            if (!hasAdminRole) {
                window.location.href = '/user';
            } else {
                await loadUserInfo();
            }
        }
    } catch (error) {
        console.error('Error updating user:', error);
        alert('Error updating user. Please try again.');
    }
}

async function handleDeleteUserSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const userId = formData.get('id');

    try {
        const response = await fetch(`/api/users/${userId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Error deleting user');
        }

        deleteModal.hide();

        await loadUsers();

        if (userId === document.getElementById('loadUserEmail').dataset.userId) {
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        alert('Error deleting user. Please try again.');
    }
}

async function handleNewUserSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const rolesSelect = event.target.querySelector('select[name="roles"]');

    const user = {
        name: formData.get('firstName'),
        surname: formData.get('lastName'),
        age: parseInt(formData.get('age')),
        email: formData.get('email'),
        password: formData.get('password'),
        roles: Array.from(rolesSelect.selectedOptions).map(option => {
            return {
                id: parseInt(option.value),
                name: 'ROLE_' + option.textContent
            };
        })
    };

    try {
        const response = await fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(user)
        });

        if (!response.ok) {
            throw new Error('Error creating user');
        }

        event.target.reset();
        await loadUsers();

        const usersTableTab = document.querySelector('a[href="#usersTable"]');
        usersTableTab.click();
    } catch (error) {
        console.error('Error creating user:', error);
        alert('Error creating user. Please try again.');
    }
}

async function loadRolesToSelect() {
    try {
        const response = await fetch('/api/roles', {
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to load roles');
        }

        const roles = await response.json();

        // Загружаем роли в форму создания пользователя
        const newUserRolesSelect = document.querySelector('#newUserForm select[name="roles"]');
        if (newUserRolesSelect) {
            newUserRolesSelect.innerHTML = '';
            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.textContent = role.name.replace('ROLE_', '');
                newUserRolesSelect.appendChild(option);
            });
        }

        // Загружаем роли в форму редактирования пользователя
        const editUserRolesSelect = document.querySelector('#editUserForm select[name="roles"]');
        if (editUserRolesSelect) {
            editUserRolesSelect.innerHTML = '';
            roles.forEach(role => {
                const option = document.createElement('option');
                option.value = role.id;
                option.textContent = role.name.replace('ROLE_', '');
                editUserRolesSelect.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error loading roles:', error);
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    try {
        editModal = new bootstrap.Modal(document.getElementById('editModal'));
        deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));

        await loadUser();
        await loadUsers();
        await loadUserInfo();
        await loadRolesToSelect();

        const editUserForm = document.getElementById('editUserForm');
        const deleteUserForm = document.getElementById('deleteUserForm');
        const newUserForm = document.getElementById('newUserForm');

        if (editUserForm) {
            editUserForm.addEventListener('submit', handleEditUserSubmit);
        }

        if (deleteUserForm) {
            deleteUserForm.addEventListener('submit', handleDeleteUserSubmit);
        }

        if (newUserForm) {
            newUserForm.addEventListener('submit', handleNewUserSubmit);
        }
    } catch (error) {
        console.error('Error initializing page:', error);
    }
});



















