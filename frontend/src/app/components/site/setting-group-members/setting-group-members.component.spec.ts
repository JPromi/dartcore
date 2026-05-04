import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingGroupMembersComponent } from './setting-group-members.component';

describe('SettingGroupMembersComponent', () => {
  let component: SettingGroupMembersComponent;
  let fixture: ComponentFixture<SettingGroupMembersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingGroupMembersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingGroupMembersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
