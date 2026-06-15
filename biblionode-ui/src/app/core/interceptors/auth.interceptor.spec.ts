import { TestBed } from '@angular/core/testing';
import {
  HttpClient,
  HttpInterceptorFn,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';

import { authInterceptor } from './auth.interceptor';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

describe('authInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) =>
    TestBed.runInInjectionContext(() => authInterceptor(req, next));

  let httpTesting: HttpTestingController;
  let httpClient: HttpClient;
  let authService: AuthService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        {
          provide: AuthService,
          useValue: { logout: vi.fn() },
        },
        {
          provide: Router,
          useValue: { navigate: vi.fn() },
        },
      ],
    });

    httpTesting = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);

    localStorage.clear();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  it('should add Authorization header when token is present', () => {
    const token = 'fake-jwt-token';
    localStorage.setItem('token', token);

    httpClient.get('api/test').subscribe();

    const req = httpTesting.expectOne('api/test');
    expect(req.request.headers.has('Authorization')).toBe(true);
    expect(req.request.headers.get('Authorization')).toBe(`Bearer ${token}`);
  });

  it('should not add Authorization header when token is absent', () => {
    httpClient.get('api/test').subscribe();

    const req = httpTesting.expectOne('api/test');
    expect(req.request.headers.has('Authorization')).toBe(false);
  });

  it('should handle 401 error by logging out', () => {
    httpClient.get('api/test').subscribe({
      error: () => {},
    });

    const req = httpTesting.expectOne('api/test');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(authService.logout).toHaveBeenCalled();
  });

  it('should navigate to login page on 401 error', async () => {
    httpClient.get('api/test').subscribe({
      error: () => {},
    });

    const req = httpTesting.expectOne('api/test');
    req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

    expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
  });
});
