import { Component, inject, input } from '@angular/core';
import { Book } from '../../domain/models/book.model';
import { BookCardComponent } from '../../components/book-card/book-card.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-grid',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './book-grid.component.html',
  styleUrl: './book-grid.component.scss',
})
export class BookGridComponent {
  private router = inject(Router);
  books = input.required<Book[]>();

  onBookClick(book: Book) {
    this.router.navigate(['/books', book.id]);
  }
}
