import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserForm } from './user-form';

describe('UserForm', () => {
  let component: UserForm;
  let fixture: ComponentFixture<UserForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserForm],
    }).compileComponents();

    fixture = TestBed.createComponent(UserForm);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('mode', 'create');
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit a valid payload on submit', () => {
    let emittedValue: unknown;
    component.submitted.subscribe(value => {
      emittedValue = value;
    });

    component.form.setValue({
      fullName: 'Admin User',
      email: 'admin@modoria.com',
      password: 'secret123',
      enabled: true,
      role: 'ADMIN',
    });

    component.onSubmit();

    expect(emittedValue).toEqual({
      fullName: 'Admin User',
      email: 'admin@modoria.com',
      password: 'secret123',
      enabled: true,
      role: 'ADMIN',
    });
  });
});
