import { Component, AfterViewInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import Chart from 'chart.js/auto';
import { BenchmarkService } from '../api/services';
import {
  DeleteTestOutputDto,
  MongoIndexTestOutputDto,
  ReadTestOutputDto,
  UpdateTestOutputDto,
  WriteTestOutputDto,
} from '../api/models';

export type BenchmarkType =
  | 'writes'
  | 'reads'
  | 'updates'
  | 'deletes'
  | 'mongo-index'
  | 'aggregation';

@Component({
  selector: 'app-benchmarks',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './benchmark.html',
  styleUrl: './benchmark.scss',
})
export class BenchmarksComponent implements AfterViewInit {
  loading = signal(false);
  error = signal<string | null>(null);

  results = signal<any>(undefined); // raw backend response
  lastType = signal<BenchmarkType | null>(null);
  lastPgTime = signal<number | null>(null);
  lastMgTime = signal<number | null>(null);

  private chart: Chart | null = null;

  benchmarkService = inject(BenchmarkService);

  ngAfterViewInit(): void {
    this.chart = new Chart('benchmarkChart', {
      type: 'line',
      data: {
        labels: [],
        datasets: [
          {
            label: 'Postgres (ms)',
            data: [],
            borderColor: 'rgba(0, 180, 255, 0.9)',
            tension: 0.3,
          },
          {
            label: 'MongoDB (ms)',
            data: [],
            borderColor: 'rgba(255, 80, 160, 0.9)',
            tension: 0.3,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
  }

  run(type: BenchmarkType): void {
    this.lastType.set(type);
    this.loading.set(true);
    this.error.set(null);

    switch (type) {
      case 'writes': {
        this.benchmarkService.runWrites().subscribe({
          next: (res) => {
            console.log('FRONTEND BENCHMARK RESPONSE', res);

            this.loading.set(false);

            this.results.set(res);

            const values = Object.values(res);

            const pgTimes = values.map((o) => o.postgresTimeMs) as number[];
            this.lastPgTime.set(pgTimes.reduce((acc, val) => acc + val, 0) / pgTimes.length);

            const mgTimes = values.map((o) => o.mongoTimeMs) as number[];
            this.lastMgTime.set(mgTimes.reduce((acc, val) => acc + val, 0) / mgTimes.length);

            this.renderWritesBarChart(res);
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
            this.error.set('Benchmark request failed – check backend logs.');
          },
        });
        break;
      }
      case 'reads': {
        this.benchmarkService.runReads().subscribe({
          next: (res) => {
            console.log('FRONTEND BENCHMARK RESPONSE', res);

            this.loading.set(false);

            this.results.set(res);

            const { pg, mg } = this.extractTimes(res, type);
            this.lastPgTime.set(pg);
            this.lastMgTime.set(mg);

            this.renderReadsLineChart(res);
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
            this.error.set('Benchmark request failed – check backend logs.');
          },
        });
        break;
      }
      case 'updates': {
        this.benchmarkService.runUpdates().subscribe({
          next: (res) => {
            console.log('FRONTEND BENCHMARK RESPONSE', res);

            this.loading.set(false);

            this.results.set(res);

            const { pg, mg } = this.extractTimes(res, type);
            this.lastPgTime.set(pg);
            this.lastMgTime.set(mg);

            this.renderUpdatesBarChart(res);
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
            this.error.set('Benchmark request failed – check backend logs.');
          },
        });
        break;
      }
      case 'deletes': {
        this.benchmarkService.runDeletes().subscribe({
          next: (res) => {
            console.log('FRONTEND BENCHMARK RESPONSE', res);

            this.loading.set(false);

            this.results.set(res);

            const { pg, mg } = this.extractTimes(res, type);
            this.lastPgTime.set(pg);
            this.lastMgTime.set(mg);

            this.renderDeletesBarChart(res);
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
            this.error.set('Benchmark request failed – check backend logs.');
          },
        });
        break;
      }
      case 'mongo-index': {
        this.benchmarkService.runMongoIndex().subscribe({
          next: (res) => {
            console.log('FRONTEND BENCHMARK RESPONSE', res);

            this.loading.set(false);

            this.results.set(res);

            this.lastPgTime.set(null);

            const mgTimes = ((res.mongoFindTimeMs ?? 0) + (res.mongoFindWithIndexTimeMs ?? 0)) / 2;
            this.lastMgTime.set(mgTimes);

            this.renderMongoIndexBarChart(res);
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
            this.error.set('Benchmark request failed – check backend logs.');
          },
        });
        break;
      }
      case 'aggregation': {
        this.benchmarkService.runAggregation().subscribe({
          next: (res) => {
            console.log('FRONTEND BENCHMARK RESPONSE', res);

            this.loading.set(false);

            this.results.set(res);

            const { pg, mg } = this.extractTimes(res, type);
            this.lastPgTime.set(pg);
            this.lastMgTime.set(mg);

            this.renderAggregationBarChart(res);
          },
          error: (err) => {
            console.error(err);
            this.loading.set(false);
            this.error.set('Benchmark request failed – check backend logs.');
          },
        });
        break;
      }
    }
  }

  labelForType(type: BenchmarkType): string {
    switch (type) {
      case 'writes':
        return 'WRITES';
      case 'reads':
        return 'READS';
      case 'updates':
        return 'UPDATES';
      case 'deletes':
        return 'DELETES';
      default:
        return '';
    }
  }

  renderWritesBarChart(data: { [key: string]: WriteTestOutputDto }) {
    const keys = Object.keys(data);
    const values = Object.values(data);

    this.chart?.destroy();
    this.chart = new Chart('benchmarkChart', {
      type: 'bar',
      data: {
        labels: keys,
        datasets: [
          {
            label: 'Postgres (ms)',
            data: values.map((v) => v.postgresTimeMs ?? 0),
            borderColor: 'rgba(0, 180, 255, 0.9)',
            backgroundColor: 'rgba(0, 180, 255, 0.9)',
          },
          {
            label: 'MongoDB (ms)',
            data: values.map((v) => v.mongoTimeMs ?? 0),
            borderColor: 'rgba(255, 80, 160, 0.9)',
            backgroundColor: 'rgba(255, 80, 160, 0.9)',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
    this.chart.render();
  }

  renderReadsLineChart(data: ReadTestOutputDto) {
    const labels = [
      'Find All',
      'With Filter',
      'With Filter + Projection',
      'With Filter + Projection + Sort',
    ];

    const postgresValues = [
      data.postgresFindAllTimeMs,
      data.postgresWithFilterTimeMs,
      data.postgresWithFilterAndProjectionTimeMs,
      data.postgresWithFilterAndProjectionAndSortTimeMs,
    ] as number[];

    const mongoValues = [
      data.mongoFindAllTimeMs,
      data.mongoWithFilterTimeMs,
      data.mongoWithFilterAndProjectionTimeMs,
      data.mongoWithFilterAndProjectionAndSortTimeMs,
    ] as number[];

    this.chart?.destroy();
    this.chart = new Chart('benchmarkChart', {
      type: 'line',
      data: {
        labels,
        datasets: [
          {
            label: 'PostgreSQL',
            data: postgresValues,
            borderColor: 'rgba(0, 180, 255, 0.9)',
            backgroundColor: 'rgba(0, 180, 255, 0.9)',
            tension: 0.3,
            pointRadius: 5,
          },
          {
            label: 'MongoDB',
            data: mongoValues,
            borderColor: 'rgba(255, 80, 160, 0.9)',
            backgroundColor: 'rgba(255, 80, 160, 0.9)',
            tension: 0.3,
            pointRadius: 5,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
    this.chart.render();
  }

  renderUpdatesBarChart(data: UpdateTestOutputDto) {
    this.chart?.destroy();
    this.chart = new Chart('benchmarkChart', {
      type: 'bar',
      data: {
        labels: ['Update'],
        datasets: [
          {
            label: 'Postgres (ms)',
            data: [data.postgresTimeMs ?? 0],
            borderColor: 'rgba(0, 180, 255, 0.9)',
            backgroundColor: 'rgba(0, 180, 255, 0.9)',
          },
          {
            label: 'MongoDB (ms)',
            data: [data.mongoTimeMs ?? 0],
            borderColor: 'rgba(255, 80, 160, 0.9)',
            backgroundColor: 'rgba(255, 80, 160, 0.9)',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
    this.chart.render();
  }

  renderDeletesBarChart(data: DeleteTestOutputDto) {
    this.chart?.destroy();
    this.chart = new Chart('benchmarkChart', {
      type: 'bar',
      data: {
        labels: ['Delete'],
        datasets: [
          {
            label: 'Postgres (ms)',
            data: [data.postgresTimeMs ?? 0],
            borderColor: 'rgba(0, 180, 255, 0.9)',
            backgroundColor: 'rgba(0, 180, 255, 0.9)',
          },
          {
            label: 'MongoDB (ms)',
            data: [data.mongoTimeMs ?? 0],
            borderColor: 'rgba(255, 80, 160, 0.9)',
            backgroundColor: 'rgba(255, 80, 160, 0.9)',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
    this.chart.render();
  }

  renderMongoIndexBarChart(data: MongoIndexTestOutputDto) {
    this.chart?.destroy();
    this.chart = new Chart('benchmarkChart', {
      type: 'bar',
      data: {
        labels: ['Find w/o Index'],
        datasets: [
          {
            label: 'No Index (ms)',
            data: [data.mongoFindTimeMs ?? 0],
            borderColor: 'rgba(156, 49, 97, 0.9)',
            backgroundColor: 'rgba(156, 49, 97, 0.9)',
          },
          {
            label: 'With Index (ms)',
            data: [data.mongoFindWithIndexTimeMs ?? 0],
            borderColor: 'rgba(255, 80, 160, 0.9)',
            backgroundColor: 'rgba(255, 80, 160, 0.9)',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
    this.chart.render();
  }

  renderAggregationBarChart(data: DeleteTestOutputDto) {
    this.chart?.destroy();
    this.chart = new Chart('benchmarkChart', {
      type: 'bar',
      data: {
        labels: ['Aggregate (avg foundedYear)'],
        datasets: [
          {
            label: 'Postgres (ms)',
            data: [data.postgresTimeMs ?? 0],
            borderColor: 'rgba(0, 180, 255, 0.9)',
            backgroundColor: 'rgba(0, 180, 255, 0.9)',
          },
          {
            label: 'MongoDB (ms)',
            data: [data.mongoTimeMs ?? 0],
            borderColor: 'rgba(255, 80, 160, 0.9)',
            backgroundColor: 'rgba(255, 80, 160, 0.9)',
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: { color: '#fbfbfb' },
          },
        },
        scales: {
          x: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
          y: {
            ticks: { color: '#aaaaaa' },
            grid: { color: 'rgba(255,255,255,0.05)' },
          },
        },
      },
    });
    this.chart.render();
  }

  private extractTimes(res: any, type: BenchmarkType): { pg: number | null; mg: number | null } {
    if (!res) return { pg: null, mg: null };

    if (type === 'reads') {
      const pg = this.averageByPrefix(res, 'postgres');
      const mg = this.averageByPrefix(res, 'mongo');
      return { pg, mg };
    }

    const pg = typeof res.postgresTimeMs === 'number' ? res.postgresTimeMs : null;
    const mg = typeof res.mongoTimeMs === 'number' ? res.mongoTimeMs : null;
    return { pg, mg };
  }

  private averageByPrefix(obj: any, prefix: string): number | null {
    const values: number[] = [];

    for (const [key, value] of Object.entries(obj)) {
      if (typeof value === 'number' && key.toLowerCase().startsWith(prefix.toLowerCase())) {
        values.push(value);
      }
    }

    if (!values.length) return null;
    const sum = values.reduce((acc, v) => acc + v, 0);
    return sum / values.length;
  }
}
