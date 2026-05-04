import { GameThrow } from "../entities/gameThrow";

export class ActiveGamePlayerResponse {
    constructor (
        public playerUuid: string = "",
        public name: string = "",
        public orderIndex: number = 0,
        public avatar: string | null = null,
        public throwList: GameThrow[] = [],
        public hints: GameThrow[] = [],
        public score: number | null = null,
        public highscore: number | null = null,
        public average: number | null = null,
        public isCurrentPlayer: boolean = false,
        public isEliminated: boolean = false,
        public isWinner: boolean = false,
    ) {}
}