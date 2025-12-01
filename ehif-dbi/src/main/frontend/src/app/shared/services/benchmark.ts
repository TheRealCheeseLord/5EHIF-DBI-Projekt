import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type BenchmarkType = 'writes' | 'reads' | 'updates' | 'deletes';

@Injectable({ providedIn: 'root' })
export class BenchmarkService {
  private readonly baseUrl = '/api/benchmarks';

  constructor(private http: HttpClient) {}

  runBenchmark(type: BenchmarkType): Observable<any> {
    switch (type) {
      case 'writes':
        return this.http.get(`${this.baseUrl}/writes`);
      case 'reads':
        return this.http.get(`${this.baseUrl}/reads`);
      case 'updates':
        return this.http.get(`${this.baseUrl}/updates`);
      case 'deletes':
        return this.http.get(`${this.baseUrl}/deletes`);
    }
  }
}
