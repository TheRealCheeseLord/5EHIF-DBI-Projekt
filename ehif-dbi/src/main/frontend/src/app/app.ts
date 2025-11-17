import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'dbi-root',
  imports: [RouterOutlet, DatePipe, CurrencyPipe],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('dbi');

  d: Date = new Date();
  c: number = 420.69;
}
