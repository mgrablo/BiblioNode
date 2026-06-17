import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Book } from '../domain/models/book.model';
import { BookService } from '../data-access/book.service';
import { BookResponse } from '../data-access/dto/book-response';
import { BookMapper } from '../domain/mappers/book.mapper';
import { DetailsComponent } from './components/details.component';
import { BacklinkComponent } from '../../../shared/components/backlink/backlink.component';

@Component({
  selector: 'app-details-page',
  imports: [DetailsComponent, BacklinkComponent],
  templateUrl: './details-page.component.html',
  styleUrl: './details-page.component.scss',
})
export class DetailsPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private bookService = inject(BookService);

  book = signal<Book | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const idStr = params.get('id');
      if (idStr) {
        this.loadBookDetails(Number(idStr));
      }
    });
  }

  private loadBookDetails(id: number): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.bookService.getBookById(id).subscribe({
      next: (response: BookResponse) => {
        this.book.set(BookMapper.toBook(response));
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load book', err);
        this.errorMessage.set('Book not found.');
        this.isLoading.set(false);
      },
    });
  }
}
