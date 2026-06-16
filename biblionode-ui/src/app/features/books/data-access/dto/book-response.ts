export interface BookResponse {
  id: number;
  title: string;
  isbn: string;
  authorName: string;
  authorId: number;
  available: boolean;
  createdAt: string;
  modifiedAt: string;
}
