import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ParishDocumentService } from '../../api/services/parish-document.service';

import { ParishDocumentDto } from '../../api/models/parish-document-dto';
import { PriestEmbeddedDto } from '../../api/models/priest-embedded-dto';
import { ParishionerEmbeddedDto } from '../../api/models/parishioner-embedded-dto';

import { AddPriestEmbeddedCommand } from '../../api/models/add-priest-embedded-command';
import { AddParishionerEmbeddedCommand } from '../../api/models/add-parishioner-embedded-command';

@Component({
  selector: 'app-parish-document',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './parishdocument.html',
  styleUrls: ['./parishdocument.scss'],
})
export class ParishDocumentComponent implements OnInit {
  documents: ParishDocumentDto[] = [];

  loading = false;
  relationsLoading = false;
  error: string | null = null;

  showForm = false;
  editMode = false;

  form = {
    id: null as string | null,
    name: '',
    location: '',
    foundedYear: null as number | null,
  };

  priests: PriestEmbeddedDto[] = [];
  parishioners: ParishionerEmbeddedDto[] = [];

  newPriestFirst = '';
  newPriestLast = '';
  newPriestOrdination = '';

  newParishionerFirst = '';
  newParishionerLast = '';
  newParishionerBirth = '';

  constructor(private api: ParishDocumentService) {}

  ngOnInit(): void {
    this.loadDocuments();
  }

  // =====================================================================
  // LIST
  // =====================================================================

  loadDocuments() {
    this.loading = true;
    this.api.getAllParishDocuments().subscribe({
      next: (res) => {
        this.documents = res ?? [];
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Could not load parish documents.';
      },
    });
  }

  // =====================================================================
  // OPEN / CLOSE MODAL
  // =====================================================================

  openCreate() {
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

  openEdit(doc: ParishDocumentDto) {
    this.editMode = true;
    this.showForm = true;

    this.form = {
      id: doc.id ?? null,
      name: doc.name ?? '',
      location: doc.location ?? '',
      foundedYear: doc.foundedYear ?? null,
    };

    this.loadRelations(doc.id!);
  }

  close() {
    this.showForm = false;
  }

  // =====================================================================
  // CREATE / UPDATE
  // =====================================================================

  save() {
    if (this.editMode) this.update();
    else this.create();
  }

  create() {
    const body: ParishDocumentDto = {
      name: this.form.name,
      location: this.form.location,
      foundedYear: this.form.foundedYear ?? 0,
    };

    this.api.createParishDocument({ body }).subscribe({
      next: () => {
        this.showForm = false;
        this.loadDocuments();
      },
      error: (err) => (this.error = 'Could not create document.'),
    });
  }

  update() {
    const body: ParishDocumentDto = {
      id: this.form.id!,
      name: this.form.name,
      location: this.form.location,
      foundedYear: this.form.foundedYear ?? 0,
    };

    this.api.updateParishDocument({ body }).subscribe({
      next: () => {
        this.showForm = false;
        this.loadDocuments();
      },
      error: (err) => (this.error = 'Could not update document.'),
    });
  }

  // =====================================================================
  // DELETE
  // =====================================================================

  delete(doc: ParishDocumentDto) {
    if (!doc.id) return;

    if (!confirm(`Delete "${doc.name}"?`)) return;

    this.api.deleteParishDocument({ parishDocumentId: doc.id }).subscribe({
      next: () => this.loadDocuments(),
      error: (err) => (this.error = 'Could not delete document.'),
    });
  }

  // =====================================================================
  // RELATIONS LOAD
  // =====================================================================

  loadRelations(id: string) {
    this.relationsLoading = true;

    this.api.getPriests({ parishDocumentId: id }).subscribe({
      next: (res) => {
        this.priests = res ?? [];
        this.relationsLoading = false;
      },
      error: () => {
        this.priests = [];
        this.relationsLoading = false;
      },
    });

    this.api.getParishioners({ parishDocumentId: id }).subscribe({
      next: (res) => (this.parishioners = res ?? []),
      error: () => (this.parishioners = []),
    });
  }

  // =====================================================================
  // ADD PRIEST
  // =====================================================================

  addPriest() {
    const id = this.form.id!;
    const body: AddPriestEmbeddedCommand = {
      firstName: this.newPriestFirst,
      lastName: this.newPriestLast,
      ordinationDate: this.newPriestOrdination,
    };

    this.api.addPriest({ parishDocumentId: id, body }).subscribe({
      next: () => {
        this.newPriestFirst = '';
        this.newPriestLast = '';
        this.newPriestOrdination = '';
        this.loadRelations(id);
      },
      error: () => (this.error = 'Could not add priest.'),
    });
  }

  removePriest(priest: PriestEmbeddedDto) {
    const id = this.form.id!;
    this.api
      .removePriest({
        parishDocumentId: id,
        priestEmbeddedId: priest.id!,
      })
      .subscribe({
        next: () => this.loadRelations(id),
        error: () => (this.error = 'Could not remove priest.'),
      });
  }

  // =====================================================================
  // ADD PARISHIONER
  // =====================================================================

  addParishioner() {
    const id = this.form.id!;
    const body: AddParishionerEmbeddedCommand = {
      firstName: this.newParishionerFirst,
      lastName: this.newParishionerLast,
      birthDate: this.newParishionerBirth,
    };

    this.api.addParishioner({ parishDocumentId: id, body }).subscribe({
      next: () => {
        this.newParishionerFirst = '';
        this.newParishionerLast = '';
        this.newParishionerBirth = '';
        this.loadRelations(id);
      },
      error: () => (this.error = 'Could not add parishioner.'),
    });
  }

  removeParishioner(p: ParishionerEmbeddedDto) {
    const id = this.form.id!;
    this.api
      .removeParishioner({
        parishDocumentId: id,
        parishionerEmbeddedId: p.id!,
      })
      .subscribe({
        next: () => this.loadRelations(id),
        error: () => (this.error = 'Could not remove parishioner.'),
      });
  }
}
