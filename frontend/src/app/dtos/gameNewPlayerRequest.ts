import { GameNew } from "../entities/gameNew";
import { GameThrowMultiplierEnum } from "../enums/gameThrowMultiplierEnum";
import { GameTypeEnum } from "../enums/gameTypeEnum";
import { ProfileLightResponse } from "./profileLightResponse";

export class GameNewPlayerRequest {
  constructor(
    public accountUuid: string | null = null,
    public name: string | null = null,
  ) { }

  public static fromProfileLightResponse(profile: ProfileLightResponse): GameNewPlayerRequest {
    if (profile.uuid === "guest") {
      return new GameNewPlayerRequest(null, profile.username);
    } else {
      return new GameNewPlayerRequest(profile.uuid, null);
    }
  }
}