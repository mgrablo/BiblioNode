import { Component, effect, signal } from '@angular/core';

@Component({
  selector: 'app-theme-toggle',
  imports: [],
  templateUrl: './theme-toggle.component.html',
  styleUrl: './theme-toggle.component.scss',
})
export class ThemeToggleComponent {
  isDark = signal<boolean>(
    localStorage.getItem('theme') === 'dark' ||
      (!('theme' in localStorage) && window.matchMedia('(prefers-color-scheme: dark)').matches),
  );

  constructor() {
    effect(() => {
      const htmlTag = window.document.documentElement;
      if (this.isDark()) {
        htmlTag.classList.add('dark');
        localStorage.setItem('theme', 'dark');
      } else {
        htmlTag.classList.remove('dark');
        localStorage.setItem('theme', 'light');
      }
    });
  }

  toggleTheme() {
    this.isDark.update((dark) => !dark);
  }
}
