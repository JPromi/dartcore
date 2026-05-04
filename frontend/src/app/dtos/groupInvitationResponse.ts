import { InvitationStatusAccountEnum } from "../enums/invitationStatusAccountEnum";
import { GroupLightResponse } from "./groupLightResponse";
import { ProfileLightResponse } from "./profileLightResponse";

export class GroupInvitationResponse {
    constructor (
        public uuid: string | null = null,
        public group: GroupLightResponse | null = null,
        public status: InvitationStatusAccountEnum | null = null,
        public inviter: ProfileLightResponse | null = null,
        public createdAt: Date | null = null
    ) {}
}