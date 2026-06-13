import { Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { NavbarComponent } from './navbar.component';
import { ThemeToggleComponent } from '../../theme-toggle/theme-toggle.component';

const meta: Meta<NavbarComponent> = {
  title: 'Layout/Navbar',
  component: NavbarComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [ThemeToggleComponent],
    }),
  ],
};
export default meta;

type Story = StoryObj<NavbarComponent>;

export const Default: Story = {};
