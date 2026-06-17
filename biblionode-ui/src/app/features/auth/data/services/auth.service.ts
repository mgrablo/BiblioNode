import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RegisterRequest } from "../dto/register-request.model";
import { LoginRequest } from "../dto/login-request.model";
import { LoginResponse } from "../dto/login-response.model";
import { catchError, tap } from 'rxjs';
import { ReaderProfile } from '../../../../shared/models/reader-profile.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private tokenSignal = signal<string | null>(localStorage.getItem('token'));

  isAuthenticated = computed<boolean>(() => !!this.tokenSignal());

  login(credentials: LoginRequest) {
    return this.http.post<LoginResponse>('api/auth/login', credentials).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        this.tokenSignal.set(response.token);
      }),
      catchError((error) => {
        localStorage.removeItem('token');
        this.tokenSignal.set(null);
        throw error;
      }),
    );
  }

  register(request: RegisterRequest) {
    return this.http.post<ReaderProfile>('api/auth/register', request);
  }

  logout() {
    localStorage.removeItem('token');
    this.tokenSignal.set(null);
  }
}
