import {createApp} from 'vue'
import {createPinia} from 'pinia'
import {initConfig} from "@/config.js";

import App from './App.vue'
import router from './router'
import boardService from "@/services/boardService.js";

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './assets/style.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

initConfig(() => boardService.getConfigs()).then(() => {
    app.mount('#app')
})
