import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { AdminLayout } from './admin-layout';

describe('AdminLayout', () => {
  let component: AdminLayout;
  let fixture: ComponentFixture<AdminLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminLayout],
      providers: [provideRouter([]), provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle sidebar collapsed state', () => {
    expect(component.sidebarCollapsed()).toBe(false);
    Object.defineProperty(window, 'innerWidth', { value: 1200, writable: true });
    component.toggleSidebar();
    expect(component.sidebarCollapsed()).toBe(true);
  });

  it('should toggle mobile open state on small screens', () => {
    expect(component.mobileOpen()).toBe(false);
    Object.defineProperty(window, 'innerWidth', { value: 800, writable: true });
    component.toggleSidebar();
    expect(component.mobileOpen()).toBe(true);
  });

  it('should close mobile sidebar', () => {
    component.mobileOpen.set(true);
    component.closeMobile();
    expect(component.mobileOpen()).toBe(false);
  });
});
