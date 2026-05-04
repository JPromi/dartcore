export class GroupEditRequest {
    constructor (
        public uuid: string | null = null,
        public name: string = "",
        public description: string = "",
        public isPublic: boolean = true,
        public members: string[] = [],
        public avatar: string | null = null,
        public banner: string | null = null
    ) {}
}