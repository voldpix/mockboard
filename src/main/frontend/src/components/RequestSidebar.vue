<script setup>
import {ref} from 'vue'

const emit = defineEmits(['view-log'])

const logs = ref([
    {id: 1, method: 'POST', path: '/api/v1/login', status: 200, matched: true, time: '14:20:01'},
    {id: 2, method: 'GET', path: '/users/123', status: 404, matched: false, time: '14:19:55'},
    {id: 3, method: 'GET', path: '/products', status: 200, matched: false, time: '14:18:22'},
    {id: 4, method: 'PUT', path: '/settings', status: 500, matched: true, time: '14:15:10'},
])

const handleLogClick = (log) => {
    emit('view-log', log)
}
</script>

<template>
    <aside class="sidebar-fixed">
        <div class="p-3 border-bottom bg-light d-flex justify-content-between align-items-center">
            <span class="text-uppercase text-muted text-xs fw-bold">Live Requests</span>

            <div class="d-flex align-items-center text-success fw-bold text-xs">
                <span class="spinner-grow spinner-grow-sm me-1" role="status" aria-hidden="true"></span>
                LISTENING
            </div>
        </div>

        <div class="list-group list-group-flush">
            <a v-for="log in logs" href="#" class="list-group-item list-group-item-action log-item matched p-3"
               onclick="showView('log-detail-1')">
                <div class="d-flex w-100 justify-content-between mb-1">
                    <span class="badge bg-success badge-method">{{ log.method }}</span>
                    <small class="text-muted font-mono">{{ log.time }}</small>
                </div>
                <div class="font-mono text-truncate mb-1 fw-bold">{{ log.path }}</div>
                <div class="d-flex align-items-center gap-2">
                    <span v-if="log.matched" class="badge bg-light text-success border border-success text-xs">
                        <i class="bi bi-check-circle-fill me-1"></i>Matched
                    </span>
                    <span v-else class="badge bg-light text-warning border border-warning text-xs">
                        <i class="bi bi-exclamation-triangle-fill me-1"></i>No Match
                    </span>
                    <span class="badge bg-light text-dark border text-xs ms-auto">{{ log.status }}</span>
                </div>
            </a>
        </div>
    </aside>
</template>