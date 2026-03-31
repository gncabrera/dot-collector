import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { MegaSetPartCountDetail } from './mega-set-part-count-detail';

describe('MegaSetPartCount Management Detail Component', () => {
  let comp: MegaSetPartCountDetail;
  let fixture: ComponentFixture<MegaSetPartCountDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mega-set-part-count-detail').then(m => m.MegaSetPartCountDetail),
              resolve: { megaSetPartCount: () => of({ id: 31677 }) },
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
    fixture = TestBed.createComponent(MegaSetPartCountDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load megaSetPartCount on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MegaSetPartCountDetail);

      // THEN
      expect(instance.megaSetPartCount()).toEqual(expect.objectContaining({ id: 31677 }));
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
