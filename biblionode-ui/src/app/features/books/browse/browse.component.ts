import { Component, inject, signal } from '@angular/core';
import { Book } from '../domain/models/book.model';
import { BookGridComponent } from './book-grid/book-grid.component';
import { BookService } from '../data-access/book.service';
import { BookMapper } from '../domain/mappers/book.mapper';

@Component({
  selector: 'app-browse',
  imports: [BookGridComponent],
  templateUrl: './browse.component.html',
  styleUrl: './browse.component.scss',
})
export class BrowseComponent {
  private bookService = inject(BookService);

  books = signal<Book[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  ngOnInit() {
    this.loadAllBooks();
  }

  loadAllBooks() {
    this.isLoading.set(true);
    this.bookService.getAllBooks().subscribe({
      next: (page) => {
        this.books.set(BookMapper.toBookList(page.content));
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error fetching books:', err);
        this.errorMessage.set('Failed to load books. Please try again later.');
        this.isLoading.set(false);
      },
    });
  }
}
