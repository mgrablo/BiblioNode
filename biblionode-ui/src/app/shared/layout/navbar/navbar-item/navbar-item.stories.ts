import { Meta, StoryObj } from '@storybook/angular';
import { NavbarItemComponent } from './navbar-item.component';

const meta: Meta<NavbarItemComponent> = {
  title: 'Shared/Layout/Navbar/NavbarItem',
  component: NavbarItemComponent,
  tags: ['autodocs'],
};
export default meta;

type Story = StoryObj<NavbarItemComponent>;

export const Default: Story = {
  args: {
    icon: 'home',
    label: 'Home',
    href: '/',
    isActive: false,
  },
};

export const Active: Story = {
  args: {
    icon: 'home',
    label: 'Home',
    href: '/',
    isActive: true,
  },
};
