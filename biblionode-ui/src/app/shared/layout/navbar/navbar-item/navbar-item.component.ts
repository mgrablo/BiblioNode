import { Component, input } from '@angular/core';

@Component({
  selector: 'app-navbar-item',
  standalone: true,
  templateUrl: './navbar-item.component.html',
  styleUrl: './navbar-item.component.scss',
})
export class NavbarItemComponent {
  icon = input.required<string>();
  label = input.required<string>();
  href = input<string>('#');
  isActive = input<boolean>(true);
}
