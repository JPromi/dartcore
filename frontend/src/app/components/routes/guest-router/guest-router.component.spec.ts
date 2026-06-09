import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuestRouterComponent } from './guest-router.component';

describe('GuestRouterComponent', () => {
  let component: GuestRouterComponent;
  let fixture: ComponentFixture<GuestRouterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestRouterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuestRouterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
