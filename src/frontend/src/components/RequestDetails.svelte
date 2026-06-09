<script>
  import { FileText } from '@lucide/svelte';
  import { formatDate, formatMs, prettyJson } from '../lib/format.js';
  import { selectedWebhook } from '../lib/stores.js';
</script>

<section class="panel detail-panel">
  <div class="panel-header">
    <div>
      <h2>Details</h2>
      <p>{$selectedWebhook ? formatDate($selectedWebhook.timestamp) : 'Select a request'}</p>
    </div>
    <FileText size={19} />
  </div>

  {#if !$selectedWebhook}
    <div class="empty-inline compact-empty">
      <h3>No request selected</h3>
      <p>Choose a request from the log to inspect headers, query params, body, and timing.</p>
    </div>
  {:else}
    <div class="details-stack">
      <div class="detail-summary">
        <span class:matched={$selectedWebhook.matched} class="match-chip">
          {$selectedWebhook.matched ? 'Matched' : 'No match'}
        </span>
        <span>Status {$selectedWebhook.statusCode}</span>
        <span>{formatMs($selectedWebhook.processingTimeMs)}</span>
      </div>

      <div class="detail-line">
        <span>{$selectedWebhook.method}</span>
        <code>{$selectedWebhook.path}</code>
      </div>

      <div class="detail-block">
        <h3>Full URL</h3>
        <pre>{$selectedWebhook.fullUrl || '-'}</pre>
      </div>

      <div class="detail-block">
        <h3>Query Params</h3>
        <pre>{$selectedWebhook.queryParams || '-'}</pre>
      </div>

      <div class="detail-block">
        <h3>Headers</h3>
        <pre>{prettyJson($selectedWebhook.headers, '{}')}</pre>
      </div>

      <div class="detail-block">
        <h3>Body</h3>
        <pre>{prettyJson($selectedWebhook.body, '-')}</pre>
      </div>
    </div>
  {/if}
</section>
