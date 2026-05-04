import { ProfileLinksResponse } from "./profileLinksResponse";

export class ProfileResponse {
    constructor(
        public uuid: string = "",
        public username: string = "",
        public description: string = "",
        public country: string | null = null,
        public avatar: string | null = null,
        public banner: string | null = null,
        public visibility: string = "",
        public links: ProfileLinksResponse = new ProfileLinksResponse(),
        public createdAt: string | null = null,
    ) {}
}