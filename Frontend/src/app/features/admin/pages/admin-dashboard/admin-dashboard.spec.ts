import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { AdminCategoryService } from '../../services/admin-category.service';
import { AdminProductService } from '../../services/admin-product.service';
import { AdminUserService } from '../../services/admin-user.service';

import { AdminDashboard } from './admin-dashboard';

describe('AdminDashboard', () => {
  let component: AdminDashboard;
  let fixture: ComponentFixture<AdminDashboard>;

  beforeEach(async () => {
    const userService = {
      getUsers: jasmine.createSpy('getUsers').and.returnValue(
        of([
          { id: 1, fullName: 'Admin User', email: 'admin@modoria.com', enabled: true, roles: ['ADMIN'] },
          { id: 2, fullName: 'Client User', email: 'client@modoria.com', enabled: false, roles: ['CLIENT'] },
        ])
      ),
    };

    const productService = {
      getProducts: jasmine.createSpy('getProducts').and.returnValue(
        of({
          content: [
            {
              id: 1,
              name: 'Summer T-Shirt',
              description: 'Basic tee',
              price: 19.99,
              stock: 12,
              season: 'SUMMER',
              category: { id: 1, name: 'Tops' },
              primaryImagePath: null,
              images: [],
            },
          ],
          totalElements: 1,
          totalPages: 1,
          number: 0,
          size: 1000,
          first: true,
          last: true,
        })
      ),
    };

    const categoryService = {
      getCategories: jasmine.createSpy('getCategories').and.returnValue(
        of({
          content: [{ id: 1, name: 'Tops', description: 'Top wear', productCount: 1 }],
          totalElements: 1,
          totalPages: 1,
          number: 0,
          size: 1000,
          first: true,
          last: true,
        })
      ),
    };

    await TestBed.configureTestingModule({
      imports: [AdminDashboard],
      providers: [
        provideRouter([]),
        { provide: AdminUserService, useValue: userService },
        { provide: AdminProductService, useValue: productService },
        { provide: AdminCategoryService, useValue: categoryService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have 5 stat cards', () => {
    expect(component.stats.length).toBe(5);
  });
});
