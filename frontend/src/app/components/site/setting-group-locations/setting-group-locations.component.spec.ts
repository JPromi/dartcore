import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingGroupLocationsComponent } from './setting-group-locations.component';

describe('SettingGroupLocationsComponent', () => {
  let component: SettingGroupLocationsComponent;
  let fixture: ComponentFixture<SettingGroupLocationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingGroupLocationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingGroupLocationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
