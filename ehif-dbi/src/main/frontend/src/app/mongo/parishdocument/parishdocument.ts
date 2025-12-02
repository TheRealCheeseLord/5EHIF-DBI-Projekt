import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { ParishDocumentService } from '../../api/services/parish-document.service';

import { ParishDocumentDto } from '../../api/models/parish-document-dto';

@Component({
  selector: 'app-parish-document',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],

  templateUrl: './parishdocument.html',
  styleUrls: ['./parishdocument.scss'],
})
export class ParishDocumentComponent implements OnInit {
  private service = inject(ParishDocumentService);
  private fb = inject(FormBuilder);

  // --- Signals for State ---
  documents = signal<ParishDocumentDto[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  // --- Modal State ---
  // Main Modal (Document)
  showDocModal = signal<boolean>(false);
  editDocMode = signal<boolean>(false);
  selectedDocId = signal<string | null>(null);

  // Embedded Modal (Priest/Parishioner)
  showEmbeddedModal = signal<boolean>(false);
  embeddedType = signal<'priest' | 'parishioner' | null>(null);
  embeddedEditMode = signal<boolean>(false);
  selectedEmbeddedId = signal<string | null>(null); // The ID within the list

  // --- Forms ---
  // Main Document Form
  docForm: FormGroup = this.fb.group({
    id: [null], // Mongo ID is usually string, often auto-generated but DTO has it
    name: ['', Validators.required],
    location: ['', Validators.required],
    foundedYear: [null, Validators.required],
    priests: [[]],
    parishioners: [[]],
  });

  // Embedded Item Form (Reused for both Priests and Parishioners)
  embeddedForm: FormGroup = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    dateField: ['', Validators.required], // Maps to ordinationDate or birthDate
  });

  // --- Computed Helpers ---
  // Get the current document object from the list based on selected ID
  currentDoc = computed(() => this.documents().find((d) => d.id === this.selectedDocId()) || null);

  ngOnInit() {
    this.loadDocuments();
  }

  // ==========================================
  // MAIN DOCUMENT CRUD
  // ==========================================

  loadDocuments() {
    this.loading.set(true);
    this.service.getAllParishDocuments().subscribe({
      next: (data) => {
        this.documents.set(data || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load documents');
        this.loading.set(false);
      },
    });
  }

  openCreateDoc() {
    this.editDocMode.set(false);
    this.selectedDocId.set(null);
    this.docForm.reset();
    this.showDocModal.set(true);
  }

  openEditDoc(doc: ParishDocumentDto) {
    this.editDocMode.set(true);
    this.selectedDocId.set(doc.id!);
    this.docForm.patchValue({
      id: doc.id,
      name: doc.name,
      location: doc.location,
      foundedYear: doc.foundedYear,
    });
    this.showDocModal.set(true);
  }

  saveDoc() {
    if (this.docForm.invalid) return;

    let dto: ParishDocumentDto = this.docForm.value as ParishDocumentDto;

    dto = {
      ...dto,
      priests: dto.priests || [],
      parishioners: dto.parishioners || [],
    };

    if (this.editDocMode()) {
      // Update
      this.service.updateParishDocument({ body: dto }).subscribe({
        next: (res) => {
          this.updateLocalList(res);
          this.closeDocModal();
        },
        error: () => this.error.set('Error updating document'),
      });
    } else {
      // Create
      this.service.createParishDocument({ body: dto }).subscribe({
        next: (res) => {
          this.documents.update((docs) => [...docs, res]);
          this.closeDocModal();
        },
        error: () => this.error.set('Error creating document'),
      });
    }
  }

  deleteDoc(doc: ParishDocumentDto) {
    if (!confirm(`Delete ${doc.name}?`)) return;

    this.service.deleteParishDocument({ parishDocumentId: doc.id! }).subscribe({
      next: () => {
        this.documents.update((docs) => docs.filter((d) => d.id !== doc.id));
      },
    });
  }

  closeDocModal() {
    this.showDocModal.set(false);
    this.error.set(null);
  }

  private updateLocalList(updated: ParishDocumentDto) {
    this.documents.update((docs) => docs.map((d) => (d.id === updated.id ? updated : d)));
  }

  // ==========================================
  // EMBEDDED ITEMS CRUD (Priests / Parishioners)
  // ==========================================

  // 1. OPEN MODAL FOR CREATION
  openAddEmbedded(type: 'priest' | 'parishioner') {
    this.embeddedType.set(type);
    this.embeddedEditMode.set(false);
    this.selectedEmbeddedId.set(null);
    this.embeddedForm.reset();
    this.showEmbeddedModal.set(true);
  }

  // 2. OPEN MODAL FOR EDITING
  openEditEmbedded(type: 'priest' | 'parishioner', item: any) {
    this.embeddedType.set(type);
    this.embeddedEditMode.set(true);
    this.selectedEmbeddedId.set(item.id);

    // Map specific date fields to generic form control
    const dateVal = type === 'priest' ? item.ordinationDate : item.birthDate;

    this.embeddedForm.patchValue({
      firstName: item.firstName,
      lastName: item.lastName,
      dateField: dateVal,
    });
    this.showEmbeddedModal.set(true);
  }

  // 3. SAVE EMBEDDED (Create or Update)
  saveEmbedded() {
    if (this.embeddedForm.invalid) return;

    const docId = this.selectedDocId();
    if (!docId) return;

    const val = this.embeddedForm.value;
    const type = this.embeddedType();

    // Construct payloads based on type
    if (type === 'priest') {
      const payload = {
        firstName: val.firstName,
        lastName: val.lastName,
        ordinationDate: val.dateField,
      };

      if (this.embeddedEditMode()) {
        // UPDATE PRIEST
        this.service
          .updatePriest1({
            parishDocumentId: docId,
            priestEmbeddedId: this.selectedEmbeddedId()!,
            body: payload,
          })
          .subscribe((res) => this.handleEmbeddedSuccess(res, 'update', 'priests'));
      } else {
        // CREATE PRIEST
        this.service
          .addPriest({
            parishDocumentId: docId,
            body: payload,
          })
          .subscribe((res) => this.handleEmbeddedSuccess(res, 'add', 'priests'));
      }
    } else {
      const payload = {
        firstName: val.firstName,
        lastName: val.lastName,
        birthDate: val.dateField,
      };

      if (this.embeddedEditMode()) {
        // UPDATE PARISHIONER
        this.service
          .updateParishioner1({
            parishDocumentId: docId,
            parishionerEmbeddedId: this.selectedEmbeddedId()!,
            body: payload,
          })
          .subscribe((res) => this.handleEmbeddedSuccess(res, 'update', 'parishioners'));
      } else {
        // CREATE PARISHIONER
        this.service
          .addParishioner({
            parishDocumentId: docId,
            body: payload,
          })
          .subscribe((res) => this.handleEmbeddedSuccess(res, 'add', 'parishioners'));
      }
    }
  }

  // 4. DELETE EMBEDDED
  removeEmbedded(type: 'priest' | 'parishioner', id: string) {
    if (!confirm('Remove this item?')) return;
    const docId = this.selectedDocId();
    if (!docId) return;

    if (type === 'priest') {
      this.service.removePriest({ parishDocumentId: docId, priestEmbeddedId: id }).subscribe({
        next: () => this.handleEmbeddedDelete('priests', id),
      });
    } else {
      this.service
        .removeParishioner({ parishDocumentId: docId, parishionerEmbeddedId: id })
        .subscribe({
          next: () => this.handleEmbeddedDelete('parishioners', id),
        });
    }
  }

  closeEmbeddedModal() {
    this.showEmbeddedModal.set(false);
  }

  // Helper to update local state without full reload
  private handleEmbeddedSuccess(
    resultItem: any,
    action: 'add' | 'update',
    listKey: 'priests' | 'parishioners'
  ) {
    const docId = this.selectedDocId();
    this.documents.update((docs) => {
      return docs.map((doc) => {
        if (doc.id !== docId) return doc;

        let list = (doc as any)[listKey] || [];
        if (action === 'add') {
          list = [...list, resultItem];
        } else {
          list = list.map((item: any) => (item.id === resultItem.id ? resultItem : item));
        }
        return { ...doc, [listKey]: list };
      });
    });
    this.closeEmbeddedModal();
  }

  private handleEmbeddedDelete(listKey: 'priests' | 'parishioners', itemId: string) {
    const docId = this.selectedDocId();
    this.documents.update((docs) => {
      return docs.map((doc) => {
        if (doc.id !== docId) return doc;
        return {
          ...doc,
          [listKey]: (doc as any)[listKey].filter((i: any) => i.id !== itemId),
        };
      });
    });
  }
}
