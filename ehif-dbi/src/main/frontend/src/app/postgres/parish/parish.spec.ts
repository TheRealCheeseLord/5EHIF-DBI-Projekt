import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Parish } from './parish';

describe('Parish', () => {
  let component: Parish;
  let fixture: ComponentFixture<Parish>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Parish]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Parish);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
