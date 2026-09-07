import { GameThrowMultiplierEnum } from "../enums/gameThrowMultiplierEnum";

export class GameHintResponse {
    constructor (
        public points: number = 0,
        public multiplier: GameThrowMultiplierEnum = GameThrowMultiplierEnum.NONE,
    ) {}
}