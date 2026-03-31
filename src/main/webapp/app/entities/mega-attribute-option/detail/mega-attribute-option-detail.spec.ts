import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { MegaAttributeOptionDetail } from './mega-attribute-option-detail';

describe('MegaAttributeOption Management Detail Component', () => {
  let comp: MegaAttributeOptionDetail;
  let fixture: ComponentFixture<MegaAttributeOptionDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mega-attribute-option-detail').then(m => m.MegaAttributeOptionDetail),
              resolve: { megaAttributeOption: () => of({ id: 23475 }) },
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
    fixture = TestBed.createComponent(MegaAttributeOptionDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load megaAttributeOption on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MegaAttributeOptionDetail);

      // THEN
      expect(instance.megaAttributeOption()).toEqual(expect.objectContaining({ id: 23475 }));
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
