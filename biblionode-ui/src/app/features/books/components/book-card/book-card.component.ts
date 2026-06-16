import { Component, input } from '@angular/core';
import { Book } from '../../domain/models/book.model';
import { NgOptimizedImage } from '@angular/common';
import { BookCoverComponent } from '../book-cover/book-cover.component';
import { StatusBadgeComponent } from '../status-badge/status-badge.component';

@Component({
  selector: 'app-book-card',
  standalone: true,
  imports: [BookCoverComponent, StatusBadgeComponent],
  templateUrl: './book-card.component.html',
  styleUrl: './book-card.component.scss',
})
export class BookCardComponent {
  book = input.required<Book>();

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
