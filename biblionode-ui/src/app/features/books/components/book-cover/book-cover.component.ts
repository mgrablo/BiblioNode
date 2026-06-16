import { Component, input } from '@angular/core';
import { Book } from '../../domain/models/book.model';

@Component({
  selector: 'app-book-cover',
  imports: [],
  host: {
    class: 'block w-full h-full overflow-hidden',
  },
  templateUrl: './book-cover.component.html',
  styleUrl: './book-cover.component.scss',
})
export class BookCoverComponent {
  book = input.required<Book>();
  size = input<'sm' | 'lg'>('sm');

  private readonly coverPalette = [
    '#3F5D4E',
    '#B08D57',
    '#7C4A3C',
    '#4A6A8A',
    '#8A6A9E',
    '#5E7355',
  ];

  get coverColor(): string {
    return this.coverPalette[this.book().id % this.coverPalette.length];
  }
}
