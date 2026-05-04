export class PageResponse<T> {
  constructor(
    public content: T[] = [],
    public page: number = 0,
    public size: number = 0,
    public totalElements: number = 0,
    public totalPages: number = 0,
    public isFirst: boolean = false,
    public isLast: boolean = false,
    public isEmpty: boolean = false
  ) { }
}