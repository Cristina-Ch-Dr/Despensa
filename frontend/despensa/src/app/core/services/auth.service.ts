import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellidos: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token?: string;
  usuario?: unknown;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenKey = 'despensa_auth_token';
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  readonly token = signal<string | null>(sessionStorage.getItem(this.tokenKey));

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        if (response.token) {
          this.storeToken(response.token);
        }
      }),
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
      tap((response) => {
        if (response.token) {
          this.storeToken(response.token);
        }
      }),
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.tokenKey);
    this.token.set(null);
  }

  private storeToken(token: string): void {
    if (!token) {
      return;
    }

    sessionStorage.setItem(this.tokenKey, token);
    this.token.set(token);
  }
}
