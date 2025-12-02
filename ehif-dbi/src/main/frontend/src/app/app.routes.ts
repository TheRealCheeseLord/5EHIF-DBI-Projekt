import { Routes } from '@angular/router';
import { BenchmarksComponent } from './benchmark/benchmark';
import { Dashboard } from './dashboard/dashboard';
import { ParishionerComponent } from './postgres/parishioner/parishioner';
import { ParishComponent } from './postgres/parish/parish';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', pathMatch: 'full', component: Dashboard },
  { path: 'postgres/parishioner', pathMatch: 'full', component: ParishionerComponent },
  { path: 'postgres/parish', pathMatch: 'full', component: ParishComponent },
  { path: 'benchmark', pathMatch: 'full', component: BenchmarksComponent },
];
