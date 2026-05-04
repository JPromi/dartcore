import { Login } from "../entities/login";

export class LoginRequest {

    constructor(
        public username: string = "",
        public password: string = "",
    ) {}

    static fromLogin(login: Login): LoginRequest {
        return new LoginRequest(login.username, login.password);
    }
}