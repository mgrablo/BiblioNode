import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { IconInputComponent } from '../../../shared/components/icon-input.component/icon-input.component';
import { AuthValidators } from '../../../core/validators/auth.validators';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, IconInputComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  registerForm = new FormGroup(
    {
      fullName: new FormControl('', {
        validators: Validators.required,
        nonNullable: true,
      }),
      email: new FormControl<string>('', {
        validators: [Validators.required, Validators.email],
        nonNullable: true,
      }),
      password: new FormControl<string>('', {
        validators: [Validators.required, Validators.minLength(8), AuthValidators.strongPassword()],
        nonNullable: true,
      }),
      confirmPassword: new FormControl<string>('', {
        validators: [Validators.required, Validators.minLength(8)],
        nonNullable: true,
      }),
    },
    {
      validators: AuthValidators.passwordMatch,
    },
  );

  passwordVisible = signal(false);
  isLoading = signal(false);

  get fullName() {
    return this.registerForm.get('fullName') as FormControl<string>;
  }
  get email() {
    return this.registerForm.get('email') as FormControl<string>;
  }
  get password() {
    return this.registerForm.get('password') as FormControl<string>;
  }
  get confirmPassword() {
    return this.registerForm.get('confirmPassword') as FormControl<string>;
  }

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading.set(true);
      this.authService
        .register({
          fullName: this.fullName.value,
          email: this.email.value,
          password: this.password.value,
        })
        .subscribe({
          next: () => this.router.navigate(['/auth/login']),
          error: (err) => {
            console.error('Registration failed', err);
            this.isLoading.set(false);
          },
        });
    }
  }

  moveToSignInPage() {
    this.router.navigate(['/auth/login']);
  }
}
