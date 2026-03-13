import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ToastService } from '../../../../core/toast/toast.service';
import { AdminCategoryService } from '../../services/admin-category.service';
import { AdminCategories } from './admin-categories';

describe('AdminCategories', () => {
  let component: AdminCategories;
  let fixture: ComponentFixture<AdminCategories>;
  let getCategoriesCalls = 0;

  const categoryService = {
    getCategories: () => {
      getCategoriesCalls += 1;
      return of({
        content: [],
        totalElements: 0,
        totalPages: 0,
        number: 0,
        size: 50,
        first: true,
        last: true,
      });
    },
    createCategory: () => of(),
    updateCategory: () => of(),
    uploadCategoryImage: () => of(),
    deleteCategory: () => of(),
  };

  const toastService = {
    success: () => undefined,
    error: () => undefined,
  };

  beforeEach(async () => {
    getCategoriesCalls = 0;

    await TestBed.configureTestingModule({
      imports: [AdminCategories],
      providers: [
        { provide: AdminCategoryService, useValue: categoryService },
        { provide: ToastService, useValue: toastService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminCategories);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page title', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Categories');
  });

  it('should load categories on init', () => {
    expect(getCategoriesCalls).toBeGreaterThan(0);
  });
});
