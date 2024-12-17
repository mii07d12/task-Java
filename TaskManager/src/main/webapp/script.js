console.log("script.js is loaded");

function showDetails(id, title, description, priority, category, deadline) {
    console.log("showDetails function called with id:", id);
    document.getElementById('taskId').value = id;
    document.getElementById('editTitle').value = title;
    document.getElementById('editDescription').value = description;
    document.getElementById('editPriority').value = priority;
    document.getElementById('editCategory').value = category;
    document.getElementById('editDeadline').value = deadline ? deadline.replace(' ', 'T') : '';
    document.getElementById('popupTitle').innerText = title;
    document.getElementById('popupDescription').innerText = description;
    document.getElementById('taskPopup').style.display = 'block';
}

function closePopup() {
    document.getElementById('taskPopup').style.display = 'none';
    console.log("Popup closed");
}

// 編集後にポップアップを閉じる
document.getElementById('editForm').addEventListener('submit', function() {
    closePopup();
    console.log("Form submitted");
});

function toggleCompletion(id, completed) {
    var form = document.createElement('form');
    form.method = 'get';
    form.action = 'task';

    var idField = document.createElement('input');
    idField.type = 'hidden';
    idField.name = 'id';
    idField.value = id;
    form.appendChild(idField);

    var completedField = document.createElement('input');
    completedField.type = 'hidden';
    completedField.name = 'completed';
    completedField.value = completed;
    form.appendChild(completedField);

    var actionField = document.createElement('input');
    actionField.type = 'hidden';
    actionField.name = 'action';
    actionField.value = 'toggleComplete';
    form.appendChild(actionField);

    document.body.appendChild(form);

    // フォーム送信前に行のクラスを変更するイベントリスナーを設定
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // デフォルトのフォーム送信を防ぐ

        var xhr = new XMLHttpRequest();
        xhr.open('GET', form.action + '?' + new URLSearchParams(new FormData(form)).toString(), true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                var row = document.getElementById('task-row-' + id);
                if (completed) {
                    row.classList.remove('grayed-out');
                } else {
                    row.classList.add('grayed-out');
                }
                form.remove(); // フォームを削除
            }
        };
        xhr.send();
    });

    form.submit();
}

function deleteTask(id) {
    var form = document.createElement('form');
    form.method = 'get';
    form.action = 'task';

    var idField = document.createElement('input');
    idField.type = 'hidden';
    idField.name = 'id';
    idField.value = id;
    form.appendChild(idField);

    var actionField = document.createElement('input');
    actionField.type = 'hidden';
    actionField.name = 'action';
    actionField.value = 'delete';
    form.appendChild(actionField);

    document.body.appendChild(form);
    form.submit();
}

function showCategoryPopup() {
    document.getElementById('categoryPopup').style.display = 'block';
}

function closeCategoryPopup() {
    document.getElementById('categoryPopup').style.display = 'none';
}

function resetToDefault() {
    document.cookie = "theme=light; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    document.cookie = "accentColor=#ADD8E6; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";
    applyTheme('light');
    applyAccentColor('#ADD8E6');
    document.querySelector('input[name="theme"][value="light"]').checked = true;
    document.querySelector('input[name="accentColor"]').value = '#ADD8E6';
}

function switchTheme(element) {
    const theme = element.value;
    document.cookie = "theme=" + theme + "; path=/";
    applyTheme(theme);
}

function switchAccentColor(element) {
    const color = element.value;
    document.cookie = "accentColor=" + color + "; path=/";
    applyAccentColor(color);
}

function loadThemeAndAccentColor() {
    const theme = getCookie("theme");
    const accentColor = getCookie("accentColor");
    if (theme) {
        applyTheme(theme);
    } else {
        applyTheme("light");
    }
    if (accentColor) {
        applyAccentColor(accentColor);
    } else {
        applyAccentColor("#ADD8E6");
    }
}

function applyTheme(theme) {
    document.body.classList.remove('light-theme', 'dark-theme');
    document.body.classList.add(theme + '-theme');
    document.querySelector(`input[name="theme"][value="${theme}"]`).checked = true;
}

function applyAccentColor(color) {
    document.documentElement.style.setProperty('--accent-color', color);
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// ページロード時にテーマと差し色を適用
window.onload = loadThemeAndAccentColor;

// フォームをクリアする関数
function clearForm() {
    document.getElementById('taskForm').reset();
}
