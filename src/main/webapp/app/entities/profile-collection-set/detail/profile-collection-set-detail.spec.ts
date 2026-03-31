import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { ProfileCollectionSetDetail } from './profile-collection-set-detail';

describe('ProfileCollectionSet Management Detail Component', () => {
  let comp: ProfileCollectionSetDetail;
  let fixture: ComponentFixture<ProfileCollectionSetDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./profile-collection-set-detail').then(m => m.ProfileCollectionSetDetail),
              resolve: { profileCollectionSet: () => of({ id: 5128 }) },
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
    fixture = TestBed.createComponent(ProfileCollectionSetDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load profileCollectionSet on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProfileCollectionSetDetail);

      // THEN
      expect(instance.profileCollectionSet()).toEqual(expect.objectContaining({ id: 5128 }));
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
