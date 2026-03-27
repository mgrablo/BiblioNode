import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { HttpClient, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);

    let store: Record<string, string> = {};
    const mockLocalStorage = {
      getItem: (key: string) => store[key] || null,
      setItem: (key: string, value: string) => {
        store[key] = value;
      },
      removeItem: (key: string) => {
        delete store[key];
      },
      clear: () => {
        store = {};
      },
    };

    vi.spyOn(localStorage, 'getItem').mockImplementation(mockLocalStorage.getItem);
    vi.spyOn(localStorage, 'setItem').mockImplementation(mockLocalStorage.setItem);
    vi.spyOn(localStorage, 'removeItem').mockImplementation(mockLocalStorage.removeItem);
    vi.spyOn(localStorage, 'clear').mockImplementation(mockLocalStorage.clear);
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send correct login request', async () => {
    const creds = { email: 'valid@email.com', password: 'password1234' };
    service.login(creds).subscribe();

    const req = httpTesting.expectOne('api/auth/login', '');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(creds);

    req.flush({ token: 'mocked-jwt' });

    httpTesting.verify();
  });

  it('should set token on login', async () => {
    const loginPromise = firstValueFrom(
      service.login({ email: 'valid@email.com', password: 'password1234' }),
    );

    const req = httpTesting.expectOne('api/auth/login', '');
    expect(req.request.method).toBe('POST');
    req.flush({
      token: 'mocked-jwt',
    });
    expect(await loginPromise).toEqual({ token: 'mocked-jwt' });
    expect(localStorage.getItem('token')).toBe('mocked-jwt');

    httpTesting.verify();
  });

  it('should handle login error', async () => {
    const loginPromise = firstValueFrom(
      service.login({ email: 'user@email.com', password: 'wrongpassword' }),
    );
    const req = httpTesting.expectOne('api/auth/login', '');
    expect(req.request.method).toBe('POST');

    req.flush({ message: 'Invalid credentials' }, { status: 401, statusText: 'Unauthorized' });

    await expect(loginPromise).rejects.toThrow();
    expect(localStorage.getItem('token')).toBeNull();

    httpTesting.verify();
  });

  it('should remove token on logout', () => {
    localStorage.setItem('token', 'mocked-jwt');
    service.logout();
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('should return authentication status', () => {
    const loginPromise = firstValueFrom(
      service.login({ email: 'valid@email.com', password: 'password1234' }),
    );

    const req = httpTesting.expectOne('api/auth/login', '');
    expect(req.request.method).toBe('POST');
    req.flush({
      token: 'mocked-jwt',
    });
    expect(service.isAuthenticated()).toBe(true);

    service.logout();
    expect(service.isAuthenticated()).toBe(false);
  });
});
