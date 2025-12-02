import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { ParishService } from '../../api/services/parish.service';
import { ParishDto } from '../../api/models/parish-dto';
import { PriestSummaryDto } from '../../api/models/priest-summary-dto';
import { ParishionerSummaryDto } from '../../api/models/parishioner-summary-dto';

@Component({
  selector: 'app-parish',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './parish.html',
  styleUrls: ['./parish.scss'],
})
export class ParishComponent implements OnInit {
  // ------------------- SIGNAL STATE -------------------
  parishes = signal<ParishDto[]>([]);
  priests = signal<PriestSummaryDto[]>([]);
  parishioners = signal<ParishionerSummaryDto[]>([]);

  loading = signal(false);
  relationsLoading = signal(false);
  error = signal<string | null>(null);

  showForm = signal(false);
  editMode = signal(false);

  newPriestId = signal<number | null>(null);
  newParishionerId = signal<number | null>(null);

  // ------------------- FORM -------------------
  form = new FormGroup({
    id: new FormControl<number | null>(null),
    name: new FormControl('', Validators.required),
    location: new FormControl(''),
    foundedYear: new FormControl<number | null>(null),
  });

  constructor(private parishService: ParishService) {}

  ngOnInit(): void {
    this.loadParishes();
  }

  // ---------------- LOAD PARISHES ----------------
  loadParishes(): void {
    this.loading.set(true);

    this.parishService.getAllParishes().subscribe({
      next: (data) => {
        this.parishes.set(data ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load parishes.');
        this.loading.set(false);
      },
    });
  }

  // ---------------- OPEN / CLOSE MODAL ----------------
  openCreate(): void {
    this.editMode.set(false);
    this.showForm.set(true);

    this.form.reset({
      id: null,
      name: '',
      location: '',
      foundedYear: null,
    });

    this.priests.set([]);
    this.parishioners.set([]);
  }

  openEdit(p: ParishDto): void {
    this.editMode.set(true);
    this.showForm.set(true);

    this.form.patchValue({
      id: p.id ?? null,
      name: p.name ?? '',
      location: p.location ?? '',
      foundedYear: p.foundedYear ?? null,
    });

    this.loadRelations(p.id!);
  }

  close(): void {
    this.showForm.set(false);
  }

  // ---------------- CREATE / UPDATE ----------------
  save(): void {
    if (this.editMode() && this.form.value.id != null) {
      this.update();
    } else {
      this.create();
    }
  }

  private create(): void {
    this.loading.set(true);

    this.parishService
      .createParish({
        body: {
          name: this.form.value.name!,
          location: this.form.value.location!,
          foundedYear: this.form.value.foundedYear!,
        },
      })
      .subscribe({
        next: () => {
          this.showForm.set(false);
          this.loadParishes();
        },
        error: () => {
          this.error.set('Could not create parish.');
          this.loading.set(false);
        },
      });
  }

  private update(): void {
    this.loading.set(true);

    this.parishService
      .updateParish({
        parishId: this.form.value.id!,
        body: {
          name: this.form.value.name!,
          location: this.form.value.location!,
          foundedYear: this.form.value.foundedYear!,
        },
      })
      .subscribe({
        next: () => {
          this.showForm.set(false);
          this.loadParishes();
        },
        error: () => {
          this.error.set('Could not update parish.');
          this.loading.set(false);
        },
      });
  }

  // ---------------- DELETE ----------------
  delete(p: ParishDto): void {
    if (!p.id) return;
    if (!confirm(`Delete parish "${p.name}"?`)) return;

    this.loading.set(true);

    this.parishService.deleteParish({ parishId: p.id }).subscribe({
      next: () => this.loadParishes(),
      error: () => {
        this.error.set('Could not delete parish.');
        this.loading.set(false);
      },
    });
  }

  // ---------------- LOAD PRIESTS + PARISHIONERS ----------------
  private loadRelations(parishId: number): void {
    this.relationsLoading.set(true);

    this.parishService.getPriests1({ parishId }).subscribe({
      next: (priests) => {
        this.priests.set(priests ?? []);
        this.relationsLoading.set(false);
      },
      error: () => {
        this.error.set('Could not load priests.');
        this.relationsLoading.set(false);
      },
    });

    this.parishService.getParishioners1({ parishId }).subscribe({
      next: (parishioners) => {
        this.parishioners.set(parishioners ?? []);
      },
      error: () => {
        this.error.set('Could not load parishioners.');
      },
    });
  }

  // ---------------- ADD RELATIONS ----------------
  addPriest(): void {
    const parishId = this.form.value.id;
    const priestId = this.newPriestId();

    if (!parishId || priestId == null) return;

    this.parishService
      .addPriest1({
        parishId,
        body: { priestId },
      })
      .subscribe({
        next: () => {
          this.newPriestId.set(null);
          this.loadRelations(parishId);
        },
        error: () => this.error.set('Could not add priest.'),
      });
  }

  addParishioner(): void {
    const parishId = this.form.value.id;
    const pid = this.newParishionerId();

    if (!parishId || pid == null) return;

    this.parishService
      .addParishioner1({
        parishId,
        body: { parishionerId: pid },
      })
      .subscribe({
        next: () => {
          this.newParishionerId.set(null);
          this.loadRelations(parishId);
        },
        error: () => this.error.set('Could not add parishioner.'),
      });
  }

  // ---------------- REMOVE RELATIONS ----------------
  removePriest(pr: PriestSummaryDto): void {
    if (!this.form.value.id || !pr.id) return;

    this.parishService
      .removePriest1({
        parishId: this.form.value.id!,
        priestId: pr.id,
      })
      .subscribe({
        next: () => this.loadRelations(this.form.value.id!),
        error: () => this.error.set('Could not remove priest.'),
      });
  }

  removeParishioner(pa: ParishionerSummaryDto): void {
    if (!this.form.value.id || !pa.id) return;

    this.parishService
      .removeParishioner1({
        parishId: this.form.value.id!,
        parishionerId: pa.id,
      })
      .subscribe({
        next: () => this.loadRelations(this.form.value.id!),
        error: () => this.error.set('Could not remove parishioner.'),
      });
  }
}
