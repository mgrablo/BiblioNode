import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';

export const routes: Routes = [
  {
    path: 'auth',
    children: [{ path: 'login', component: LoginComponent }],
  },
];
