import { TestBed } from '@angular/core/testing';

import { LoAuthService } from './lo-auth.service';

describe('LoAuthService', () => {
  let service: LoAuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoAuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
