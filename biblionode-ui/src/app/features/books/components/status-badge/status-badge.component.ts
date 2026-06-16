import { Component, computed, input, signal } from '@angular/core';

@Component({
  selector: '[appStatusBadge]',
  standalone: true,
  imports: [],
  host: {
    '[class]': 'hostClasses()',
  },
  template: `
    <span aria-hidden="true" class="h-2 w-2 rounded-full bg-current"></span>
    {{ statusText() }}
  `,
})
export class StatusBadgeComponent {
  availableCopies = input.required<number>();

  isAvailable = computed(() => this.availableCopies() > 0);

  statusText = computed(() => {
    if (!this.isAvailable()) {
      return 'Checked out';
    }
    const txt = this.availableCopies() === 1 ? 'copy' : 'copies';
    return `${this.availableCopies()} ${txt} left`;
  });

  hostClasses = computed(() => {
    const baseClasses = 'm-0 inline-flex items-center gap-1.5 font-semibold text-sm';

    const colorClasses = this.isAvailable()
      ? 'text-app-primary dark:text-app-primary-dark'
      : 'text-app-error dark:text-app-error-dark';

    return `${baseClasses} ${colorClasses}`;
  });
}
