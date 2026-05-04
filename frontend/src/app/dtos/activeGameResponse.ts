import { GameTypeEnum } from "../enums/gameTypeEnum";
import { ActiveGamePlayerResponse } from "./activeGamePlayerResponse";

export class ActiveGameResponse {
    constructor (
        public uuid: string = "",
        public gameType: GameTypeEnum = GameTypeEnum.CLASSIC,
        public round: number = 0,
        public gameTypeClassicPoints: number = 301,
        public gameTypeClassicInType: string = "",
        public gameTypeClassicOutType: string = "",
        public startTime: Date | null = null,
        public endTime: Date | null = null,
        public isCancelled: boolean = false,
        public players: ActiveGamePlayerResponse[] = [],
    ) {}
}