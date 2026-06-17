import { componentWrapperDecorator, Meta, StoryObj } from '@storybook/angular';
import { BookCoverComponent } from './book-cover.component';
import { Book } from '../../domain/models/book.model';

const meta: Meta = {
  title: 'Features/Books/Components/Book Cover',
  component: BookCoverComponent,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  decorators: [
    componentWrapperDecorator((story) => `<div style="width: 200px; height: 200px">${story}</div>`),
  ],
};
export default meta;

type Story = StoryObj<BookCoverComponent>;

const props: Book = {
  id: 1,
  title: 'The Way Of Kings',
  authorName: 'Brandon Sanderson',
  isbn: '1234567890',
  availableCopies: 10,
  coverUrl: 'https://m.media-amazon.com/images/I/81cO02Zz6VL.jpg',
  description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
};

export const Default: Story = {
  args: {
    book: props,
  },
};

export const NoCover: Story = {
  args: {
    book: {
      ...props,
      coverUrl: null,
    },
  },
};
