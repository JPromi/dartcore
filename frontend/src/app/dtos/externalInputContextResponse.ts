export class ExternalInputContextResponse {
    constructor(
        public groupUuid: string = "",
        public locationUuid: string = "",
        public activeGameUuid: string | null = null
    ) { }
}
