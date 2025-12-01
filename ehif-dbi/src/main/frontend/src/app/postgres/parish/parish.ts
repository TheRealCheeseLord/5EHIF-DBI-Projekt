import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ParishService } from '../../api/services/parish.service';
import { ParishDto } from '../../api/models/parish-dto';
import { PriestSummaryDto } from '../../api/models/priest-summary-dto';
import { ParishionerSummaryDto } from '../../api/models/parishioner-summary-dto';

@Component({
  selector: 'app-parish',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './parish.html',
  styleUrls: ['./parish.scss'],
})
export class ParishComponent implements OnInit {
  parishes: ParishDto[] = [];

  loading = false;
  relationsLoading = false;
  error: string | null = null;

  showForm = false;
  editMode = false;

  // Form model
  form = {
    id: null as number | null,
    name: '',
    location: '',
    foundedYear: null as number | null,
  };

  // Related data
  priests: PriestSummaryDto[] = [];
  parishioners: ParishionerSummaryDto[] = [];

  newPriestId: number | null = null;
  newParishionerId: number | null = null;

  constructor(private parishService: ParishService) {}

  ngOnInit(): void {
    this.loadParishes();
  }

  // ---------------- LOAD PARISHES ----------------
  loadParishes(): void {
    this.loading = true;

    this.parishService.getAllParishes().subscribe({
      next: (data) => {
        this.parishes = data ?? [];
        this.loading = false;
      },
      error: () => {
        this.error = 'Could not load parishes.';
        this.loading = false;
      },
    });
  }

  // ---------------- OPEN / CLOSE MODAL ----------------
  openCreate(): void {
    this.editMode = false;
    this.showForm = true;

    this.form = {
      id: null,
      name: '',
      location: '',
      foundedYear: null,
    };

    this.priests = [];
    this.parishioners = [];
  }

  openEdit(p: ParishDto): void {
    this.editMode = true;
    this.showForm = true;

    this.form = {
      id: p.id ?? null,
      name: p.name ?? '',
      location: p.location ?? '',
      foundedYear: p.foundedYear ?? null,
    };

    this.loadRelations(p.id!);
  }

  close(): void {
    this.showForm = false;
  }

  // ---------------- CREATE / UPDATE ----------------
  save(): void {
    if (this.editMode && this.form.id != null) {
      this.update();
    } else {
      this.create();
    }
  }

  private create(): void {
    this.loading = true;

    this.parishService
      .createParish({
        body: {
          name: this.form.name,
          location: this.form.location,
          foundedYear: this.form.foundedYear,
        } as any,
      })
      .subscribe({
        next: () => {
          this.showForm = false;
          this.loadParishes();
        },
        error: () => {
          this.error = 'Could not create parish.';
          this.loading = false;
        },
      });
  }

  private update(): void {
    this.loading = true;

    this.parishService
      .updateParish({
        parishId: this.form.id!,
        body: {
          name: this.form.name,
          location: this.form.location,
          foundedYear: this.form.foundedYear,
        } as any,
      })
      .subscribe({
        next: () => {
          this.showForm = false;
          this.loadParishes();
        },
        error: () => {
          this.error = 'Could not update parish.';
          this.loading = false;
        },
      });
  }

  // ---------------- DELETE ----------------
  delete(p: ParishDto): void {
    if (!p.id) return;
    if (!confirm(`Delete parish "${p.name}"?`)) return;

    this.loading = true;

    this.parishService.deleteParish({ parishId: p.id }).subscribe({
      next: () => this.loadParishes(),
      error: () => {
        this.error = 'Could not delete parish.';
        this.loading = false;
      },
    });
  }

  // ---------------- LOAD PRIESTS + PARISHIONERS ----------------
  private loadRelations(parishId: number): void {
    this.relationsLoading = true;

    this.parishService.getPriests1({ parishId }).subscribe({
      next: (priests) => {
        this.priests = priests ?? [];
        this.relationsLoading = false;
      },
      error: () => {
        this.error = 'Could not load priests.';
        this.relationsLoading = false;
      },
    });

    this.parishService.getParishioners1({ parishId }).subscribe({
      next: (parishioners) => {
        this.parishioners = parishioners ?? [];
      },
      error: () => {
        this.error = 'Could not load parishioners.';
      },
    });
  }

  // ---------------- ADD priest/parishioner ----------------
  addPriest(): void {
    if (!this.form.id || this.newPriestId == null) return;

    this.parishService
      .addPriest1({
        parishId: this.form.id,
        body: { priestId: this.newPriestId },
      })
      .subscribe({
        next: () => {
          this.newPriestId = null;
          this.loadRelations(this.form.id!);
        },
        error: () => (this.error = 'Could not add priest.'),
      });
  }

  addParishioner(): void {
    if (!this.form.id || this.newParishionerId == null) return;

    this.parishService
      .addParishioner1({
        parishId: this.form.id,
        body: { parishionerId: this.newParishionerId },
      })
      .subscribe({
        next: () => {
          this.newParishionerId = null;
          this.loadRelations(this.form.id!);
        },
        error: () => (this.error = 'Could not add parishioner.'),
      });
  }

  // ---------------- REMOVE priest/parishioner ----------------
  removePriest(priest: PriestSummaryDto): void {
    if (!this.form.id || !priest.id) return;

    this.parishService
      .removePriest1({
        parishId: this.form.id,
        priestId: priest.id,
      })
      .subscribe({
        next: () => this.loadRelations(this.form.id!),
        error: () => (this.error = 'Could not remove priest.'),
      });
  }

  removeParishioner(par: ParishionerSummaryDto): void {
    if (!this.form.id || !par.id) return;

    this.parishService
      .removeParishioner1({
        parishId: this.form.id,
        parishionerId: par.id,
      })
      .subscribe({
        next: () => this.loadRelations(this.form.id!),
        error: () => (this.error = 'Could not remove parishioner.'),
      });
  }
}
