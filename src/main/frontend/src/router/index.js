import {createRouter, createWebHistory} from 'vue-router'
import BoardView from "@/views/BoardView.vue";
import ErrorView from "@/views/ErrorView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'board',
            component: BoardView,
        },
        {
            path: '/:pathMatch(.*)*',
            name: 'not-found',
            component: ErrorView
        }
    ],
})

export default router
