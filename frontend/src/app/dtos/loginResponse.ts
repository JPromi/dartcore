export class LoginResponse {
    constructor(
        public token: string = "",
        public totpRequired: boolean = false,
        public error: string = "",
    ) {}
}