export class SessionAccountResponse {
    constructor (
        public uuid: string = "",
        public username: string = "",
        public email: string = "",
        public firstName: string = "",
        public lastName: string = "",
        public avatar: string | null = null,
        public registrationTimestamp: string | null = null,
    ) {}
}