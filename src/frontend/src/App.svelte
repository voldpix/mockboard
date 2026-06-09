<script>
  import { onMount } from 'svelte';
  import AppFooter from './components/AppFooter.svelte';
  import BoardList from './components/BoardList.svelte';
  import Dashboard from './components/Dashboard.svelte';
  import ToastContainer from './components/ToastContainer.svelte';
  import { api, getAppToken, loadPreConfig } from './lib/api.js';
  import { connectSse, closeSse } from './lib/sse.js';
  import {
    appToken,
    boards,
    config,
    mockRules,
    pushToast,
    resetBoardState,
    selectedBoard,
    selectedWebhook,
    webhooks
  } from './lib/stores.js';
  import { mockUrl } from './lib/format.js';

  let loading = true;
  let busy = false;
  let bootError = '';

  onMount(() => {
    boot();
    return () => closeSse();
  });

  async function boot() {
    loading = true;
    bootError = '';
    try {
      const preConfig = await loadPreConfig();
      config.set(preConfig);
      appToken.set(getAppToken());
      await refreshBoards();
    } catch (error) {
      bootError = error.message || 'Unable to load app configuration';
    } finally {
      loading = false;
    }
  }

  async function refreshBoards() {
    boards.set(await api.listBoards());
  }

  function syncBoard(updatedBoard) {
    boards.update((items) => {
      let found = false;
      const next = items.map((board) => {
        if (board.id !== updatedBoard.id) return board;
        found = true;
        return updatedBoard;
      });
      return found ? next : [updatedBoard, ...next];
    });
  }

  async function createBoard() {
    busy = true;
    try {
      const board = await api.createBoard();
      await refreshBoards();
      await openBoard(board);
      pushToast('success', 'Board created');
    } catch (error) {
      pushToast('error', error.message || 'Failed to create board');
    } finally {
      busy = false;
    }
  }

  async function openBoard(board) {
    busy = true;
    try {
      const [persistedBoard, rules, hooks] = await Promise.all([
        api.getBoard(board.id),
        api.listMockRules(board.id),
        api.listWebhooks(board.id)
      ]);
      selectedBoard.set(persistedBoard);
      syncBoard(persistedBoard);
      mockRules.set(rules);
      webhooks.set(hooks);
      selectedWebhook.set(hooks[0] || null);
      connectSse(persistedBoard.id, getAppToken(), $config?.validations?.maxWebhooks || 100);
    } catch (error) {
      pushToast('error', error.message || 'Failed to open board');
    } finally {
      busy = false;
    }
  }

  async function refreshBoard() {
    if (!$selectedBoard) return;
    await openBoard($selectedBoard);
    pushToast('success', 'Board refreshed');
  }

  async function updateBoardName(name) {
    const updated = await api.updateBoard($selectedBoard.id, { name });
    selectedBoard.set(updated);
    syncBoard(updated);
    pushToast('success', updated.name ? 'Board name updated' : 'Board name cleared');
  }

  async function deleteBoard(board) {
    if (!window.confirm(`Delete board ${board.id}?`)) return;

    busy = true;
    try {
      await api.deleteBoard(board.id);
      if ($selectedBoard?.id === board.id) {
        closeBoard();
      }
      await refreshBoards();
      pushToast('success', 'Board deleted');
    } catch (error) {
      pushToast('error', error.message || 'Failed to delete board');
    } finally {
      busy = false;
    }
  }

  async function createMock(payload) {
    const result = await api.createMockRule($selectedBoard.id, payload);
    mockRules.update((items) => [{ ...payload, id: result.id, timestamp: new Date().toISOString() }, ...items]);
    selectedBoard.update((board) => board ? { ...board, mockRuleCount: (board.mockRuleCount ?? 0) + 1 } : board);
    boards.update((items) => items.map((board) =>
      board.id === $selectedBoard.id ? { ...board, mockRuleCount: (board.mockRuleCount ?? 0) + 1 } : board
    ));
    pushToast('success', 'Mock rule created');
  }

  async function updateMock(mockRuleId, payload) {
    await api.updateMockRule($selectedBoard.id, mockRuleId, payload);
    mockRules.update((items) => items.map((rule) =>
      rule.id === mockRuleId ? { ...payload, id: mockRuleId, timestamp: rule.timestamp } : rule
    ));
    pushToast('success', 'Mock rule updated');
  }

  async function deleteMock(rule) {
    if (!window.confirm(`Delete ${rule.method} ${rule.path}?`)) return;
    await api.deleteMockRule($selectedBoard.id, rule.id);
    mockRules.update((items) => items.filter((item) => item.id !== rule.id));
    selectedBoard.update((board) => board ? { ...board, mockRuleCount: Math.max(0, (board.mockRuleCount ?? 0) - 1) } : board);
    boards.update((items) => items.map((board) =>
      board.id === $selectedBoard.id ? { ...board, mockRuleCount: Math.max(0, (board.mockRuleCount ?? 0) - 1) } : board
    ));
    pushToast('success', 'Mock rule deleted');
  }

  function closeBoard() {
    closeSse();
    resetBoardState();
  }

  async function copyBoardUrl(board) {
    await navigator.clipboard.writeText(mockUrl(board.id));
    pushToast('success', 'Mock URL copied');
  }
</script>

{#if loading}
  <main class="boot-screen">
    <img src="/logo.png" alt="Mockboard" class="boot-logo" />
    <p>Loading local workspace</p>
  </main>
{:else if bootError}
  <main class="boot-screen">
    <img src="/logo.png" alt="Mockboard" class="boot-logo" />
    <h1>Mockboard</h1>
    <p class="error-text">{bootError}</p>
    <button type="button" class="button primary" on:click={boot}>Retry</button>
  </main>
{:else if $selectedBoard}
  <Dashboard
    busy={busy}
    onBack={closeBoard}
    onRefresh={refreshBoard}
    onCopy={() => copyBoardUrl($selectedBoard)}
    onUpdateBoardName={updateBoardName}
    onCreateMock={createMock}
    onUpdateMock={updateMock}
    onDeleteMock={deleteMock}
  />
{:else}
  <BoardList
    boards={$boards}
    busy={busy}
    onCreate={createBoard}
    onOpen={openBoard}
    onCopy={copyBoardUrl}
    onDelete={deleteBoard}
  />
{/if}

<AppFooter />
<ToastContainer />
