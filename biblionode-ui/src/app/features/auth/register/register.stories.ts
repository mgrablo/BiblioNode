import { RegisterComponent } from './register.component';
import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';

const meta: Meta<RegisterComponent> = {
  title: 'Features/Auth/Register',
  component: RegisterComponent,
  tags: ['autodocs'],
  parameters: { layout: 'centered' },
  decorators: [
    moduleMetadata({
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: { register: () => of({}) } },
        { provide: Router, useValue: { navigate: () => {} } },
      ],
    }),
  ],
};
export default meta;

type Story = StoryObj<RegisterComponent>;

export const Default: Story = {};
