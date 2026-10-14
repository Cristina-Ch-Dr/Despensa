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
  usuarioId: number;
  nombre: string;
  apellidos: string;
  email: string;
  token?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenKey = 'despensa_auth_token';
  private readonly userIdKey = 'despensa_user_id';
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  readonly token = signal<string | null>(sessionStorage.getItem(this.tokenKey));
  readonly usuarioId = signal<number | null>(this.readStoredUserId());

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        if (response.token) {
          this.storeToken(response.token);
        }
        this.storeUserId(response.usuarioId);
      }),
    );
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data).pipe(
      tap((response) => {
        if (response.token) {
          this.storeToken(response.token);
        }
        this.storeUserId(response.usuarioId);
      }),
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.userIdKey);
    this.token.set(null);
    this.usuarioId.set(null);
  }

  private storeToken(token: string): void {
    if (!token) {
      return;
    }

    sessionStorage.setItem(this.tokenKey, token);
    this.token.set(token);
  }

  private readStoredUserId(): number | null {
    const storedUserId = sessionStorage.getItem(this.userIdKey);
    if (storedUserId === null) {
      return null;
    }

    const userId = Number(storedUserId);
    if (!Number.isSafeInteger(userId) || userId <= 0) {
      sessionStorage.removeItem(this.userIdKey);
      return null;
    }

    return userId;
  }

  private storeUserId(userId: number): void {
    sessionStorage.setItem(this.userIdKey, String(userId));
    this.usuarioId.set(userId);
  }
}
