<script setup>
import constants from "@/constants.js";

const currentYear = () => {
    return new Date().getFullYear();
}
</script>

<template>
    <footer class="footer-fixed justify-content-between text-muted text-xs">
        <div>
            &copy; {{ currentYear() }} MockBoard.dev
        </div>
        <div>
            <span class="me-3">Developed by <a href="https://github.com/voldpix" target="_blank"
                                               class="text-decoration-none text-primary">voldpix</a></span>
            <span class="badge bg-light text-dark border">{{ constants.APP_VERSION }}</span>
        </div>
    </footer>
</template>