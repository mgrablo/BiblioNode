export interface BookResponse {
  id: number;
  title: string;
  isbn: string;
  authorName: string;
  authorId: number;
  available: boolean;
  coverUrl: string | null;
  description: string | null;
  createdAt: string;
  modifiedAt: string;
}
