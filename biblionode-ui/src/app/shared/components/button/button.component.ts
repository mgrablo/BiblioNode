import { Component, computed, input, output } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button.component.html',
})
export class ButtonComponent {
  variant = input<'primary' | 'secondary'>('primary');
  type = input<'button' | 'submit' | 'reset'>('button');
  disabled = input<boolean>(false);

  onClick = output<MouseEvent>();

  computedClasses = computed(() => {
    if (this.disabled()) {
      return 'bg-app-disabled text-app-disabled-text dark:bg-app-disabled-dark dark:text-app-disabled-text-dark';
    }

    if (this.variant() === 'primary') {
      return `bg-app-primary text-app-primary-text hover:bg-app-primary-hover
              dark:bg-app-primary-dark dark:text-app-primary-text-dark dark:hover:bg-app-primary-dark-hover`;
    }

    if (this.variant() === 'secondary') {
      return `border-app-primary hover:bg-app-primary-hover hover:border-app-primary-hover border-2
              dark:border-app-primary-dark dark:hover:bg-app-primary-dark-hover dark:hover:border-app-primary-dark-hover
              text-app-primary dark:text-app-primary-dark hover:text-app-background dark:hover:text-app-background-dark`;
    }

    return '';
  });

  handleClick(event: MouseEvent) {
    if (!this.disabled()) {
      this.onClick.emit(event);
    }
  }
}
