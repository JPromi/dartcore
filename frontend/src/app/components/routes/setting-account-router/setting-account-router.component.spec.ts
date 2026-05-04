import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingAccountRouterComponent } from './setting-account-router.component';

describe('SettingAccountRouterComponent', () => {
  let component: SettingAccountRouterComponent;
  let fixture: ComponentFixture<SettingAccountRouterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingAccountRouterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingAccountRouterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
