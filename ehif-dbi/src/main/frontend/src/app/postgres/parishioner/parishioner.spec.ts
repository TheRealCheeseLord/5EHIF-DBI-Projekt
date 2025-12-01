import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Parishioner } from './parishioner';

describe('Parishioner', () => {
  let component: Parishioner;
  let fixture: ComponentFixture<Parishioner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Parishioner]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Parishioner);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
