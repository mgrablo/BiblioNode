import { BookResponse } from '../../data-access/dto/book-response';
import { Book } from '../models/book.model';

export class BookMapper {
  static toBook(response: BookResponse): Book {
    console.log('Mapping BookResponse to Book:', response);
    return {
      id: response.id,
      title: response.title,
      isbn: response.isbn,
      authorName: response.authorName,
      availableCopies: response.available ? 1 : 0,
      coverUrl: response.coverUrl,
      description: response.description,
    };
  }

  static toBookList(responses: BookResponse[]): Book[] {
    return responses.map(BookMapper.toBook);
  }
}
