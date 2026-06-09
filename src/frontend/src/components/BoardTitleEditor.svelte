<script>
  import { Check, Pencil, X } from '@lucide/svelte';
  import { config } from '../lib/stores.js';

  export let board;
  export let saving = false;
  export let onSave = async () => {};

  let editing = false;
  let draft = '';
  let error = '';

  $: displayName = board?.name?.trim() || board?.id;
  $: maxNameLength = $config?.validations?.maxBoardNameLength || 80;

  function startEditing() {
    draft = board?.name || '';
    error = '';
    editing = true;
  }

  function cancelEditing() {
    editing = false;
    error = '';
  }

  async function saveName() {
    const normalized = draft.trim();
    if (normalized.length > maxNameLength) {
      error = `Use ${maxNameLength} characters or fewer`;
      return;
    }

    try {
      await onSave(normalized);
      editing = false;
      error = '';
    } catch (saveError) {
      error = saveError.message || 'Unable to save board name';
    }
  }
</script>

{#if editing}
  <form class="board-title-editor" on:submit|preventDefault={saveName}>
    <label>
      <span class="muted">Board</span>
      <input
        bind:value={draft}
        aria-label="Board name"
        maxlength={maxNameLength}
        placeholder={board.id}
        disabled={saving}
      />
    </label>
    <div class="board-title-controls">
      <button type="submit" class="icon-button" title="Save board name" disabled={saving}>
        <Check size={16} />
      </button>
      <button type="button" class="icon-button" title="Cancel board name edit" disabled={saving} on:click={cancelEditing}>
        <X size={16} />
      </button>
    </div>
    {#if error}<small class="field-error">{error}</small>{/if}
  </form>
{:else}
  <button type="button" class="board-title-button" title="Edit board name" on:click={startEditing}>
    <span class="muted">Board</span>
    <strong>{displayName}</strong>
    {#if board.name}
      <code>{board.id}</code>
    {/if}
    <Pencil size={14} />
  </button>
{/if}
