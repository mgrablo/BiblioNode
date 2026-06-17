export interface Book {
  id: number;
  title: string;
  authorName: string;
  isbn: string;
  availableCopies: number;
  coverUrl: string | null;
  description: string | null;
}
