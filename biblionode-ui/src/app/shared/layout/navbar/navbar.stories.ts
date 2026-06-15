import { applicationConfig, Meta, moduleMetadata, StoryObj } from '@storybook/angular';
import { NavbarComponent } from './navbar.component';
import { ThemeToggleComponent } from '../../components/theme-toggle/theme-toggle.component';
import { NavbarItemComponent } from './navbar-item/navbar-item.component';
import { provideRouter } from '@angular/router';

const meta: Meta<NavbarComponent> = {
  title: 'Shared/Layout/Navbar',
  component: NavbarComponent,
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [ThemeToggleComponent, NavbarItemComponent],
    }),
    applicationConfig({
      providers: [provideRouter([])],
    }),
  ],
};
export default meta;

type Story = StoryObj<NavbarComponent>;

export const Default: Story = {};
