import { FileCustom } from "./fileCustom";

export class GroupSettingsGeneral {
    constructor(
        public uuid: string = "",
        public name: string = "",
        public description: string = "",
        public avatar: FileCustom | null = null,
        public banner: FileCustom | null = null,
        public isPublic: boolean = false,
        public isAdmin: boolean = false,
        public isOwner: boolean = false,
  ) { }
}