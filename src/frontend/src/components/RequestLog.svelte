<script>
  import { Activity, CheckCircle2, CircleSlash } from '@lucide/svelte';
  import { formatDate, formatMs, methodClass } from '../lib/format.js';
  import { selectedWebhook } from '../lib/stores.js';

  export let requests = [];
</script>

<section class="panel request-panel">
  <div class="panel-header">
    <div>
      <h2>Requests</h2>
      <p>{requests.length} captured</p>
    </div>
    <Activity size={19} />
  </div>

  {#if requests.length === 0}
    <div class="empty-inline compact-empty request-empty">
      <h3>No requests yet</h3>
      <p>Requests to the mock URL will appear here.</p>
    </div>
  {:else}
    <div class="request-list">
      {#each requests as request (request.id)}
        <button
          type="button"
          class:active={$selectedWebhook?.id === request.id}
          class="request-row"
          on:click={() => selectedWebhook.set(request)}
        >
          <span class="request-status" class:matched={request.matched}>
            {#if request.matched}<CheckCircle2 size={15} />{:else}<CircleSlash size={15} />{/if}
            {request.matched ? 'Matched' : 'No match'}
          </span>
          <span class="request-line">
            <span class="method-badge {methodClass(request.method)}">{request.method}</span>
            <code>{request.path}</code>
          </span>
          <span class="request-meta">
            <span>{request.statusCode}</span>
            <span>{formatMs(request.processingTimeMs)}</span>
            <span>{formatDate(request.timestamp)}</span>
          </span>
        </button>
      {/each}
    </div>
  {/if}
</section>
