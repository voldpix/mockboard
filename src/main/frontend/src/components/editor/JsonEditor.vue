<script setup>
import {computed, nextTick, ref, watch} from 'vue'

const props = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    maxBytes: {
        type: Number,
        default: null
    }
})

const emit = defineEmits(['update:modelValue', 'valid', 'invalid'])

const textarea = ref(null)
const lineNumbers = ref(null)
const localValue = ref(props.modelValue)
const validationError = ref('')
const copied = ref(false)
const placeholder = '{ "key": "value" }';

const hasError = computed(() => !!validationError.value)
const lineCount = computed(() => localValue.value.split('\n').length)
const charCount = computed(() => new Blob([localValue.value]).size)
const isNearLimit = computed(() => {
    if (!props.maxBytes) return false
    return charCount.value / props.maxBytes > 0.8
})

watch(() => props.modelValue, (newValue) => {
    if (newValue !== localValue.value) {
        localValue.value = newValue
        if (localValue.value.trim()) {
            console.log(localValue.value)
            try {
                JSON.parse(localValue.value)
                formatJson()
            } catch (e) {
                validateJson()
            }
        }
    }
}, {immediate: true}) // runs when the component is created

const handleInput = () => {
    emit('update:modelValue', localValue.value)

    clearTimeout(handleInput.timeout)
    handleInput.timeout = setTimeout(() => {
        validateJson()
    }, 500)
}

const handleBlur = () => {
    if (localValue.value.trim()) {
        try {
            JSON.parse(localValue.value)
            formatJson()
        } catch (e) {
            validateJson()
        }
    }
}

const validateJson = () => {
    validationError.value = ''

    if (!localValue.value.trim()) {
        emit('valid')
        return true
    }

    try {
        JSON.parse(localValue.value)
        emit('valid')
        return true
    } catch (e) {
        const errorMsg = parseJsonError(e.message)
        validationError.value = errorMsg
        emit('invalid', errorMsg)
        return false
    }
}

const parseJsonError = (message) => {
    if (message.includes('Unexpected token')) {
        return 'Invalid JSON syntax'
    }
    if (message.includes('Unexpected end')) {
        return 'Incomplete JSON, missing closing bracket or brace'
    }
    if (message.includes('Expected')) {
        return 'Invalid JSON structure'
    }
    return 'Invalid JSON'
}

const formatJson = () => {
    if (!localValue.value.trim()) return

    try {
        const parsed = JSON.parse(localValue.value)
        const formatted = JSON.stringify(parsed, null, 2)

        localValue.value = formatted
        emit('update:modelValue', formatted)
        validationError.value = ''
        emit('valid')
    } catch (e) {
        validationError.value = parseJsonError(e.message)
        emit('invalid', validationError.value)
    }
}

const copyToClipboard = async () => {
    try {
        await navigator.clipboard.writeText(localValue.value)
        copied.value = true
        setTimeout(() => {
            copied.value = false
        }, 2000)
    } catch (e) {
        console.error('Failed to copy:', e)
    }
}

const handleKeydown = (e) => {
    if (e.key === 'Tab') {
        e.preventDefault()
        const start = e.target.selectionStart
        const end = e.target.selectionEnd
        const indent = '  '

        localValue.value = localValue.value.substring(0, start) + indent + localValue.value.substring(end)

        nextTick(() => {
            textarea.value.selectionStart = textarea.value.selectionEnd = start + 2
        })
        emit('update:modelValue', localValue.value)
    }

    if (e.key === '{' || e.key === '[' || e.key === '"') {
        e.preventDefault()
        const start = e.target.selectionStart
        const end = e.target.selectionEnd

        let closeChar
        if (e.key === '{') closeChar = '}'
        else if (e.key === '[') closeChar = ']'
        else if (e.key === '"') closeChar = '"'

        localValue.value = localValue.value.substring(0, start) + e.key + closeChar + localValue.value.substring(end);
        nextTick(() => {
            textarea.value.selectionStart = textarea.value.selectionEnd = start + 1
        })

        emit('update:modelValue', localValue.value)
    }
}

const formatBytes = (bytes) => {
    if (bytes < 1024) return bytes + 'B'
    return (bytes / 1024).toFixed(1) + 'KB'
}

defineExpose({
    formatJson,
    validateJson,
    hasError: () => hasError.value
})
</script>

