export class ProfileLightResponse {
    constructor(
        public uuid: string = "",
        public username: string = "",
        public avatar: string | null = null,
        public visibility: string = "",
    ) {}
}