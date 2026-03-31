import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../profile-request.test-samples';

import { ProfileRequestFormService } from './profile-request-form.service';

describe('ProfileRequest Form Service', () => {
  let service: ProfileRequestFormService;

  beforeEach(() => {
    service = TestBed.inject(ProfileRequestFormService);
  });

  describe('Service methods', () => {
    describe('createProfileRequestFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfileRequestFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            type: expect.any(Object),
            profile: expect.any(Object),
          }),
        );
      });

      it('passing IProfileRequest should create a new form with FormGroup', () => {
        const formGroup = service.createProfileRequestFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            type: expect.any(Object),
            profile: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfileRequest', () => {
      it('should return NewProfileRequest for default ProfileRequest initial value', () => {
        const formGroup = service.createProfileRequestFormGroup(sampleWithNewData);

        const profileRequest = service.getProfileRequest(formGroup);

        expect(profileRequest).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfileRequest for empty ProfileRequest initial value', () => {
        const formGroup = service.createProfileRequestFormGroup();

        const profileRequest = service.getProfileRequest(formGroup);

        expect(profileRequest).toMatchObject({});
      });

      it('should return IProfileRequest', () => {
        const formGroup = service.createProfileRequestFormGroup(sampleWithRequiredData);

        const profileRequest = service.getProfileRequest(formGroup);

        expect(profileRequest).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfileRequest should not enable id FormControl', () => {
        const formGroup = service.createProfileRequestFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfileRequest should disable id FormControl', () => {
        const formGroup = service.createProfileRequestFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
