import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../profile-request-type.test-samples';

import { ProfileRequestTypeFormService } from './profile-request-type-form.service';

describe('ProfileRequestType Form Service', () => {
  let service: ProfileRequestTypeFormService;

  beforeEach(() => {
    service = TestBed.inject(ProfileRequestTypeFormService);
  });

  describe('Service methods', () => {
    describe('createProfileRequestTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfileRequestTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            key: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IProfileRequestType should create a new form with FormGroup', () => {
        const formGroup = service.createProfileRequestTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            key: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfileRequestType', () => {
      it('should return NewProfileRequestType for default ProfileRequestType initial value', () => {
        const formGroup = service.createProfileRequestTypeFormGroup(sampleWithNewData);

        const profileRequestType = service.getProfileRequestType(formGroup);

        expect(profileRequestType).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfileRequestType for empty ProfileRequestType initial value', () => {
        const formGroup = service.createProfileRequestTypeFormGroup();

        const profileRequestType = service.getProfileRequestType(formGroup);

        expect(profileRequestType).toMatchObject({});
      });

      it('should return IProfileRequestType', () => {
        const formGroup = service.createProfileRequestTypeFormGroup(sampleWithRequiredData);

        const profileRequestType = service.getProfileRequestType(formGroup);

        expect(profileRequestType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfileRequestType should not enable id FormControl', () => {
        const formGroup = service.createProfileRequestTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfileRequestType should disable id FormControl', () => {
        const formGroup = service.createProfileRequestTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
