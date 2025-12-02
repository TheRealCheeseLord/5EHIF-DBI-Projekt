import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Priest } from './priest';

describe('Priest', () => {
  let component: Priest;
  let fixture: ComponentFixture<Priest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Priest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Priest);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
