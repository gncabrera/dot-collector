import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { MegaPartTypeDetail } from './mega-part-type-detail';

describe('MegaPartType Management Detail Component', () => {
  let comp: MegaPartTypeDetail;
  let fixture: ComponentFixture<MegaPartTypeDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mega-part-type-detail').then(m => m.MegaPartTypeDetail),
              resolve: { megaPartType: () => of({ id: 8406 }) },
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
    fixture = TestBed.createComponent(MegaPartTypeDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load megaPartType on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MegaPartTypeDetail);

      // THEN
      expect(instance.megaPartType()).toEqual(expect.objectContaining({ id: 8406 }));
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
