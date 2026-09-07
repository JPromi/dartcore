import { LocationResourceResponse } from "./locationResourceResponse";

export class LocationResponse {
    constructor(
        public name: string | null = null,
        public description: string | null = null,
        public isPublic: boolean = true,
        public address: string | null = null,
        public screens: {
            uuid: string,
            name: string
        }[] = [],
        public clients: {
            uuid: string,
            name: string
        }[] = []
    ) {}
}
