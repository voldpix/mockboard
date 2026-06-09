<script>
  import { Plus, Save, X } from '@lucide/svelte';
  import { config } from '../lib/stores.js';
  import { formFromRule, supportedMethods, toMockPayload, validateMockForm } from '../lib/validation.js';

  export let rule = null;
  export let saving = false;
  export let onCancel = () => {};
  export let onSubmit = async () => {};

  let form = formFromRule(rule);
  let loadedRuleId = undefined;
  let errors = {};
  let serverError = '';
  const bodyPlaceholder = '{"ok": true}';

  $: if ((rule?.id || 'create') !== loadedRuleId) {
    loadedRuleId = rule?.id || 'create';
    form = formFromRule(rule);
    errors = {};
    serverError = '';
  }

  function addHeader() {
    form = { ...form, headers: [...form.headers, { key: '', value: '' }] };
  }

  function removeHeader(index) {
    form = { ...form, headers: form.headers.filter((_, itemIndex) => itemIndex !== index) };
  }

  async function submitForm() {
    errors = validateMockForm(form, $config);
    serverError = '';
    if (Object.keys(errors).length) return;

    try {
      await onSubmit(toMockPayload(form));
    } catch (error) {
      serverError = error.message || 'Failed to save mock rule';
    }
  }
</script>

<form class="mock-form" on:submit|preventDefault={submitForm}>
  <div class="modal-header">
    <div>
      <h2>{rule ? 'Edit Mock Endpoint' : 'Create Mock Endpoint'}</h2>
      <p>{rule ? `${rule.method} ${rule.path}` : 'Define how this board responds to matching requests.'}</p>
    </div>
    <button type="button" class="icon-button" title="Close" on:click={onCancel}>
      <X size={18} />
    </button>
  </div>

  <div class="form-grid">
    <label>
      <span>Method</span>
      <select bind:value={form.method}>
        {#each supportedMethods as method}
          <option value={method}>{method}</option>
        {/each}
      </select>
      {#if errors.method}<small class="field-error">{errors.method}</small>{/if}
    </label>

    <label class="path-field">
      <span>Path</span>
      <input bind:value={form.path} maxlength={$config?.validations?.maxMockPathLength || 1000} placeholder="/api/users/*" />
      {#if errors.path}<small class="field-error">{errors.path}</small>{/if}
    </label>

    <label>
      <span>Status</span>
      <input bind:value={form.statusCode} type="number" min="100" max="599" />
      {#if errors.statusCode}<small class="field-error">{errors.statusCode}</small>{/if}
    </label>

    <label>
      <span>Delay</span>
      <input bind:value={form.delay} type="number" min="0" max="10000" />
      {#if errors.delay}<small class="field-error">{errors.delay}</small>{/if}
    </label>
  </div>

  <section class="form-section">
    <div class="form-section-header">
      <h3>Response Headers</h3>
      <button type="button" class="button secondary compact" on:click={addHeader}>
        <Plus size={15} />
        Header
      </button>
    </div>

    <div class="header-rows">
      {#each form.headers as header, index}
        <div class="header-row">
          <input bind:value={header.key} placeholder="Header" maxlength={$config?.validations?.maxMockHeaderKeyLength || 100} />
          <input bind:value={header.value} placeholder="Value" maxlength={$config?.validations?.maxMockHeaderValueLength || 500} />
          <button type="button" class="icon-button danger" title="Remove header" on:click={() => removeHeader(index)}>
            <X size={15} />
          </button>
        </div>
        {#if errors[`header-${index}`]}<small class="field-error">{errors[`header-${index}`]}</small>{/if}
      {/each}
      {#if errors.headers}<small class="field-error">{errors.headers}</small>{/if}
    </div>
  </section>

  <label class="body-field">
    <span>JSON Body</span>
    <textarea bind:value={form.body} rows="10" maxlength={$config?.validations?.maxMockBodyLength || 100000} placeholder={bodyPlaceholder}></textarea>
    {#if errors.body}<small class="field-error">{errors.body}</small>{/if}
  </label>

  {#if serverError}
    <div class="server-error">{serverError}</div>
  {/if}

  <div class="modal-actions">
    <button type="button" class="button secondary" disabled={saving} on:click={onCancel}>Cancel</button>
    <button type="submit" class="button primary" disabled={saving}>
      <Save size={17} />
      {saving ? 'Saving' : 'Save Mock'}
    </button>
  </div>
</form>
