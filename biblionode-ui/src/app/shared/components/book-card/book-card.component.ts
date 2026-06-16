import { Component, input } from '@angular/core';
import { Book } from '../../../features/books/domain/models/book.model';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss',
})
export class BookCardComponent {
  book = input.required<Book>();

  private readonly spinePalette = [
    '#3F5D4E',
    '#B08D57',
    '#7C4A3C',
    '#4A6A8A',
    '#8A6A9E',
    '#5E7355',
  ];

  get coverColor(): string {
    return this.spinePalette[this.book().id % this.spinePalette.length];
  }

  get statusColorClass(): string {
    return this.book().availableCopies === 0 ? 'text-[#B0503A]' : 'text-[#3F5D4E]';
  }

  get statusText(): string {
    if (this.book().availableCopies === 0) {
      return 'Checked out';
    }
    const txt = this.book().availableCopies === 1 ? 'copy' : 'copies';
    return `${this.book().availableCopies} ${txt} left`;
  }
}
