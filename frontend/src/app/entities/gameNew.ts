import { ProfileLightResponse } from "../dtos/profileLightResponse";
import { GameThrowMultiplierEnum } from "../enums/gameThrowMultiplierEnum";
import { GameTypeEnum } from "../enums/gameTypeEnum";

export class GameNew {
  constructor(
    public groupUuid: string | null = null,
    public locationUuid: string | null = null,
    public players: ProfileLightResponse[] = [],
    public gameType: GameTypeEnum = GameTypeEnum.CLASSIC,
    public gameTypeClassicPoints: number | null = 301,
    public gameTypeClassicIn: GameThrowMultiplierEnum | null = null,
    public gameTypeClassicOut: GameThrowMultiplierEnum | null = GameThrowMultiplierEnum.DOUBLE,
  ) { }
}