<script>
  import { Copy, Database, ExternalLink, Plus, Trash2 } from '@lucide/svelte';
  import { formatDate, mockUrl } from '../lib/format.js';

  export let boards = [];
  export let busy = false;
  export let onCreate = () => {};
  export let onOpen = () => {};
  export let onCopy = () => {};
  export let onDelete = () => {};
</script>

<main class="home-shell">
  <header class="home-header">
    <div class="brand-lockup">
      <img src="/logo.png" alt="Mockboard" class="brand-logo" />
      <div>
        <h1>Mockboard</h1>
        <p>{boards.length} local board{boards.length === 1 ? '' : 's'}</p>
      </div>
    </div>
    <button type="button" class="button primary" disabled={busy} on:click={onCreate}>
      <Plus size={18} />
      Create Board
    </button>
  </header>

  <section class="board-section" aria-label="Local boards">
    {#if boards.length === 0}
      <div class="empty-state">
        <Database size={34} />
        <h2>No boards yet</h2>
        <p>Create a local board to start defining mock endpoints.</p>
        <button type="button" class="button primary" disabled={busy} on:click={onCreate}>
          <Plus size={18} />
          Create Board
        </button>
      </div>
    {:else}
      <div class="board-grid">
        {#each boards as board (board.id)}
          <article class="board-card">
            <div class="board-card-main">
              <span class="board-name">{board.name || board.id}</span>
              {#if board.name}
                <code class="board-id">{board.id}</code>
              {/if}
              <span class="muted">{formatDate(board.timestamp)}</span>
            </div>
            <div class="board-card-meta">
              <span>{board.mockRuleCount ?? 0} mock{(board.mockRuleCount ?? 0) === 1 ? '' : 's'}</span>
              <code>{mockUrl(board.id)}</code>
            </div>
            <div class="board-actions">
              <button type="button" class="button secondary" disabled={busy} on:click={() => onOpen(board)}>
                <ExternalLink size={16} />
                Open
              </button>
              <button type="button" class="icon-button" title="Copy mock URL" disabled={busy} on:click={() => onCopy(board)}>
                <Copy size={16} />
              </button>
              <button type="button" class="icon-button danger" title="Delete board" disabled={busy} on:click={() => onDelete(board)}>
                <Trash2 size={16} />
              </button>
            </div>
          </article>
        {/each}
      </div>
    {/if}
  </section>
</main>
