import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

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
  private parishService = inject(ParishService);
  private fb = inject(FormBuilder);

  // --------- signals ----------
  parishes = signal<ParishDto[]>([]);
  priests = signal<PriestSummaryDto[]>([]);
  parishioners = signal<ParishionerSummaryDto[]>([]);

  showForm = signal(false);
  editMode = signal(false);
  selectedParishId = signal<number | null>(null);

  newPriestId = signal<number | null>(null);
  newParishionerId = signal<number | null>(null);

  // --------- form ----------
  form = this.fb.group({
    name: ['', Validators.required],
    location: [''],
    foundedYear: [null as number | null],
  });

  ngOnInit(): void {
    this.loadParishes();
  }

  // --------- load parishes ----------
  loadParishes(): void {
    this.parishService.getAllParishes().subscribe((data) => {
      this.parishes.set(data ?? []);
    });
  }

  // --------- open / close modal ----------
  openCreate(): void {
    this.editMode.set(false);
    this.selectedParishId.set(null);
    this.showForm.set(true);

    this.form.reset({
      name: '',
      location: '',
      foundedYear: null,
    });

    this.priests.set([]);
    this.parishioners.set([]);
    this.newPriestId.set(null);
    this.newParishionerId.set(null);
  }

  openEdit(p: ParishDto): void {
    if (!p.id) return;

    this.editMode.set(true);
    this.selectedParishId.set(p.id);
    this.showForm.set(true);

    this.form.patchValue({
      name: p.name ?? '',
      location: p.location ?? '',
      foundedYear: p.foundedYear ?? null,
    });

    this.loadRelations(p.id);
  }

  close(): void {
    this.showForm.set(false);
  }

  // --------- create / update ----------
  save(): void {
    if (this.form.invalid) return;

    const foundedYear = this.form.value.foundedYear ?? 0; // backend expects number, not null

    const body = {
      name: this.form.value.name as string,
      location: (this.form.value.location as string) ?? '',
      foundedYear, // number
    };

    if (this.editMode() && this.selectedParishId() != null) {
      this.parishService
        .updateParish({
          parishId: this.selectedParishId()!,
          body,
        })
        .subscribe(() => {
          this.showForm.set(false);
          this.loadParishes();
        });
    } else {
      this.parishService
        .createParish({
          body,
        })
        .subscribe(() => {
          this.showForm.set(false);
          this.loadParishes();
        });
    }
  }

  // --------- delete ----------
  delete(id: number): void {
    this.parishService.deleteParish({ parishId: id }).subscribe(() => {
      this.loadParishes();
    });
  }

  // --------- load relations ----------
  private loadRelations(parishId: number): void {
    this.parishService.getPriests1({ parishId }).subscribe((priests) => {
      this.priests.set(priests ?? []);
    });

    this.parishService.getParishioners1({ parishId }).subscribe((parishioners) => {
      this.parishioners.set(parishioners ?? []);
    });
  }

  // --------- add relations ----------
  addPriest(): void {
    const parishId = this.selectedParishId();
    const priestId = this.newPriestId();

    if (!parishId || priestId == null) return;

    this.parishService
      .addPriest1({
        parishId,
        body: { priestId },
      })
      .subscribe(() => {
        this.newPriestId.set(null);
        this.loadRelations(parishId);
      });
  }

  addParishioner(): void {
    const parishId = this.selectedParishId();
    const pid = this.newParishionerId();

    if (!parishId || pid == null) return;

    this.parishService
      .addParishioner1({
        parishId,
        body: { parishionerId: pid },
      })
      .subscribe(() => {
        this.newParishionerId.set(null);
        this.loadRelations(parishId);
      });
  }

  // --------- remove relations ----------
  removePriest(pr: PriestSummaryDto): void {
    const parishId = this.selectedParishId();
    if (!parishId || !pr.id) return;

    this.parishService
      .removePriest1({
        parishId,
        priestId: pr.id,
      })
      .subscribe(() => this.loadRelations(parishId));
  }

  removeParishioner(pa: ParishionerSummaryDto): void {
    const parishId = this.selectedParishId();
    if (!parishId || !pa.id) return;

    this.parishService
      .removeParishioner1({
        parishId,
        parishionerId: pa.id,
      })
      .subscribe(() => this.loadRelations(parishId));
  }
}
