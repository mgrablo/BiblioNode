import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginRequest, LoginResponse } from '../../models/auth.model';
import { catchError, tap } from 'rxjs';

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

  logout() {
    localStorage.removeItem('token');
    this.tokenSignal.set(null);
  }
}
