import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-backlink',
  standalone: true,
  imports: [RouterLink],
  template: `
    <a
      class="text-app-subtext dark:text-app-subtext-dark hover:text-app-primary dark:hover:text-app-primary-dark inline-flex items-center gap-1.5 text-sm font-semibold"
      [routerLink]="route()"
    >
      <span class="material-symbols-outlined">chevron_left</span>
      <ng-content></ng-content>
    </a>
  `,
})
export class BacklinkComponent {
  route = input.required<string>();
}
