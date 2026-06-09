<script>
  import { AlertTriangle, CheckCircle2, X } from '@lucide/svelte';
  import { dismissToast, toasts } from '../lib/stores.js';
</script>

<div class="toast-stack" aria-live="polite">
  {#each $toasts as toast (toast.id)}
    <div class="toast {toast.type}">
      {#if toast.type === 'success'}
        <CheckCircle2 size={17} />
      {:else}
        <AlertTriangle size={17} />
      {/if}
      <span>{toast.message}</span>
      <button type="button" class="toast-close" title="Dismiss" on:click={() => dismissToast(toast.id)}>
        <X size={14} />
      </button>
    </div>
  {/each}
</div>
