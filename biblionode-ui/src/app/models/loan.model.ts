export interface Loan {
  id: number;
  bookId: number;
  bookTitle: string;
  bookAuthorName: string;
  bookIsbn: string;
  readerId: number;
  loanDate: string;
  dueDate: string;
  returnDate: string | null;
}
