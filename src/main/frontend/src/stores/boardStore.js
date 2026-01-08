import {defineStore} from 'pinia'

export const useBoardStore = defineStore("boardStore", {
    state: () => ({
        board: null,
        mocks: []
    }),
    actions: {
        initializeBoardStore(boardModel) {
            console.log(`Initializing boardStore for ${boardModel.id}`);
            this.board = boardModel;
            this.board.updateLastInteraction()
        },

        clearBoardStore() {
            this.board = null
            localStorage.clear()
        }
    }
})
