<script setup>
import {ref} from "vue";

const dashboardStats = ref([
    {
        statName: 'Active Mocks',
        value: 8
    },
    {
        statName: 'Total Requests',
        value: '1,245'
    },
    {
        statName: 'Avg Latency',
        value: '45ms'
    },
])
</script>
<template>
    <div class="row g-3 mb-4">
        <div v-for="stat in dashboardStats" class="col-md-4">
            <div class="card shadow-sm h-100 border-0">
                <div class="card-body">
                    <div class="text-muted text-uppercase text-xs fw-bold mb-2">{{stat.statName}}</div>
                    <h2 class="mb-0">{{stat.value}}</h2>
                </div>
            </div>
        </div>
    </div>
</template>