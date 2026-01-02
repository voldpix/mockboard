<script setup>
import Navbar from "@/components/Navbar.vue";
</script>

<template>
    <div class="min-h-screen bg-gray-100">
        <Navbar/>

        <main class="pt-20 px-6 max-w-[1400px] mx-auto">

            <div
                class="flex flex-col items-center justify-center h-[80vh] text-center border-2 border-dashed border-gray-300 rounded-xl bg-gray-50/50">
                <h1 class="text-3xl font-bold text-gray-400 mb-2">
                    No mocks defined yet
                </h1>
                <p class="text-gray-400">
                    Create your first mock endpoint to get started.
                </p>
                <button
                    class="mt-6 px-6 py-2 bg-indigo-600 text-white rounded-md font-medium hover:bg-indigo-700 transition shadow-sm cursor-pointer">
                    + Create Mock
                </button>
            </div>

        </main>
    </div>
</template>
