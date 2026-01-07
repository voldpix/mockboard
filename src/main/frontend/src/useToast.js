import {ref} from 'vue'

const toasts = ref([])
let toastId = 0

export function useToast() {
    const show = (message, type = 'success', duration = 3000) => {
        const id = toastId++
        toasts.value.push({
            id,
            message,
            type,
            duration,
            show: true
        })

        if (duration > 0) {
            setTimeout(() => {
                remove(id)
            }, duration + 300)
        }
    }

    const remove = (id) => {
        const index = toasts.value.findIndex(t => t.id === id)
        if (index > -1) {
            toasts.value.splice(index, 1)
        }
    }

    const success = (message, duration) => show(message, 'success', duration)
    const error = (message, duration) => show(message, 'error', duration)
    const warning = (message, duration) => show(message, 'warning', duration)
    const info = (message, duration) => show(message, 'info', duration)

    return {
        toasts,
        show,
        success,
        error,
        warning,
        info,
        remove
    }
}