import { InvitationStatusAccountEnum } from "../enums/invitationStatusAccountEnum";

export class GroupAdminMember {
    constructor (
        public uuid: string = '',
        public username: string | null = null,
        public avatar: string | null = null,
        public isAdmin: boolean = false,
        public isOwner: boolean = false,
        public status: InvitationStatusAccountEnum | null = null
    ) {}
}