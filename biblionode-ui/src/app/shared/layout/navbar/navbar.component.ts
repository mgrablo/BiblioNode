import { Component } from '@angular/core';
import { ThemeToggleComponent } from '../../components/theme-toggle/theme-toggle.component';
import { NavbarItemComponent } from './navbar-item/navbar-item.component';

@Component({
  selector: 'app-navbar',
  imports: [ThemeToggleComponent, NavbarItemComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent {}
