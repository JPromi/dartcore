export interface NewGameLocationResponse {
  uuid: string;
  name: string | null;
  description: string | null;
  address: string | null;
  occupied: boolean;
  activeGameUuid: string | null;
}
