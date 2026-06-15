import { applicationConfig, Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { NavbarItemComponent } from './navbar-item.component';
import { provideRouter } from '@angular/router';

const meta: Meta<NavbarItemComponent> = {
  title: 'Shared/Layout/Navbar/NavbarItem',
  component: NavbarItemComponent,
  tags: ['autodocs'],
  decorators: [
    applicationConfig({
      providers: [provideRouter([])],
    }),
  ],
};
export default meta;

type Story = StoryObj<NavbarItemComponent>;

export const Default: Story = {
  args: {
    icon: 'home',
    label: 'Home',
    link: '/home',
  },
};

export const Active: Story = {
  args: {
    icon: 'home',
    label: 'Home',
    link: '/',
  },
};
