import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'benchmark',
    loadComponent: () => import('./benchmark/benchmark').then((m) => m.BenchmarksComponent),
  },
];
