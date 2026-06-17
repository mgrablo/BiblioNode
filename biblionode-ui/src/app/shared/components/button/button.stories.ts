import { Meta, StoryObj } from '@storybook/angular';
import { ButtonComponent } from './button.component';

const meta: Meta = {
  title: 'Shared/Components/Button',
  tags: ['autodocs'],
  component: ButtonComponent,
};
export default meta;

type Story = StoryObj<ButtonComponent>;

export const Default: Story = {
  args: {
    disabled: false,
    variant: 'primary',
  },
  render: (args) => ({
    props: args,
    template: `<app-button [disabled]="disabled" [variant]="variant">Click me</app-button>`,
  }),
};

export const Secondary: Story = {
  args: {
    disabled: false,
    variant: 'secondary',
  },
  render: (args) => ({
    props: args,
    template: `<app-button [disabled]="disabled" [variant]="variant">Click me</app-button>`,
  }),
};

export const Disabled: Story = {
  args: {
    disabled: true,
    variant: 'secondary',
  },
  render: (args) => ({
    props: args,
    template: `<app-button [disabled]="disabled" [variant]="variant">Click me</app-button>`,
  }),
};
