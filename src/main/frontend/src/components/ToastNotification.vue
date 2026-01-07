<script setup>
import {ref, watch, onMounted} from 'vue'

const props = defineProps({
    message: {
        type: String,
        required: true
    },
    type: {
        type: String,
        default: 'success', // success, error, warning, info
        validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
    },
    duration: {
        type: Number,
        default: 3000
    },
    show: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['close'])

const visible = ref(false)
let timeout = null

const typeConfig = {
    success: {
        bgClass: 'bg-success',
        icon: 'bi-check-circle-fill',
        textClass: 'text-white'
    },
    error: {
        bgClass: 'bg-danger',
        icon: 'bi-x-circle-fill',
        textClass: 'text-white'
    },
    warning: {
        bgClass: 'bg-warning',
        icon: 'bi-exclamation-triangle-fill',
        textClass: 'text-dark'
    },
    info: {
        bgClass: 'bg-info',
        icon: 'bi-info-circle-fill',
        textClass: 'text-white'
    }
}

const config = typeConfig[props.type]

const close = () => {
    visible.value = false
    if (timeout) {
        clearTimeout(timeout)
    }
    setTimeout(() => {
        emit('close')
    }, 300)
}

watch(() => props.show, (newVal) => {
    if (newVal) {
        visible.value = true
        if (props.duration > 0) {
            timeout = setTimeout(() => {
                close()
            }, props.duration)
        }
    }
})

onMounted(() => {
    if (props.show) {
        visible.value = true
        if (props.duration > 0) {
            timeout = setTimeout(() => {
                close()
            }, props.duration)
        }
    }
})
</script>

<template>
    <Transition name="toast">
        <div v-if="visible" class="toast-container">
            <div class="toast-notification" :class="[config.bgClass, config.textClass]">
                <div class="d-flex align-items-center flex-grow-1">
                    <i :class="`bi ${config.icon} me-2`"></i>
                    <span class="fw-semibold">{{ message }}</span>
                </div>
                <button @click="close" type="button" class="btn-close btn-close-white ms-3 flex-shrink-0" aria-label="Close"></button>
            </div>
        </div>
    </Transition>
</template>

<style scoped>
.toast-container {
    position: fixed;
    top: 50px;
    right: 20px;
    z-index: 9999;
}

.toast-notification {
    display: flex;
    align-items: center;
    padding: 12px 16px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    min-width: 300px;
    max-width: 500px;
}

.toast-enter-active,
.toast-leave-active {
    transition: all 0.3s ease;
}

.toast-enter-from {
    opacity: 0;
    transform: translateX(100px);
}

.toast-leave-to {
    opacity: 0;
    transform: translateX(100px);
}
</style>