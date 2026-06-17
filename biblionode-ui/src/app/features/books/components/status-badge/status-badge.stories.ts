import { componentWrapperDecorator, Meta } from '@storybook/angular';
import { StatusBadgeComponent } from './status-badge.component';

const meta: Meta = {
  title: 'Features/Books/Components/Status Badge',
  tags: ['autodocs'],
  component: StatusBadgeComponent,
  decorators: [
    componentWrapperDecorator(
      (story) =>
        `<div class="bg-app-background dark:bg-app-background-dark px-2 py-2">${story}</div>`,
    ),
  ],
};
export default meta;

export const Default = {
  render: () => ({ template: '<p appStatusBadge [availableCopies]="10"></p>' }),
};

export const OneLeft = {
  render: () => ({ template: '<p appStatusBadge [availableCopies]="1"></p>' }),
};

export const OutOfStock = {
  render: () => ({ template: '<p appStatusBadge [availableCopies]="0"></p>' }),
};
