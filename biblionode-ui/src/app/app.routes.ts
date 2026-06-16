import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { BrowseComponent } from './features/books/browse/browse.component';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'signup', component: RegisterComponent },
    ],
  },
  {
    path: 'browse',
    component: BrowseComponent,
  },
];
