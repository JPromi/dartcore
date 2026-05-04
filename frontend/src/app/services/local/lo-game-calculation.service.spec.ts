import { TestBed } from '@angular/core/testing';

import { LoGameCalculationService } from './lo-game-calculation.service';

describe('LoGameCalculationService', () => {
  let service: LoGameCalculationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoGameCalculationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
