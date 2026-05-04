import { GameThrowMultiplierEnum } from "../enums/gameThrowMultiplierEnum";
import { GameThrowTypeEnum } from "../enums/gameThtowTypeEnum";

export class ActiveGameThrowRequest {
    constructor(
        public type: GameThrowTypeEnum = GameThrowTypeEnum.THROW,
        public point: number = 0,
        public multiplier: GameThrowMultiplierEnum | null = GameThrowMultiplierEnum.NONE,
        public isUndo: boolean = false
    ) { }
}