import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BookResponse } from './dto/book-response';
import { Page } from '../../../shared/models/page.model';

@Injectable({ providedIn: 'root' })
export class BookService {
  private http = inject(HttpClient);

  getAllBooks() {
    return this.http.get<Page<BookResponse>>('api/books');
  }

  getBookById(id: number) {
    return this.http.get<BookResponse>(`api/books/${id}`);
  }
}
