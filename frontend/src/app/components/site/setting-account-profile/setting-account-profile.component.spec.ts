import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingAccountProfileComponent } from './setting-account-profile.component';

describe('SettingAccountProfileComponent', () => {
  let component: SettingAccountProfileComponent;
  let fixture: ComponentFixture<SettingAccountProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingAccountProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingAccountProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
