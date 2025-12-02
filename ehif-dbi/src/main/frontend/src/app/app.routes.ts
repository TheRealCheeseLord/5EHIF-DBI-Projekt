import { Routes } from '@angular/router';
import { BenchmarksComponent } from './benchmark/benchmark';
import { Dashboard } from './dashboard/dashboard';
import { ParishionerComponent } from './postgres/parishioner/parishioner';
import { ParishComponent } from './postgres/parish/parish';
import { PriestComponent } from './postgres/priest/priest';
import { Validate } from './validate/validate';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', pathMatch: 'full', component: Dashboard },
  { path: 'postgres/parishioners', pathMatch: 'full', component: ParishionerComponent },
  { path: 'postgres/parishes', pathMatch: 'full', component: ParishComponent },
  { path: 'postgres/priest', pathMatch: 'full', component: PriestComponent },
  { path: 'benchmark', pathMatch: 'full', component: BenchmarksComponent },
  { path: 'validate', pathMatch: 'full', component: Validate },
];
