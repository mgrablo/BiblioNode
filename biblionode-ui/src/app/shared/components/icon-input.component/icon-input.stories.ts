import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { IconInputComponent } from './icon-input.component';
import { InputSignal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

const meta: Meta<IconInputComponent> = {
  title: 'Shared/IconInput',
  component: IconInputComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [ReactiveFormsModule],
    }),
  ],
};
export default meta;

type Story = StoryObj<IconInputComponent>;

export const Default: Story = {
  args: {
    control: new FormControl<string>(''),
    placeholder: 'Placeholder...',
    isPassword: false,
  },
};

export const PasswordInput: Story = {
  args: {
    control: new FormControl<string>('aaa'),
    placeholder: 'Enter your password',
    isPassword: true,
  },
};

export const WithIcons: Story = {
  args: {
    placeholder: 'Search...',
    control: new FormControl(''),
    isPassword: false,
  },
  render: (args) => ({
    props: args,
    template: `
      <app-icon-input [placeholder]="placeholder" [control]="control" [isPassword]="isPassword">
        <span start class="material-symbols-outlined">search</span>
        <span end class="material-symbols-outlined" style="cursor: pointer">close</span>
      </app-icon-input>
    `,
  }),
};

export const PasswordWithErrors: Story = {
  args: {
    placeholder: 'Enter your password',
    isPassword: true,
    errorMessages: { required: 'Please enter your password' },
    control: (() => {
      const fc = new FormControl('', Validators.required);
      fc.markAsTouched();
      fc.setErrors({ required: true });
      return fc;
    })(),
  },
  render: (args) => ({
    props: args,
    template: `
      <app-icon-input [placeholder]="placeholder" [isPassword]="isPassword" [control]="control" [errorMessages]="errorMessages">
        <span start class="material-symbols-outlined">lock</span>
        <span end class="material-symbols-outlined" style="cursor: pointer">visibility</span>
      </app-icon-input>
    `,
  }),
};
