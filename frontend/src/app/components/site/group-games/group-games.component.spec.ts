import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupGamesComponent } from './group-games.component';

describe('GroupGamesComponent', () => {
  let component: GroupGamesComponent;
  let fixture: ComponentFixture<GroupGamesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupGamesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GroupGamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
