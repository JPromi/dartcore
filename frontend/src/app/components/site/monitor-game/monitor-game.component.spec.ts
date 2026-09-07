import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitorGameComponent } from './monitor-game.component';

describe('MonitorGameComponent', () => {
  let component: MonitorGameComponent;
  let fixture: ComponentFixture<MonitorGameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MonitorGameComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MonitorGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
