import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Parishdocument } from './parishdocument';

describe('Parishdocument', () => {
  let component: Parishdocument;
  let fixture: ComponentFixture<Parishdocument>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Parishdocument]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Parishdocument);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
