import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminCategories } from './admin-categories';

describe('AdminCategories', () => {
  let component: AdminCategories;
  let fixture: ComponentFixture<AdminCategories>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminCategories],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminCategories);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page title', () => {
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Categories');
  });
});
