import { TestBed } from '@angular/core/testing';

import { LoValidationService } from './lo-validation.service';

describe('LoValidationService', () => {
  let service: LoValidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoValidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
