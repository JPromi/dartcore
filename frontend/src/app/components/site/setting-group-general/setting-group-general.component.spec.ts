import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingGroupGeneralComponent } from './setting-group-general.component';

describe('SettingGroupGeneralComponent', () => {
  let component: SettingGroupGeneralComponent;
  let fixture: ComponentFixture<SettingGroupGeneralComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingGroupGeneralComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingGroupGeneralComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
