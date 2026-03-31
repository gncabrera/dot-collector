import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { MegaAttributeDetail } from './mega-attribute-detail';

describe('MegaAttribute Management Detail Component', () => {
  let comp: MegaAttributeDetail;
  let fixture: ComponentFixture<MegaAttributeDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./mega-attribute-detail').then(m => m.MegaAttributeDetail),
              resolve: { megaAttribute: () => of({ id: 12325 }) },
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
    fixture = TestBed.createComponent(MegaAttributeDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load megaAttribute on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MegaAttributeDetail);

      // THEN
      expect(instance.megaAttribute()).toEqual(expect.objectContaining({ id: 12325 }));
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
