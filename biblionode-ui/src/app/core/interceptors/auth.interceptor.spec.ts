import {TestBed} from '@angular/core/testing';
import {HttpClient, HttpInterceptorFn, provideHttpClient, withInterceptors} from '@angular/common/http';

import {authInterceptor} from './auth.interceptor';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

describe('authInterceptor', () => {
  const interceptor: HttpInterceptorFn = (req, next) =>
    TestBed.runInInjectionContext(() => authInterceptor(req, next));

  let httpTesting: HttpTestingController;
  let httpClient: HttpClient;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });

    httpTesting = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
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
});
