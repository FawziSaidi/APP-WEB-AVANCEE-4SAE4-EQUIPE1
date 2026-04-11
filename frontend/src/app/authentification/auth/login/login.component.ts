import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.services';
import { AuthRequest, AuthResponse } from '../auth.module';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  isLoading = false;
  showPassword = false;
  errorMessage = '';
  currentYear = new Date().getFullYear();

  user: { email: string; role: string } | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  ngOnInit(): void {
    document.body.classList.add('auth-page');

    // Si déjà connecté, rediriger directement
    if (this.authService.isLoggedIn()) {
      const role = this.authService.getRole();
      this.router.navigate(role === 'ADMIN' ? ['/admin/dashboard'] : ['/app']);
    }
  }

  ngOnDestroy(): void {
    document.body.classList.remove('auth-page');
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  logout(): void {
    this.authService.logout();
    this.user = null;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const authRequest: AuthRequest = {
      email: this.loginForm.value.email,
      password: this.loginForm.value.password
    };

    this.authService.login(authRequest).subscribe({
      next: (res: AuthResponse) => {
        this.isLoading = false;

        // ✅ KEYCLOAK : on sauvegarde le token Keycloak via setSession
        // L'intercepteur HTTP l'enverra automatiquement dans chaque requête
        this.authService.setSession(res, authRequest.email);

        // Compatibilité avec ancien code qui lit directement localStorage
        localStorage.setItem('token', res.token);
        localStorage.setItem('userName', authRequest.email);
        localStorage.setItem('role', res.role);
        localStorage.setItem('userId', res.userId.toString()); // ✅ était res.id

        this.user = { email: authRequest.email, role: res.role };

        if (res.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/app']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        // Afficher le message d'erreur du backend si disponible
        if (typeof err.error === 'string') {
          this.errorMessage = err.error;
        } else {
          this.errorMessage = 'Login failed. Please check your credentials.';
        }
        console.error('Login error', err);
      }
    });
  }

  socialLogin(provider: string): void {
    this.isLoading = true;
    setTimeout(() => {
      this.isLoading = false;
      this.router.navigate(['/app/dashboard']);
    }, 1200);
  }
}