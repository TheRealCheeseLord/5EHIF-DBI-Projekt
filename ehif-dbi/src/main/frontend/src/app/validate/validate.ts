import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BenchmarkService } from '../api/services';
import { ParishDocumentDto, Error } from '../api/models';

@Component({
  selector: 'dbi-validate',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './validate.html',
  styleUrl: './validate.scss',
})
export class Validate {
  private fb = inject(FormBuilder);
  private benchmarkApi = inject(BenchmarkService);

  // form
  form = this.fb.group({
    id: [''],
    name: [''],
    location: [''],
    foundedYear: [''],
    priests: [''], // JSON input string
    parishioners: [''], // JSON input string
  });

  // signals
  loading = signal(false);
  errors = signal<Error[]>([]);
  lastValidated = signal<ParishDocumentDto | null>(null);

  // --- Methods --------------------------------------------------------------

  validate() {
    this.loading.set(true);
    this.errors.set([]);
    this.lastValidated.set(null);

    // Parse priests and parishioners JSON
    let priests: any[] = [];
    let parishioners: any[] = [];
    try {
      priests = JSON.parse(this.form.value.priests || '[]');
    } catch {
      this.errors.set([{ message: 'Invalid JSON in priests field', keyword: 'json' } as Error]);
      this.loading.set(false);
      return;
    }
    try {
      parishioners = JSON.parse(this.form.value.parishioners || '[]');
    } catch {
      this.errors.set([
        { message: 'Invalid JSON in parishioners field', keyword: 'json' } as Error,
      ]);
      this.loading.set(false);
      return;
    }

    const dto: ParishDocumentDto = {
      id: this.form.value.id || undefined,
      name: this.form.value.name!,
      location: this.form.value.location ?? undefined,
      foundedYear: this.form.value.foundedYear ? +this.form.value.foundedYear : undefined,
      priests,
      parishioners,
    };

    this.benchmarkApi.validateParishDocument({ body: dto }).subscribe({
      next: (res) => {
        this.errors.set(res || []);
        if ((res || []).length === 0) {
          this.lastValidated.set(dto);
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.errors.set([{ message: 'Server error: ' + err.message, keyword: 'server' } as Error]);
        this.loading.set(false);
      },
    });
  }
}
