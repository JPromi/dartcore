import { FileCustom } from "./fileCustom";
import { ProfileLinksResponse } from "../dtos/profileLinksResponse";

export class SettingProfile {
    constructor(
        public uuid: string = "",
        public avatar: FileCustom | null = null,
        public banner: FileCustom | null = null,
        public username: string = "",
        public description: string = "",
        public country: string = "",
        public visibility: string = "",
        public links: ProfileLinksResponse = new ProfileLinksResponse(),
    ) {}
}