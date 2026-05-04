export class GroupLightResponse {
  constructor(
    public uuid: string = "",
    public name: string = "",
    public avatar: string | null = null,
    public banner: string | null = null,
    public membersTotal: number = 0,
    public isMember: boolean = false,
    public isPublic: boolean = false,
  ) { }
}