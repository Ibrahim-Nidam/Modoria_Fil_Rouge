import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryForm } from './category-form';

describe('CategoryForm', () => {
  let component: CategoryForm;
  let fixture: ComponentFixture<CategoryForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryForm],
    }).compileComponents();

    fixture = TestBed.createComponent(CategoryForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit a trimmed payload on submit', () => {
    let emittedValue: unknown;
    component.submitted.subscribe(value => {
      emittedValue = value;
    });

    component.form.setValue({
      name: '  Dresses  ',
      description: '  Seasonal eveningwear  ',
    });

    component.onSubmit();

    expect(emittedValue).toEqual({
      name: 'Dresses',
      description: 'Seasonal eveningwear',
      imageFile: null,
    });
  });
});
