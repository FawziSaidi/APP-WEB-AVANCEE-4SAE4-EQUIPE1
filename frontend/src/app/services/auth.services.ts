import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { AuthRequest, AuthResponse, RegisterRequest } from '../authentification/auth/auth.module';

export interface SessionUser {
  email: string;
  role: 'ADMIN' | 'USER' | 'CLIENT' | 'FREELANCER';
  token: string;
  refreshToken: string;
  expiresAt: number;
  userId: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8222/api/auth';

  private currentUserSubject = new BehaviorSubject<SessionUser | null>(
    this.getUserFromStorage()
  );

  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, request, { responseType: 'text' });
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  refreshAccessToken(): Observable<AuthResponse> {
    const user = this.currentUserSubject.value;
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {
      refreshToken: user?.refreshToken
    });
  }

  setSession(res: AuthResponse, email: string): void {
    const expiresIn = res.expiresIn ?? 300;
    const user: SessionUser = {
      email,
      role: res.role,
      token: res.token,
      refreshToken: (res as any).refreshToken ?? '',
      expiresAt: Date.now() + (expiresIn * 1000),
      userId: res.userId
    };
    localStorage.setItem('sessionUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  updateToken(res: AuthResponse): void {
    const current = this.currentUserSubject.value;
    if (!current) return;
    const expiresIn = res.expiresIn ?? 300;
    const updated: SessionUser = {
      ...current,
      token: res.token,
      refreshToken: (res as any).refreshToken ?? current.refreshToken,
      expiresAt: Date.now() + (expiresIn * 1000)
    };
    localStorage.setItem('sessionUser', JSON.stringify(updated));
    this.currentUserSubject.next(updated);
  }

  getToken(): string | null {
    return this.currentUserSubject.value?.token ?? null;
  }

  getCurrentUserId(): number | null {
    const userId = this.currentUserSubject.value?.userId;
    return (userId !== null && userId !== undefined) ? userId : null;
  }

  isTokenExpired(): boolean {
    const user = this.currentUserSubject.value;
    if (!user || !user.expiresAt) return true;
    return Date.now() > (user.expiresAt - 30000);
  }

  logout(): void {
    localStorage.removeItem('sessionUser');
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    return !!this.currentUserSubject.value;
  }

  getRole(): string | null {
    return this.currentUserSubject.value?.role ?? null;
  }

  getCurrentUser(): SessionUser | null {
    return this.currentUserSubject.value;
  }

  private getUserFromStorage(): SessionUser | null {
    const stored = localStorage.getItem('sessionUser');
    if (!stored) return null;
    try {
      const parsed: SessionUser = JSON.parse(stored);
      if (parsed.userId === null || parsed.userId === undefined) {
        localStorage.removeItem('sessionUser');
        return null;
      }
      return parsed;
    } catch {
      localStorage.removeItem('sessionUser');
      return null;
    }
  }
}