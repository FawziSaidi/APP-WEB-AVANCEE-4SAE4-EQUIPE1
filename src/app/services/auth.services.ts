import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { AuthRequest, AuthResponse, RegisterRequest } from '../authentification/auth/auth.module';

export interface SessionUser {
  email: string;
  role: 'ADMIN' | 'USER' | 'CLIENT' | 'FREELANCER';
  token: string;
  refreshToken: string;
  expiresAt: number;   // timestamp ms : Date.now() + expiresIn*1000
  userId: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8222/api/auth';

  private currentUserSubject = new BehaviorSubject<SessionUser | null>(
    this.getUserFromStorage()
  );

  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // ✅ FIX : vérifier le token au démarrage et le rafraîchir si nécessaire
    this.checkAndRefreshToken();
  }

  // ---------- API ----------

  register(request: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, request, { responseType: 'text' });
  }

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
  }

  // ✅ FIX NOUVEAU : appel au backend pour rafraîchir le token
  refreshAccessToken(): Observable<AuthResponse> {
    const user = this.currentUserSubject.value;
    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, {
      refreshToken: user?.refreshToken
    });
  }

  // ---------- SESSION ----------

  setSession(res: AuthResponse, email: string): void {
    const expiresIn = res.expiresIn ?? 300;  // défaut 5 min si non fourni
    const user: SessionUser = {
      email,
      role: res.role,
      token: res.token,
      // ✅ FIX : stocker le refresh token
      refreshToken: (res as any).refreshToken ?? '',
      expiresAt: Date.now() + (expiresIn * 1000),
      userId: res.userId
    };
    localStorage.setItem('sessionUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  // ✅ FIX : mise à jour de la session après refresh (sans changer email/role/userId)
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
    // ✅ FIX : vérifier null/undefined (pas falsy) — évite d'effacer userId=0
    return (userId !== null && userId !== undefined) ? userId : null;
  }

  // ✅ FIX : vérifier si le token est expiré (avec 30s de marge)
  isTokenExpired(): boolean {
    const user = this.currentUserSubject.value;
    if (!user || !user.expiresAt) return true;
    return Date.now() > (user.expiresAt - 30000); // 30s avant expiration
  }

  // ✅ FIX : rafraîchir le token automatiquement au démarrage si expiré
  private checkAndRefreshToken(): void {
    if (!this.isLoggedIn()) return;
    if (!this.isTokenExpired()) return;

    const user = this.currentUserSubject.value;
    if (!user?.refreshToken) {
      this.logout();
      return;
    }

    this.refreshAccessToken().subscribe({
      next: (res) => this.updateToken(res),
      error: () => this.logout()
    });
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
      // ✅ FIX : avant "if (!parsed.userId)" effaçait la session si userId=0
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