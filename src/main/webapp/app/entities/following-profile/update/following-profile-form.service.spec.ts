import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../following-profile.test-samples';

import { FollowingProfileFormService } from './following-profile-form.service';

describe('FollowingProfile Form Service', () => {
  let service: FollowingProfileFormService;

  beforeEach(() => {
    service = TestBed.inject(FollowingProfileFormService);
  });

  describe('Service methods', () => {
    describe('createFollowingProfileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFollowingProfileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateFollowing: expect.any(Object),
            profile: expect.any(Object),
            followedProfile: expect.any(Object),
          }),
        );
      });

      it('passing IFollowingProfile should create a new form with FormGroup', () => {
        const formGroup = service.createFollowingProfileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dateFollowing: expect.any(Object),
            profile: expect.any(Object),
            followedProfile: expect.any(Object),
          }),
        );
      });
    });

    describe('getFollowingProfile', () => {
      it('should return NewFollowingProfile for default FollowingProfile initial value', () => {
        const formGroup = service.createFollowingProfileFormGroup(sampleWithNewData);

        const followingProfile = service.getFollowingProfile(formGroup);

        expect(followingProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewFollowingProfile for empty FollowingProfile initial value', () => {
        const formGroup = service.createFollowingProfileFormGroup();

        const followingProfile = service.getFollowingProfile(formGroup);

        expect(followingProfile).toMatchObject({});
      });

      it('should return IFollowingProfile', () => {
        const formGroup = service.createFollowingProfileFormGroup(sampleWithRequiredData);

        const followingProfile = service.getFollowingProfile(formGroup);

        expect(followingProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFollowingProfile should not enable id FormControl', () => {
        const formGroup = service.createFollowingProfileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFollowingProfile should disable id FormControl', () => {
        const formGroup = service.createFollowingProfileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
