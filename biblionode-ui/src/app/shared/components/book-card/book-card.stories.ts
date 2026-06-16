import { Meta, StoryObj } from '@storybook/angular';
import { BookCardComponent } from './book-card.component';
import { Book } from '../../../features/books/domain/models/book.model';

const meta: Meta<BookCardComponent> = {
  title: 'Shared/Components/BookCard',
  component: BookCardComponent,
  tags: ['autodocs'],
};
export default meta;

type Story = StoryObj<BookCardComponent>;

const props: Book = {
  id: 1,
  title: 'Lorem Ipsum',
  authorName: 'Lorem Ipsum',
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
