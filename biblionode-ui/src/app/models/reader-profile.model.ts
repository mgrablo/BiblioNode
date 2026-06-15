import { Loan } from './loan.model';

export interface ReaderProfile {
  id: number;
  fullName: string;
  email: string;
  loans: Loan[];
}
