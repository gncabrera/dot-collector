import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { MegaPartSubPartCountDetail } from './mega-part-sub-part-count-detail';

describe('MegaPartSubPartCount Management Detail Component', () => {
  let comp: MegaPartSubPartCountDetail;
  let fixture: ComponentFixture<MegaPartSubPartCountDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mega-part-sub-part-count-detail').then(m => m.MegaPartSubPartCountDetail),
              resolve: { megaPartSubPartCount: () => of({ id: 4746 }) },
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
    fixture = TestBed.createComponent(MegaPartSubPartCountDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load megaPartSubPartCount on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MegaPartSubPartCountDetail);

      // THEN
      expect(instance.megaPartSubPartCount()).toEqual(expect.objectContaining({ id: 4746 }));
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
