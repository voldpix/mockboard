<script setup>
import Navbar from '@/components/Navbar.vue'
import SessionOverlay from '@/components/SessionOverlay.vue'
import RequestSidebar from '@/components/RequestSidebar.vue'
import {onMounted, ref} from 'vue'
import DashboardLayout from '@/components/DashboardLayout.vue'
import Footer from "@/components/Footer.vue";

const dashboardRef = ref(null)
const isReady = ref(false)

const checkDevice= () => {
    const ua = navigator.userAgent
    return (/android/i.test(ua) || /iPad|iPhone|iPod/.test(ua) || window.innerWidth < 800);
}

const isMobile = ref(checkDevice())
onMounted(() => {
    checkDevice()
    window.addEventListener('resize', () => {
        isMobile.value = checkDevice()
    })
})

const onSessionStart = () => {
    isReady.value = true
}

const onSessionContinue = () => {
    isReady.value = true
}

const onWebhookSelected = (log) => {
    if (dashboardRef.value) {
        dashboardRef.value.openWebhookDetails(log)
    }
}
</script>

<template>
    <div v-if="isMobile" class="min-vh-100 bg-white d-flex align-items-center justify-content-center p-4 text-center">
        <div>
            <h4 class="fw-bold mb-3">Desktop Only</h4>
            <p class="text-muted" style="max-width: 300px; margin: 0 auto;">
                MockBoard is a developer tool designed for large screens. Please open this on your desktop browser.
            </p>
        </div>
    </div>

    <template v-else>
        <SessionOverlay
            v-if="!isReady"
            @session-start="onSessionStart"
            @session-continue="onSessionContinue"/>

        <template v-if="isReady">
            <Navbar/>
            <RequestSidebar @view-webhook="onWebhookSelected"/>
            <DashboardLayout ref="dashboardRef"/>
            <Footer/>
        </template>
    </template>
</template>

<style scoped>
.tracking-wide {
    letter-spacing: 0.1em;
}
</style>
