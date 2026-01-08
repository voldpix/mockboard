import api from "@/api.js";

const getBoardPath = (boardId) => `api/boards/${boardId}`;
const createBoardPath = `api/boards`;

const BoardService = {
    getBoard(boardId, ownerToken) {
        return api.get(getBoardPath(boardId), {
            ...api.attachOwnerHeader(ownerToken),
        });
    },

    createBoard() {
        return api.post(createBoardPath, {})
    }
}

export default BoardService;