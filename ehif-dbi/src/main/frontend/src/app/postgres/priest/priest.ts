// priest.component.ts
import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PriestService } from '../../api/services/priest.service';
import { PriestDto } from '../../api/models';

@Component({
  standalone: true,
  selector: 'app-pg-priest',
  templateUrl: './priest.html',
  styleUrls: ['./priest.scss'],
  imports: [CommonModule, ReactiveFormsModule],
})
export class PriestComponent implements OnInit {
  private api = inject(PriestService);
  private fb = inject(FormBuilder);

  // signals
  priests = signal<PriestDto[]>([]);
  showForm = signal(false);
  editMode = signal(false);
  selectedId = signal<number | null>(null);

  // reactive form
  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    ordinationDate: ['', Validators.required],
  });

  ngOnInit() {
    this.load();
  }

  // --- Load all priests ------------------------------------------------------

  load() {
    this.api.getAllPriests().subscribe((res) => {
      res = res ?? [];
      this.priests.set(
        res.map((p) => ({
          id: p.id,
          firstName: p.firstName,
          lastName: p.lastName,
          ordinationDate: p.ordinationDate,
        }))
      );
    });
  }

  // --- Form handling ---------------------------------------------------------

  openAdd() {
    this.editMode.set(false);
    this.selectedId.set(null);
    this.form.reset();
    this.showForm.set(true);
  }

  openEdit(p: PriestDto) {
    this.editMode.set(true);
    this.selectedId.set(p.id ?? null);
    this.form.patchValue({
      firstName: p.firstName ?? '',
      lastName: p.lastName ?? '',
      ordinationDate: p.ordinationDate ?? '',
    });
    this.showForm.set(true);
  }

  close() {
    this.showForm.set(false);
  }

  // --- Save ------------------------------------------------------------------

  save() {
    if (this.form.invalid) return;

    const payload = this.form.value;

    if (this.editMode() && this.selectedId() != null) {
      this.api
        .updatePriest({
          priestId: this.selectedId()!,
          body: {
            firstName: payload.firstName as string,
            lastName: payload.lastName as string,
            ordinationDate: payload.ordinationDate as string,
          },
        })
        .subscribe(() => {
          this.load();
          this.close();
        });
    } else {
      this.api
        .createPriest({
          body: {
            firstName: payload.firstName as string,
            lastName: payload.lastName as string,
            ordinationDate: payload.ordinationDate as string,
          },
        })
        .subscribe(() => {
          this.load();
          this.close();
        });
    }
  }

  // --- Delete ----------------------------------------------------------------

  delete(id: number) {
    this.api.deletePriest({ priestId: id }).subscribe(() => {
      this.load();
    });
  }
}
