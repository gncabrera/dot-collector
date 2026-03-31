import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faArrowLeft, faPencilAlt } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';

import { FollowingProfileDetail } from './following-profile-detail';

describe('FollowingProfile Management Detail Component', () => {
  let comp: FollowingProfileDetail;
  let fixture: ComponentFixture<FollowingProfileDetail>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./following-profile-detail').then(m => m.FollowingProfileDetail),
              resolve: { followingProfile: () => of({ id: 29154 }) },
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
    fixture = TestBed.createComponent(FollowingProfileDetail);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load followingProfile on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FollowingProfileDetail);

      // THEN
      expect(instance.followingProfile()).toEqual(expect.objectContaining({ id: 29154 }));
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
