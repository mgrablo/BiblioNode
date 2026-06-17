import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { IconInputComponent } from '../../../shared/components/icon-input.component/icon-input.component';
import { AuthService } from '../data/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, IconInputComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  loginForm = new FormGroup({
    email: new FormControl<string>('', {
      validators: [Validators.required, Validators.email],
      nonNullable: true,
    }),
    password: new FormControl<string>('', {
      validators: Validators.required,
      nonNullable: true,
    }),
  });

  get email() {
    return this.loginForm.get('email') as FormControl<string>;
  }
  get password() {
    return this.loginForm.get('password') as FormControl<string>;
  }

  passwordVisible = signal(false);
  incorrectCredentialsMessage = signal(false);

  signIn() {
    if (this.loginForm.valid) {
      this.authService.login({ email: this.email.value, password: this.password.value }).subscribe({
        next: () => this.router.navigate(['/']),
        error: (err) => {
          this.incorrectCredentialsMessage.set(true);
          this.loginForm.markAsTouched();
        },
      });
    }
  }

  moveToSignUpPage() {
    this.router.navigate(['/auth/signup']);
  }

  togglePasswordVisibility() {
    this.passwordVisible.update((visible) => !visible);
  }

  hideIncorrectCredentialsMessage() {
    this.incorrectCredentialsMessage.set(false);
  }
}
