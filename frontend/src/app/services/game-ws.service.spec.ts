import { TestBed } from '@angular/core/testing';

import { GameWsService } from './game-ws.service';

describe('GameWsService', () => {
  let service: GameWsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameWsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
