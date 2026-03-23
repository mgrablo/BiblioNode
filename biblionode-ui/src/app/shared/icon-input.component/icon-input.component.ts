import {Component, computed, input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-icon-input',
  imports: [ReactiveFormsModule],
  templateUrl: './icon-input.component.html',
  styleUrl: './icon-input.component.scss',
})
export class IconInputComponent {
  control = input.required<FormControl>();

  placeholder = input<string>('');
  isPassword = input<boolean>(false);
  type = computed(() => this.isPassword() ? 'password' : 'text');
}
