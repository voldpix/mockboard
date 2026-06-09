<script>
  import { Clock, Pencil, Plus, Trash2 } from '@lucide/svelte';
  import { formatDate, methodClass } from '../lib/format.js';

  export let rules = [];
  export let onCreate = () => {};
  export let onEdit = () => {};
  export let onDelete = () => {};
</script>

<section class="panel mock-panel">
  <div class="panel-header">
    <div>
      <h2>Mock Endpoints</h2>
      <p>{rules.length} rule{rules.length === 1 ? '' : 's'}</p>
    </div>
    <button type="button" class="icon-button" title="Create mock rule" on:click={onCreate}>
      <Plus size={17} />
    </button>
  </div>

  {#if rules.length === 0}
    <div class="empty-inline">
      <h3>No mock endpoints</h3>
      <p>Create a rule and send a request to the board URL.</p>
      <button type="button" class="button primary" on:click={onCreate}>
        <Plus size={17} />
        Create Mock
      </button>
    </div>
  {:else}
    <div class="mock-list">
      {#each rules as rule (rule.id)}
        <article class="mock-card">
          <div class="mock-card-top">
            <span class="method-badge {methodClass(rule.method)}">{rule.method}</span>
            <code>{rule.path}</code>
          </div>
          <div class="mock-card-meta">
            <span>Status {rule.statusCode}</span>
            <span><Clock size={14} /> {rule.delay || 0} ms</span>
            <span>{formatDate(rule.timestamp)}</span>
          </div>
          <div class="mock-card-actions">
            <button type="button" class="icon-button" title="Edit mock" on:click={() => onEdit(rule)}>
              <Pencil size={15} />
            </button>
            <button type="button" class="icon-button danger" title="Delete mock" on:click={() => onDelete(rule)}>
              <Trash2 size={15} />
            </button>
          </div>
        </article>
      {/each}
    </div>
  {/if}
</section>
