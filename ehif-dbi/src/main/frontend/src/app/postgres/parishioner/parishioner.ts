import { Component, inject, OnInit, signal } from '@angular/core';
import { ParishionerService } from '../../api/services/parishioner.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ParishionerDto } from '../../api/models';

@Component({
  standalone: true,
  selector: 'app-pg-parishioner',
  templateUrl: './parishioner.html',
  styleUrls: ['./parishioner.scss'],
  imports: [CommonModule, ReactiveFormsModule],
})
export class ParishionerComponent implements OnInit {
  private api = inject(ParishionerService);
  private fb = inject(FormBuilder);

  // signals
  parishioners = signal<ParishionerDto[]>([]);
  showForm = signal(false);
  editMode = signal(false);
  selectedId = signal<number | null>(null);

  // reactive form
  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    birthDate: ['', Validators.required],
  });

  ngOnInit() {
    this.load();
  }

  // --- Load all parishioners -------------------------------------------------

  load() {
    this.api.getAllParishioners().subscribe((res) => {
      res = res ?? [];
      this.parishioners.set(
        res.map((p) => ({
          id: p.id,
          firstName: p.firstName,
          lastName: p.lastName,
          birthDate: p.birthDate,
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

  openEdit(p: ParishionerDto) {
    this.editMode.set(true);
    this.selectedId.set(p.id ?? null);
    this.form.patchValue(p);
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
        .updateParishioner({
          parishionerId: this.selectedId()!,
          body: {
            firstName: payload.firstName as string,
            lastName: payload.lastName as string,
            birthDate: payload.birthDate as string,
          },
        })
        .subscribe(() => {
          this.load();
          this.close();
        });
    } else {
      this.api
        .createParishioner({
          body: {
            firstName: payload.firstName as string,
            lastName: payload.lastName as string,
            birthDate: payload.birthDate as string,
          },
        })
        .subscribe(() => {
          this.load();
          this.close();
        });
    }
  }

  // --- Delete ---------------------------------------------------------------

  delete(id: number) {
    this.api.deleteParishioner({ parishionerId: id }).subscribe(() => {
      this.load();
    });
  }
}
