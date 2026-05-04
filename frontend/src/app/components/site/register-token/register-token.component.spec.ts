import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterTokenComponent } from './register-token.component';

describe('RegisterTokenComponent', () => {
  let component: RegisterTokenComponent;
  let fixture: ComponentFixture<RegisterTokenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterTokenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterTokenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
