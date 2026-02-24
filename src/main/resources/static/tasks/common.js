function getTaskKey() {
    const parts = window.location.pathname.split('/');
    return parts[parts.length - 1];
}

/**
 * Fetch process variables visible to the current user task from Camunda.
 * Returns a plain object: { varName: value, ... }
 */
async function getTaskVariables() {
    const taskKey = getTaskKey();
    const resp = await fetch('/api/tasks/' + taskKey + '/variables');
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || 'Failed to fetch task variables');
    }
    return resp.json();
}

/**
 * Complete Camunda User Task with variables
 * @param variables
 * @returns {Promise<void>}
 */
async function completeTask(variables) {
    const taskKey = getTaskKey();
    const resp = await fetch('/api/tasks/' + taskKey + '/complete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(variables || {})
    });
    if (!resp.ok) {
        const text = await resp.text();
        throw new Error(text || 'Failed to complete task');
    }
}

function showToast(message, type) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast ' + type;
    setTimeout(() => { toast.className = 'toast'; }, 4000);
}

function showCompleted() {
    document.querySelectorAll('.card').forEach(c => c.style.display = 'none');
    const div = document.createElement('div');
    div.className = 'card';
    div.innerHTML = '<h2 style="color:#4caf50">Task Completed</h2><p>This task has been completed successfully. You can close this page.</p>';
    document.querySelector('.container').appendChild(div);
}

document.addEventListener('DOMContentLoaded', function() {
    const el = document.getElementById('task-key-display');
    if (el) el.textContent = 'Task Key: ' + getTaskKey();
});
