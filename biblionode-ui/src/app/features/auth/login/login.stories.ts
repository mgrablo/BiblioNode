import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { LoginComponent } from './login.component';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { signal } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { of } from 'rxjs';
import { Router } from '@angular/router';

const meta: Meta<LoginComponent> = {
  title: 'Features/Auth/LoginPage',
  component: LoginComponent,
  parameters: { layout: 'centered' },
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: { login: () => of({}) } },
        { provide: Router, useValue: { navigate: () => {} } },
      ],
    }),
  ],
};
export default meta;

type Story = StoryObj<LoginComponent>;

export const Default: Story = {};

export const WithErrors: Story = {
  args: {
    incorrectCredentialsMessage: signal(true),
  },
};

export const PasswordVisibility: Story = {
  args: {
    loginForm: new FormGroup({
      email: new FormControl<string>('', {
        validators: [Validators.required, Validators.email],
        nonNullable: true,
      }),
      password: new FormControl<string>('1234', {
        validators: Validators.required,
        nonNullable: true,
      }),
    }),
    passwordVisible: signal(true),
  },
};
