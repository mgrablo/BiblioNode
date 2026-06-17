import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { IconInputComponent } from './icon-input.component';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

const meta: Meta<IconInputComponent> = {
  title: 'Shared/Components/IconInput',
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
    type: 'text',
  },
};

export const PasswordInput: Story = {
  args: {
    control: new FormControl<string>('aaa'),
    placeholder: 'Enter your password',
    type: 'password',
  },
};

export const WithIcons: Story = {
  args: {
    placeholder: 'Search...',
    control: new FormControl(''),
    type: 'text',
  },
  render: (args) => ({
    props: args,
    template: `
      <app-icon-input [placeholder]="placeholder" [control]="control" [type]="type" leadingIcon="search" trailingIcon="close" [trailingIconClickable]="true" />
    `,
  }),
};

export const PasswordWithErrors: Story = {
  args: {
    placeholder: 'Enter your password',
    type: 'password',
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
      <app-icon-input [placeholder]="placeholder" [type]="type" [control]="control" [errorMessages]="errorMessages" leadingIcon="lock" trailingIcon="visibility" />
    `,
  }),
};
