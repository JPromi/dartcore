import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GamePlayerTileComponent } from './game-player-tile.component';

describe('GamePlayerTileComponent', () => {
  let component: GamePlayerTileComponent;
  let fixture: ComponentFixture<GamePlayerTileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GamePlayerTileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GamePlayerTileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
