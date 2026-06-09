<script>
  import { ArrowLeft, Copy, Plus, RefreshCw, Wifi, WifiOff } from '@lucide/svelte';
  import BoardTitleEditor from './BoardTitleEditor.svelte';
  import MockRuleForm from './MockRuleForm.svelte';
  import MockRules from './MockRules.svelte';
  import RequestDetails from './RequestDetails.svelte';
  import RequestLog from './RequestLog.svelte';
  import { mockRules, selectedBoard, sseStatus, webhooks } from '../lib/stores.js';
  import { mockUrl } from '../lib/format.js';

  export let busy = false;
  export let onBack = () => {};
  export let onRefresh = () => {};
  export let onCopy = () => {};
  export let onUpdateBoardName = async () => {};
  export let onCreateMock = async () => {};
  export let onUpdateMock = async () => {};
  export let onDeleteMock = async () => {};

  let formOpen = false;
  let editingRule = null;
  let saving = false;
  let nameSaving = false;

  $: connected = $sseStatus.state === 'connected';

  function openCreateForm() {
    editingRule = null;
    formOpen = true;
  }

  function openEditForm(rule) {
    editingRule = rule;
    formOpen = true;
  }

  async function saveMock(payload) {
    saving = true;
    try {
      if (editingRule) {
        await onUpdateMock(editingRule.id, payload);
      } else {
        await onCreateMock(payload);
      }
      formOpen = false;
      editingRule = null;
    } finally {
      saving = false;
    }
  }

  async function saveBoardName(name) {
    nameSaving = true;
    try {
      await onUpdateBoardName(name);
    } finally {
      nameSaving = false;
    }
  }
</script>

<main class="dashboard-shell">
  <header class="topbar">
    <div class="topbar-left">
      <button type="button" class="icon-button" title="Back to boards" on:click={onBack}>
        <ArrowLeft size={18} />
      </button>
      <BoardTitleEditor board={$selectedBoard} saving={nameSaving} onSave={saveBoardName} />
    </div>

    <div class="topbar-url">
      <code>{mockUrl($selectedBoard.id)}</code>
      <button type="button" class="icon-button" title="Copy mock URL" on:click={onCopy}>
        <Copy size={16} />
      </button>
    </div>

    <div class="topbar-actions">
      <span class:connected class="sse-pill">
        {#if connected}<Wifi size={15} />{:else}<WifiOff size={15} />{/if}
        {$sseStatus.label}
      </span>
      <button type="button" class="icon-button" title="Refresh board" disabled={busy} on:click={onRefresh}>
        <RefreshCw size={17} />
      </button>
      <button type="button" class="button primary compact" on:click={openCreateForm}>
        <Plus size={17} />
        Mock
      </button>
    </div>
  </header>

  <section class="dashboard-grid">
    <div class="mock-column">
      <MockRules
        rules={$mockRules}
        onCreate={openCreateForm}
        onEdit={openEditForm}
        onDelete={onDeleteMock}
      />
    </div>

    <div class="activity-column">
      <RequestLog requests={$webhooks} />
    </div>

    <div class="detail-column">
      <RequestDetails />
    </div>
  </section>

  {#if formOpen}
    <div class="modal-backdrop">
      <div class="modal-panel" role="dialog" aria-modal="true" aria-label={editingRule ? 'Edit mock rule' : 'Create mock rule'}>
        <MockRuleForm
          rule={editingRule}
          saving={saving}
          onCancel={() => {
            formOpen = false;
            editingRule = null;
          }}
          onSubmit={saveMock}
        />
      </div>
    </div>
  {/if}
</main>
