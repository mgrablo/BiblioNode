import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookCoverComponent } from './book-cover.component';

describe('BookCoverComponent', () => {
  let component: BookCoverComponent;
  let fixture: ComponentFixture<BookCoverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookCoverComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BookCoverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
