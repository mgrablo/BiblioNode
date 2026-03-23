import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IconInputComponent } from './icon-input.component';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {Component} from '@angular/core';
import {By} from '@angular/platform-browser';

@Component({
  standalone: true,
  imports: [IconInputComponent, ReactiveFormsModule],
  template: `
    <app-icon-input [control]="control">
      <span start id="test-start">StartIcon</span>
      <span end id="test-end">EndIcon</span>
    </app-icon-input>
  `
})
class TestHostComponent {
  control = new FormControl('');
}

describe('IconInputComponent', () => {
  describe("Core Functionality", () => {
    let component: IconInputComponent;
    let fixture: ComponentFixture<IconInputComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [IconInputComponent, ReactiveFormsModule],
      }).compileComponents();

      fixture = TestBed.createComponent(IconInputComponent);
      component = fixture.componentInstance;

      fixture.componentRef.setInput('control', new FormControl(''));
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should change type based on isPassword', () => {
      fixture.componentRef.setInput('isPassword', true);
      fixture.detectChanges();
      const inputElement: HTMLInputElement = fixture.nativeElement.querySelector('input');

      expect(component.type()).toBe('password');
      expect(inputElement.type).toBe('password');

      fixture.componentRef.setInput('isPassword', false);
      fixture.detectChanges();
      expect(component.type()).toBe('text');
      expect(inputElement.type).toBe('text');
    })

    it('should display placeholder', () => {
      const placeholderText = 'Enter your text';
      fixture.componentRef.setInput('placeholder', placeholderText);
      fixture.detectChanges();
      const inputElement: HTMLInputElement = fixture.nativeElement.querySelector('input');
      expect(inputElement.placeholder).toBe(placeholderText);
    })

    it('should update the input when the FormControl value changes', () => {
      const control = new FormControl('initial');
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();
      const inputElement: HTMLInputElement = fixture.nativeElement.querySelector('input');
      expect(inputElement.value).toBe('initial');

      control.setValue('updated');
      fixture.detectChanges();
      expect(inputElement.value).toBe('updated');
    });

    it('should update the FormControl value when the input changes', () => {
      const control = new FormControl('');
      fixture.componentRef.setInput('control', control);
      fixture.detectChanges();
      const inputElement: HTMLInputElement = fixture.nativeElement.querySelector('input');

      inputElement.value = 'user input';
      inputElement.dispatchEvent(new Event('input'));
      fixture.detectChanges();
      expect(control.value).toBe('user input');
    });
  });

  describe('Content Projection', () => {
    let hostFixture: ComponentFixture<TestHostComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [TestHostComponent, IconInputComponent, ReactiveFormsModule],
      }).compileComponents();

      hostFixture = TestBed.createComponent(TestHostComponent);
      hostFixture.detectChanges();
    });

    it('should project start icon content', () => {
      const startIcon = hostFixture.debugElement.query(By.css('#test-start'));

      expect(startIcon).toBeTruthy();
      expect(startIcon.nativeElement.textContent).toBe('StartIcon');
    });

    it('should project end icon content', () => {
      const endIcon = hostFixture.debugElement.query(By.css('#test-end'));

      expect(endIcon).toBeTruthy();
      expect(endIcon.nativeElement.textContent).toBe('EndIcon');
    });
  });
});

