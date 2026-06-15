import { Component, input } from '@angular/core';
import { Book } from '../../domain/models/book.model';
import { BookCardComponent } from '../../../../shared/components/book-card/book-card.component';

@Component({
  selector: 'app-book-grid',
  standalone: true,
  imports: [BookCardComponent],
  templateUrl: './book-grid.component.html',
  styleUrl: './book-grid.component.scss',
})
export class BookGridComponent {
  books = input.required<Iterable<Book>>();
}
