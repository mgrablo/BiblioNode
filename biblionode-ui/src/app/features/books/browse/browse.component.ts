import { Component } from '@angular/core';
import { Book } from '../domain/models/book.model';
import { BookCardComponent } from '../../../shared/components/book-card/book-card.component';
import { BookGridComponent } from './book-grid/book-grid.component';

@Component({
  selector: 'app-browse',
  imports: [BookGridComponent],
  templateUrl: './browse.component.html',
  styleUrl: './browse.component.scss',
})
export class BrowseComponent {
  books: Array<Book> = [
    {
      id: 1,
      title: 'The Way Of Kings',
      authorName: 'Brandon Sanderson',
      isbn: '1234567890',
      availableCopies: 10,
    },
    {
      id: 2,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 0,
    },
    {
      id: 3,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 1,
    },
    {
      id: 4,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 10,
    },
    {
      id: 5,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 10,
    },
    {
      id: 6,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 10,
    },
    {
      id: 7,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 10,
    },
    {
      id: 8,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 10,
    },
    {
      id: 9,
      title: 'Lorem Ipsum',
      authorName: 'Lorem Ipsum',
      isbn: '1234567890',
      availableCopies: 10,
    },
  ];
}
