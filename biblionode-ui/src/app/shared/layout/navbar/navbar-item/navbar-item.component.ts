import { Component, input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar-item',
  standalone: true,
  templateUrl: './navbar-item.component.html',
  styleUrl: './navbar-item.component.scss',
  imports: [RouterLink, RouterLinkActive],
})
export class NavbarItemComponent {
  icon = input.required<string>();
  label = input.required<string>();
  link = input<string>('#');
}