<template>
    <div class="json-editor">
        <div class="json-editor-toolbar">
            <div class="toolbar-status">
                <span v-if="validationError" class="status-error">
                  <i class="bi bi-exclamation-circle-fill"></i>
                  {{ validationError }}
                </span>
                <span v-else-if="localValue.trim()" class="status-valid">
                    <i class="bi bi-check-circle-fill"></i>
                    Valid JSON
                </span>
            </div>
            <div class="toolbar-actions">
                <button
                    type="button"
                    @click="formatJson"
                    class="toolbar-icon-btn"
                    :disabled="hasError"
                    title="Format JSON">
                    <i class="bi bi-code-square"></i>
                </button>
                <button
                    type="button"
                    @click="copyToClipboard"
                    class="toolbar-icon-btn"
                    :title="copied ? 'Copied!' : 'Copy to clipboard'">
                    <i :class="copied ? 'bi bi-check-lg' : 'bi bi-clipboard'"></i>
                </button>
                <span v-if="charCount" class="char-count" :class="{ 'near-limit': isNearLimit }">
          {{ formatBytes(charCount) }}
          <span v-if="maxBytes"> / {{ formatBytes(maxBytes) }}</span>
        </span>
            </div>
        </div>

        <div class="json-editor-wrapper" :class="{ 'has-error': hasError }">
            <div class="line-numbers" ref="lineNumbers">
                <div v-for="line in lineCount" :key="line" class="line-number">{{ line }}</div>
            </div>
            <textarea
                name="jsonEditorArea"
                ref="textarea"
                v-model="localValue"
                @input="handleInput"
                @blur="handleBlur"
                @keydown="handleKeydown"
                class="json-editor-textarea"
                :placeholder="placeholder"
                spellcheck="false"
                autocomplete="off"
                autocorrect="off"
                autocapitalize="off"
            ></textarea>
        </div>
    </div>
</template>

<style scoped>
.json-editor {
    display: flex;
    flex-direction: column;
    border: 1px solid #dee2e6;
    border-radius: 0.375rem;
    overflow: hidden;
    background: #fff;
}

.json-editor-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5rem 0.75rem;
    background: #f8f9fa;
    border-bottom: 1px solid #dee2e6;
    gap: 0.5rem;
    min-height: 2.5rem;
}

.toolbar-status {
    flex: 1;
    font-size: 0.875rem;
}

.status-error {
    color: #dc3545;
    display: flex;
    align-items: center;
    gap: 0.375rem;
}

.status-valid {
    color: #198754;
    display: flex;
    align-items: center;
    gap: 0.375rem;
}

.toolbar-actions {
    display: flex;
    gap: 0.5rem;
    align-items: center;
}

.toolbar-icon-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 2rem;
    height: 2rem;
    padding: 0;
    border: 1px solid #dee2e6;
    background: #fff;
    border-radius: 0.25rem;
    cursor: pointer;
    transition: all 0.15s;
    font-size: 1rem;
}

.toolbar-icon-btn:hover:not(:disabled) {
    background: #e9ecef;
    border-color: #adb5bd;
}

.toolbar-icon-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.char-count {
    font-size: 0.75rem;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
    color: #6c757d;
    white-space: nowrap;
    margin-left: 0.5rem;
}

.char-count.near-limit {
    color: #fd7e14;
    font-weight: bold;
}

.json-editor-wrapper {
    display: flex;
    position: relative;
    background: #1e1e1e;
    max-height: 400px;
    overflow: hidden;
}

.json-editor-wrapper.has-error {
    border: 2px solid #dc3545;
    border-top: none;
}

.line-numbers {
    padding: 0.75rem 0.5rem;
    background: #3e3e42;
    color: #858585;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
    font-size: 0.875rem;
    line-height: 1.5;
    text-align: right;
    user-select: none;
    min-width: 3rem;
    border-right: 1px solid #3e3e42;
    overflow: hidden;
}

.line-number {
    height: 1.3125rem;
}

.json-editor-textarea {
    flex: 1;
    padding: 0.75rem 0.75rem 0.75rem 0.5rem;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
    font-size: 0.875rem;
    line-height: 1.5;
    border: none;
    outline: none;
    resize: none;
    min-height: 200px;
    max-height: 600px;
    background: #322f2f;
    color: #97c997;
    tab-size: 2;
    overflow-y: auto;
}

.json-editor-textarea::placeholder {
    color: #858585;
}
</style>