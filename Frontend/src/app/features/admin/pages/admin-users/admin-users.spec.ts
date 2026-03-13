import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ToastService } from '../../../../core/toast/toast.service';
import { AdminUserService } from '../../services/admin-user.service';
import { AdminUsers } from './admin-users';

describe('AdminUsers', () => {
  let component: AdminUsers;
  let fixture: ComponentFixture<AdminUsers>;
  let getUsersCalls = 0;

  const userService = {
    getUsers: () => {
      getUsersCalls += 1;
      return of([]);
    },
    createUser: () => of(),
    updateUser: () => of(),
    deleteUser: () => of(),
  };

  const toastService = {
    success: () => undefined,
    error: () => undefined,
  };

  beforeEach(async () => {
    getUsersCalls = 0;

    await TestBed.configureTestingModule({
      imports: [AdminUsers],
      providers: [
        { provide: AdminUserService, useValue: userService },
        { provide: ToastService, useValue: toastService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminUsers);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page title', () => {
    const el = fixture.nativeElement as HTMLElement;
    expect(el.textContent).toContain('Users');
  });

  it('should load users on init', () => {
    expect(getUsersCalls).toBeGreaterThan(0);
  });
});
