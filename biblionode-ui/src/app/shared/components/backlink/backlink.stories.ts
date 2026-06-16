import { applicationConfig, Meta, StoryObj } from '@storybook/angular';
import { BacklinkComponent } from './backlink.component';
import { provideRouter } from '@angular/router';

const meta: Meta = {
  title: 'Shared/Components/Backlink',
  tags: ['autodocs'],
  component: BacklinkComponent,
  decorators: [
    applicationConfig({
      providers: [provideRouter([])],
    }),
  ],
};
export default meta;

type Story = StoryObj<BacklinkComponent>;

export const Default: Story = {
  render: () => ({ template: '<app-backlink>Back to homepage</app-backlink>' }),
};
