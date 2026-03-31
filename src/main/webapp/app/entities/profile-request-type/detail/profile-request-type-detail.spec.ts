import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { ProfileRequestTypeDetail } from './profile-request-type-detail';

describe('ProfileRequestType Management Detail Component', () => {
  let comp: ProfileRequestTypeDetail;
  let fixture: ComponentFixture<ProfileRequestTypeDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./profile-request-type-detail').then(m => m.ProfileRequestTypeDetail),
              resolve: { profileRequestType: () => of({ id: 27058 }) },
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
    fixture = TestBed.createComponent(ProfileRequestTypeDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load profileRequestType on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProfileRequestTypeDetail);

      // THEN
      expect(instance.profileRequestType()).toEqual(expect.objectContaining({ id: 27058 }));
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
