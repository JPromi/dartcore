import { ProfileLightResponse } from "./profileLightResponse";

export class GroupResponse {
    constructor(
        public uuid: string = "",
        public name: string = "",
        public description: string = "",
        public avatar: string | null = null,
        public banner: string | null = null,
        public members: ProfileLightResponse[] = [],
        public isMember: boolean = false,
        public isAdmin: boolean = false,
        public isOwner: boolean = false,
        public isPublic: boolean = false,
        public createdAt: string | null = null,
  ) { }
}