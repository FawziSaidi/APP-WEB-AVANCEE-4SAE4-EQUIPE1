import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.services';
import { RegisterRequest } from '../auth.module';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit, OnDestroy {
  registerForm: FormGroup;
  isLoading = false;
  showPassword = false;
  errorMessage = '';
  currentYear = new Date().getFullYear();
  userType: 'freelancer' | 'recruiter' = 'freelancer';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.registerForm = this.fb.group({
      fullName:   ['', [Validators.required, Validators.minLength(2)]],
      lastName:   ['', [Validators.required, Validators.minLength(2)]],
      email:      ['', [Validators.required, Validators.email]],
      password:   ['', [Validators.required, Validators.minLength(8)]],
      birthDate:  ['', Validators.required],
      agreeTerms: [false, [Validators.requiredTrue]]
    });
  }

  ngOnInit(): void {
    document.body.classList.add('auth-page');
  }

  ngOnDestroy(): void {
    document.body.classList.remove('auth-page');
  }

  setUserType(type: 'freelancer' | 'recruiter'): void {
    this.userType = type;
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request: RegisterRequest = {
      name:      this.registerForm.value.fullName,
      lastName:  this.registerForm.value.lastName,
      email:     this.registerForm.value.email,
      password:  this.registerForm.value.password,
      role:      this.userType === 'freelancer' ? 'FREELANCER' : 'CLIENT',
      birthDate: this.registerForm.value.birthDate
    };

    this.authService.register(request).subscribe({
      next: () => {
        // ✅ KEYCLOAK : register retourne juste un message texte (pas de token)
        // On redirige vers le login pour que l'utilisateur s'authentifie
        this.isLoading = false;
        this.router.navigate(['/login'], {
          queryParams: { registered: 'true' }
        });
      },
      error: (err) => {
        this.isLoading = false;
        if (typeof err.error === 'string') {
          this.errorMessage = err.error;
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
        }
        console.error('Registration failed', err);
      }
    });
  }

  socialRegister(provider: string): void {
    this.isLoading = true;
    setTimeout(() => {
      this.isLoading = false;
      this.router.navigate(['/app/dashboard']);
    }, 1200);
  }
}