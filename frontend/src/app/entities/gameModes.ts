import { GameTypeEnum } from "../enums/gameTypeEnum";

export class GameModes {
    constructor (
        public name: GameTypeEnum,
        public isPopular: boolean = false,
        public icon: string | null = null,
        public minPlayers: number = 0,
        public maxPlayers: number = 0,
    ) {}

    public static list(): GameModes[] {
        return [
            new GameModes(GameTypeEnum.CLASSIC, true, './assets/images/error-drawing.svg', 2, 10),
            new GameModes(GameTypeEnum.CRICKET, true, './assets/images/error-drawing.svg', 2, 8),
            new GameModes(GameTypeEnum.COUNT_UP, false, './assets/images/error-drawing.svg', 1, 8),
        ];
    }
}
