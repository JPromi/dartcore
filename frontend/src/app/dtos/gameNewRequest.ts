import { GameNew } from "../entities/gameNew";
import { GameThrowMultiplierEnum } from "../enums/gameThrowMultiplierEnum";
import { GameTypeEnum } from "../enums/gameTypeEnum";
import { GameNewPlayerRequest } from "./gameNewPlayerRequest";

export class GameNewRequest {
  constructor(
    public groupUuid: string | null = null,
    public locationUuid: string | null = null,
    public players: GameNewPlayerRequest[] = [],
    public gameType: GameTypeEnum = GameTypeEnum.CLASSIC,
    public gameTypeClassicPoints: number | null = 301,
    public gameTypeClassicIn: GameThrowMultiplierEnum | null = GameThrowMultiplierEnum.SINGLE,
    public gameTypeClassicOut: GameThrowMultiplierEnum | null = GameThrowMultiplierEnum.DOUBLE,
  ) { }

  public static fromGameNew(gameNew: GameNew): GameNewRequest {
    return new GameNewRequest(
      gameNew.groupUuid,
      gameNew.locationUuid,
      gameNew.players.map(player => GameNewPlayerRequest.fromProfileLightResponse(player)),
      gameNew.gameType,
      gameNew.gameTypeClassicPoints,
      gameNew.gameTypeClassicIn,
      gameNew.gameTypeClassicOut
    );
  }
}