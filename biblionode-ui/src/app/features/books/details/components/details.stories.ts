import { applicationConfig, Meta } from '@storybook/angular';
import { DetailsComponent } from './details.component';
import { Book } from '../../domain/models/book.model';
import { provideRouter } from '@angular/router';

const meta: Meta = {
  title: 'Features/Books/Details',
  tags: ['autodocs'],
  component: DetailsComponent,
  decorators: [
    applicationConfig({
      providers: [provideRouter([])],
    }),
  ],
};
export default meta;

const props: Book = {
  id: 1,
  title: 'Lorem Ipsum',
  authorName: 'Lorem Ipsum',
  isbn: '1234567890',
  availableCopies: 10,
  coverUrl: 'https://m.media-amazon.com/images/I/81cO02Zz6VL.jpg',
  description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.',
};

export const Default = {
  args: {
    book: props,
  },
};

export const NoCover = {
  args: {
    book: { ...props, coverUrl: null },
  },
};

export const NoDescription = {
  args: {
    book: { ...props, coverUrl: null, description: null },
  },
};

export const Unavailable = {
  args: {
    book: { ...props, availableCopies: 0 },
  },
};
