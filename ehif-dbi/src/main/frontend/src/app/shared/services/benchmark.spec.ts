import { TestBed } from '@angular/core/testing';

import { Benchmark } from './benchmark';

describe('Benchmark', () => {
  let service: Benchmark;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Benchmark);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
