import { Component, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BenchmarkService, BenchmarkType } from '../shared/services/benchmark';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-benchmarks',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './benchmark.html',
  styleUrl: './benchmark.scss',
})
export class BenchmarksComponent implements AfterViewInit {
  loading = false;
  error: string | null = null;

  results: any; // raw backend response
  lastType: BenchmarkType | null = null;
  lastPgTime: number | null = null;
  lastMgTime: number | null = null;

  private chart: Chart | null = null;

  constructor(private benchmarkService: BenchmarkService, private cdr: ChangeDetectorRef) {}

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
    this.lastType = type;
    this.loading = true;
    this.error = null;

    this.results = null;
    this.lastPgTime = null;
    this.lastMgTime = null;

    this.benchmarkService.runBenchmark(type).subscribe({
      next: (res: any) => {
        console.log('FRONTEND BENCHMARK RESPONSE', res);

        this.loading = false;
        this.results = res;

        const { pg, mg } = this.extractTimes(res, type);
        this.lastPgTime = pg;
        this.lastMgTime = mg;

        if (this.chart && typeof pg === 'number' && typeof mg === 'number') {
          this.chart.data.labels!.push(this.labelForType(type));
          (this.chart.data.datasets[0].data as number[]).push(pg);
          (this.chart.data.datasets[1].data as number[]).push(mg);
          this.chart.update();
        }

        // 🔥 Tell Angular: "hey, data changed!"
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.error = 'Benchmark request failed – check backend logs.';
        this.cdr.detectChanges(); // also update error UI
      },
    });
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

  get hasTimes(): boolean {
    return typeof this.lastPgTime === 'number' && typeof this.lastMgTime === 'number';
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
}
