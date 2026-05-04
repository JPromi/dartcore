import { TestBed } from '@angular/core/testing';

import { LoStorageService } from './lo-storage.service';

describe('LoStorageService', () => {
  let service: LoStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoStorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
