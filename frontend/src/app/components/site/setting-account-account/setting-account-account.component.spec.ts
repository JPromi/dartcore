import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingAccountAccountComponent } from './setting-account-account.component';

describe('SettingAccountAccountComponent', () => {
  let component: SettingAccountAccountComponent;
  let fixture: ComponentFixture<SettingAccountAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingAccountAccountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingAccountAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
