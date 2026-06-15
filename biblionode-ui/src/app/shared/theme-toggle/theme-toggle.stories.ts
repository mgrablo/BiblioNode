import { ThemeToggleComponent } from './theme-toggle.component';
import { Meta, StoryObj } from '@storybook/angular';
import { signal } from '@angular/core';

const meta: Meta<ThemeToggleComponent> = {
  title: 'ThemeToggle',
  component: ThemeToggleComponent,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
};

export default meta;
type Story = StoryObj<ThemeToggleComponent>;

export const Default: Story = {};
