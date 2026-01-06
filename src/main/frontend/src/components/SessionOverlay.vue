<script setup>
import {ref, onMounted} from 'vue'
import constants from "@/constants.js";

const emit = defineEmits(['session-start', 'session-continue'])
const showModal = ref(false)
const isReturningUser = ref(false)

const boardIdKey = constants.BOARD_ID_LS_KEY;
const ownerTokenKey = constants.OWNER_TOKEN_LS_KEY;
const reaskKey = constants.SESSION_OVERLAY_REASK_TTL_LS_KEY;
const reaskTTL = constants.REASK_TTL

onMounted(() => {
    const existingSession = localStorage.getItem(boardIdKey)
    const lastActive = localStorage.getItem(reaskKey)
    const now = Date.now()

    if (existingSession && lastActive && (now - lastActive) < reaskTTL) {
        updateTimestamp()
        emit('session-continue')
    } else {
        showModal.value = true
        if (existingSession) isReturningUser.value = true
    }
})

const updateTimestamp = () => {
    localStorage.setItem(reaskKey, Date.now().toString())
}

const handleStartNew = () => {
    localStorage.removeItem(boardIdKey)
    localStorage.setItem(boardIdKey, 'new-generated-id-' + Date.now())
    updateTimestamp()

    showModal.value = false
    emit('session-start')
}

const handleContinue = () => {
    updateTimestamp()
    showModal.value = false
    emit('session-continue')
}
</script>

<template>
    <div v-if="true" class="session-overlay">
        <div class="card border-0 shadow-lg overflow-hidden w-100"
             style="max-width: 900px; min-height: 450px; border-radius: 0.75rem;">
            <div class="row g-0 h-100">

                <div class="col-md-5 bg-light border-end d-flex flex-column justify-content-between p-4 p-md-5">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-4 fw-bold">
                            <i class="bi bi-box-seam-fill text-primary"></i> MockBoard<span class="text-muted fw-light">.dev</span>
                        </div>

                        <div class="d-flex flex-column gap-4">
                            <div class="d-flex gap-3">
                                <div class="text-warning mt-1">
                                    <svg class="bi" width="20" height="20" fill="none" stroke="currentColor"
                                         viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                                    </svg>
                                </div>
                                <div>
                                    <h6 class="fw-bold text-dark mb-1" style="font-size: 0.875rem;">Disposable
                                        Workspace</h6>
                                    <p class="text-muted mb-0" style="font-size: 0.75rem; line-height: 1.5;">
                                        Everything is ephemeral. All data is automatically wiped daily at <span
                                        class="font-mono text-dark">03:00 UTC</span>.
                                    </p>
                                </div>
                            </div>

                            <div class="d-flex gap-3">
                                <div class="text-primary mt-1">
                                    <svg class="bi" width="20" height="20" fill="none" stroke="currentColor"
                                         viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M13 10V3L4 14h7v7l9-11h-7z"></path>
                                    </svg>
                                </div>
                                <div>
                                    <h6 class="fw-bold text-dark mb-1" style="font-size: 0.875rem;">Strict Rate
                                        Limits</h6>
                                    <p class="text-muted mb-0" style="font-size: 0.75rem; line-height: 1.5;">
                                        Fair use policy applies. Mock execution and creation are rate-limited per API
                                        Key.
                                    </p>
                                </div>
                            </div>

                            <div class="d-flex gap-3">
                                <div class="text-secondary mt-1">
                                    <svg class="bi" width="20" height="20" fill="none" stroke="currentColor"
                                         viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                              d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                                    </svg>
                                </div>
                                <div>
                                    <div class="d-flex align-items-center gap-2">
                                        <h6 class="fw-bold text-dark mb-1" style="font-size: 0.875rem;">Private
                                            Sandbox</h6>
                                        <span
                                            class="badge bg-secondary-subtle text-secondary border border-secondary-subtle text-uppercase"
                                            style="font-size: 10px;">Share Planned</span>
                                    </div>
                                    <p class="text-muted mb-0" style="font-size: 0.75rem; line-height: 1.5;">
                                        Your mocks are private to this browser session. Sharing capabilities are coming
                                        soon.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="pt-4 border-top mt-4">
                        <div class="d-flex align-items-center gap-2 text-muted font-mono" style="font-size: 0.75rem;">
                            <span class="bg-success rounded-circle d-inline-block spinner-grow spinner-grow-sm"
                                  style="width: 8px; height: 8px; animation-duration: 2s;"></span>
                            SYSTEM ONLINE
                        </div>
                    </div>
                </div>

                <div
                    class="col-md-7 bg-white p-5 d-flex flex-column justify-content-center position-relative bg-grid-pattern">
                    <div v-if="!isReturningUser">
                        <h2 class="fw-bold text-dark mb-2">Start New Session</h2>
                        <p class="text-muted mb-4" style="font-size: 0.875rem;">
                            Generate a unique API key to start mocking. This session will persist in your browser until
                            the daily wipe.
                        </p>

                        <button
                            @click="handleStartNew"
                            class="btn btn-dark w-100 py-3 d-flex align-items-center justify-content-center gap-2 shadow fw-semibold">
                            Generate API Key
                            <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M17 8l4 4m0 0l-4 4m4-4H3"></path>
                            </svg>
                        </button>

                        <p class="text-center text-muted mt-4" style="font-size: 0.75rem;">
                            By continuing, you agree to the <a href="#"
                                                               class="text-decoration-underline text-secondary">fair use
                            policy</a>.
                        </p>
                    </div>

                    <div v-if="isReturningUser">
                        <div
                            class="d-inline-flex align-items-center gap-2 px-3 py-1 rounded-pill bg-success-subtle text-success border border-success-subtle mb-3"
                            style="font-size: 0.75rem;">
                            <svg width="12" height="12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                            </svg>
                            <span class="fw-bold">Previous Session Found</span>
                        </div>

                        <h2 class="fw-bold text-dark mb-2">Welcome Back</h2>
                        <p class="text-muted mb-4" style="font-size: 0.875rem;">
                            We found a locally stored API key. Would you like to continue working with your existing
                            mocks?
                        </p>

                        <div class="d-flex flex-column gap-3">
                            <button @click="handleContinue"
                                    class="btn btn-primary w-100 py-3 fw-semibold shadow-sm d-flex align-items-center justify-content-center gap-2">
                                Continue Session
                            </button>

                            <button @click="isReturningUser = false"
                                    class="btn btn-outline-secondary w-100 py-3 fw-semibold bg-white text-dark">
                                Discard & Generate New Key
                            </button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</template>