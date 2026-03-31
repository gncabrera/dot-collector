import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { PartSubCategoryDetail } from './part-sub-category-detail';

describe('PartSubCategory Management Detail Component', () => {
  let comp: PartSubCategoryDetail;
  let fixture: ComponentFixture<PartSubCategoryDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./part-sub-category-detail').then(m => m.PartSubCategoryDetail),
              resolve: { partSubCategory: () => of({ id: 12026 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    });
    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faArrowLeft);
    library.addIcons(faPencilAlt);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PartSubCategoryDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load partSubCategory on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PartSubCategoryDetail);

      // THEN
      expect(instance.partSubCategory()).toEqual(expect.objectContaining({ id: 12026 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      vitest.spyOn(globalThis.history, 'back');
      comp.previousState();
      expect(globalThis.history.back).toHaveBeenCalled();
    });
  });
});
