export class RegistrationRequest {
    constructor(
        public username: string = "",
        public password: string = "",
        public email: string = "",
        public firstName: string = "",
        public lastName: string = "",
        public dateOfBirth: Date = new Date(),
    ) {}
}