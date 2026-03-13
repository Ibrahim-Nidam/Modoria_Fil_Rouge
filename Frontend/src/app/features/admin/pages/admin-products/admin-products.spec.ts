import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ToastService } from '../../../../core/toast/toast.service';
import { AdminCategoryService } from '../../services/admin-category.service';
import { AdminProductService } from '../../services/admin-product.service';

import { AdminProducts } from './admin-products';

describe('AdminProducts', () => {
  let component: AdminProducts;
  let fixture: ComponentFixture<AdminProducts>;

  beforeEach(async () => {
    const productService = {
      getProducts: jasmine.createSpy('getProducts').and.returnValue(
        of({
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: 50,
          first: true,
          last: true,
        })
      ),
      getProductById: jasmine.createSpy('getProductById').and.returnValue(
        of({
          id: 1,
          name: 'Sample Product',
          description: 'Description',
          price: 99.9,
          stock: 10,
          season: 'SUMMER',
          category: { id: 1, name: 'Category' },
          primaryImagePath: null,
          images: [],
        })
      ),
      createProduct: jasmine.createSpy('createProduct').and.returnValue(of(null)),
      updateProduct: jasmine.createSpy('updateProduct').and.returnValue(of(null)),
      deleteProduct: jasmine.createSpy('deleteProduct').and.returnValue(of(void 0)),
      uploadProductImages: jasmine.createSpy('uploadProductImages').and.returnValue(of([])),
      deleteProductImage: jasmine.createSpy('deleteProductImage').and.returnValue(of(void 0)),
      setPrimaryProductImage: jasmine.createSpy('setPrimaryProductImage').and.returnValue(of(null)),
    };

    const categoryService = {
      getCategories: jasmine.createSpy('getCategories').and.returnValue(
        of({
          content: [],
          totalElements: 0,
          totalPages: 0,
          number: 0,
          size: 50,
          first: true,
          last: true,
        })
      ),
    };

    const toastService = {
      success: jasmine.createSpy('success'),
      error: jasmine.createSpy('error'),
      warning: jasmine.createSpy('warning'),
    };

    await TestBed.configureTestingModule({
      imports: [AdminProducts],
      providers: [
        { provide: AdminProductService, useValue: productService },
        { provide: AdminCategoryService, useValue: categoryService },
        { provide: ToastService, useValue: toastService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminProducts);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page title', () => {
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Products');
  });
});
