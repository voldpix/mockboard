<script setup>
import {useToast} from '@/useToast.js'
import ToastNotification from '@/components/ToastNotification.vue'

const {toasts, remove} = useToast()
</script>

<template>
    <div class="toast-container-wrapper">
        <ToastNotification
            v-for="toast in toasts"
            :key="toast.id"
            :message="toast.message"
            :type="toast.type"
            :duration="toast.duration"
            :show="toast.show"
            @close="remove(toast.id)"
        />
    </div>
</template>

<style scoped>
.toast-container-wrapper {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 9999;
    display: flex;
    flex-direction: column;
    gap: 10px;
}
</style>