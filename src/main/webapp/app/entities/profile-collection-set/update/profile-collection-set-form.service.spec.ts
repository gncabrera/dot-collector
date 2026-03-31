import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../profile-collection-set.test-samples';

import { ProfileCollectionSetFormService } from './profile-collection-set-form.service';

describe('ProfileCollectionSet Form Service', () => {
  let service: ProfileCollectionSetFormService;

  beforeEach(() => {
    service = TestBed.inject(ProfileCollectionSetFormService);
  });

  describe('Service methods', () => {
    describe('createProfileCollectionSetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfileCollectionSetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            owned: expect.any(Object),
            wanted: expect.any(Object),
            dateAdded: expect.any(Object),
            collection: expect.any(Object),
            sets: expect.any(Object),
          }),
        );
      });

      it('passing IProfileCollectionSet should create a new form with FormGroup', () => {
        const formGroup = service.createProfileCollectionSetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            owned: expect.any(Object),
            wanted: expect.any(Object),
            dateAdded: expect.any(Object),
            collection: expect.any(Object),
            sets: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfileCollectionSet', () => {
      it('should return NewProfileCollectionSet for default ProfileCollectionSet initial value', () => {
        const formGroup = service.createProfileCollectionSetFormGroup(sampleWithNewData);

        const profileCollectionSet = service.getProfileCollectionSet(formGroup);

        expect(profileCollectionSet).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfileCollectionSet for empty ProfileCollectionSet initial value', () => {
        const formGroup = service.createProfileCollectionSetFormGroup();

        const profileCollectionSet = service.getProfileCollectionSet(formGroup);

        expect(profileCollectionSet).toMatchObject({});
      });

      it('should return IProfileCollectionSet', () => {
        const formGroup = service.createProfileCollectionSetFormGroup(sampleWithRequiredData);

        const profileCollectionSet = service.getProfileCollectionSet(formGroup);

        expect(profileCollectionSet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfileCollectionSet should not enable id FormControl', () => {
        const formGroup = service.createProfileCollectionSetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfileCollectionSet should disable id FormControl', () => {
        const formGroup = service.createProfileCollectionSetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
