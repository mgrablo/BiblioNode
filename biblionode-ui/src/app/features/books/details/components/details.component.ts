import { Component, computed, inject, input } from '@angular/core';
import { Book } from '../../domain/models/book.model';
import { BookCoverComponent } from '../../components/book-cover/book-cover.component';
import { StatusBadgeComponent } from '../../components/status-badge/status-badge.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { BookService } from '../../data-access/book.service';
import { BacklinkComponent } from '../../../../shared/components/backlink/backlink.component';

@Component({
  selector: 'app-details',
  imports: [BookCoverComponent, StatusBadgeComponent, ButtonComponent, BacklinkComponent],
  templateUrl: './details.component.html',
  styleUrl: './details.component.scss',
})
export class DetailsComponent {
  private bookService = inject(BookService);

  book = input.required<Book>();
  isAvailable = computed(() => this.book().availableCopies > 0);

  onBorrow(): void {
    // TODO
  }
}
