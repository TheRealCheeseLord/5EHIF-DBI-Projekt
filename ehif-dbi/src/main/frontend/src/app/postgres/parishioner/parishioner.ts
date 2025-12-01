import { Component, OnInit } from '@angular/core';
import { ParishionerService } from '../../api/services/parishioner.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface Parishioner {
  id: number | null;
  firstName: string;
  lastName: string;
  birthDate: string; // format: YYYY-MM-DD
}

@Component({
  standalone: true,
  selector: 'app-pg-parishioner',
  templateUrl: './parishioner.html',
  styleUrls: ['./parishioner.scss'],
  imports: [CommonModule, FormsModule],
})
export class ParishionerComponent implements OnInit {
  parishioners: Parishioner[] = [];

  showForm = false;
  editMode = false;

  form: Parishioner = {
    id: null,
    firstName: '',
    lastName: '',
    birthDate: '',
  };

  constructor(private parishionerService: ParishionerService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.parishionerService.getAllParishioners().subscribe((res: any[]) => {
      // if backend returns birthDate already, this is basically a cast
      this.parishioners = res.map((p) => ({
        id: p.id,
        firstName: p.firstName,
        lastName: p.lastName,
        birthDate: p.birthDate, // make sure this matches backend
      }));
    });
  }

  openAdd() {
    this.editMode = false;
    this.form = {
      id: null,
      firstName: '',
      lastName: '',
      birthDate: '',
    };
    this.showForm = true;
  }

  openEdit(p: Parishioner) {
    this.editMode = true;
    // shallow clone so form is independent
    this.form = { ...p };
    this.showForm = true;
  }

  close() {
    this.showForm = false;
  }

  save() {
    // console.log('save clicked', this.editMode, this.form); // uncomment to debug

    const payload = {
      firstName: this.form.firstName,
      lastName: this.form.lastName,
      birthDate: this.form.birthDate,
    };

    if (this.editMode && this.form.id != null) {
      this.parishionerService
        .updateParishioner({
          parishionerId: this.form.id,
          body: payload,
        })
        .subscribe(() => {
          this.load();
          this.close();
        });
    } else {
      this.parishionerService
        .createParishioner({
          body: payload,
        })
        .subscribe(() => {
          this.load();
          this.close();
        });
    }
  }

  delete(id: number) {
    this.parishionerService.deleteParishioner({ parishionerId: id }).subscribe(() => this.load());
  }
}
