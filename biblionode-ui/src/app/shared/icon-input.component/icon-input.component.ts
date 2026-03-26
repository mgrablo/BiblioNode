import { Component, computed, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-icon-input',
  imports: [ReactiveFormsModule],
  templateUrl: './icon-input.component.html',
  styleUrl: './icon-input.component.scss',
})
export class IconInputComponent {
  control = input.required<FormControl>();
  errorMessages = input<Record<string, string>>({});

  placeholder = input<string>('');
  isPassword = input<boolean>(false);
  type = computed(() => (this.isPassword() ? 'password' : 'text'));

  showErrors() {
    const { invalid, touched, errors } = this.control();
    return invalid && touched && errors;
  }

  displayErrorMessage() {
    if (this.showErrors()) {
      const firstErrorKey = Object.keys(this.control().errors!)[0];
      return this.errorMessages()[firstErrorKey] || 'Invalid input';
    }
    return 'Unknown error';
  }
}
