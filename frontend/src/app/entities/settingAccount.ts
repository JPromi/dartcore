export class SettingAccount {
    constructor(
        public uuid: string = "",
        public firstName: string = "",
        public lastName: string = "",
        public email: string = "",
        public passwordOld: string = "",
        public passwordNew: string = "",
        public passwordNewConfirm: string = "",
        // 2fa
    ) {}
}