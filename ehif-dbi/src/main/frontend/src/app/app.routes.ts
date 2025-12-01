import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./dashboard/dashboard').then((m) => m.Dashboard),
  },
  {
    path: 'postgres/parishioner',
    loadComponent: () =>
      import('./postgres/parishioner/parishioner').then((m) => m.ParishionerComponent),
  },
  {
    path: 'benchmark',
    loadComponent: () => import('./benchmark/benchmark').then((m) => m.BenchmarksComponent),
  },
  {
    path: 'postgres/parish',
    loadComponent: () => import('./postgres/parish/parish').then((m) => m.ParishComponent),
  },
];
