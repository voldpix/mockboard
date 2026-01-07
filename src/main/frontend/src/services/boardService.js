import api from "@/api.js";

const checkBoardExistencePath = (boardId) => `api/boards/${boardId}/check`;
const createBoardPath = `api/boards`;

const BoardService = {
    checkExistence(boardId, ownerToken) {
        return api.get(checkBoardExistencePath(boardId), {
            ...api.attachOwnerHeader(ownerToken),
        });
    },

    createBoard() {
        return api.post(createBoardPath, {})
    }
}

export default BoardService;