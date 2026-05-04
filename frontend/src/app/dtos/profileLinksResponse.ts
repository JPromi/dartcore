export class ProfileLinksResponse {
    constructor(
        public web: string | null = null,
        public x: string | null = null,
        public instagram: string | null = null,
        public github: string | null = null,
        public youtube: string | null = null,
        public twitch: string | null = null,
        public facebook: string | null = null,
    ) {}
}