export class LocationResponse {
    constructor(
        public uuid: string = '',
        public name: string | null = null,
        public description: string | null = null,
        public isPublic: boolean = true,
        public address: string | null = null,
        public screens: any[] = [],
        public inputClients: any[] = []
    ) {}
}
