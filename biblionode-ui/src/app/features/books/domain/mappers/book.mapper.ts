import { BookResponse } from '../../data-access/dto/book-response';
import { Book } from '../models/book.model';

export class BookMapper {
  static toBook(response: BookResponse): Book {
    return {
      id: response.id,
      title: response.title,
      isbn: response.isbn,
      authorName: response.authorName,
      availableCopies: response.available ? 1 : 0,
    };
  }

  static toBookList(responses: BookResponse[]): Book[] {
    return responses.map(BookMapper.toBook);
  }
}
