import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingGroupRouterComponent } from './setting-group-router.component';

describe('SettingGroupRouterComponent', () => {
  let component: SettingGroupRouterComponent;
  let fixture: ComponentFixture<SettingGroupRouterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingGroupRouterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingGroupRouterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
