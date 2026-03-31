import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../profile-collection.test-samples';

import { ProfileCollectionFormService } from './profile-collection-form.service';

describe('ProfileCollection Form Service', () => {
  let service: ProfileCollectionFormService;

  beforeEach(() => {
    service = TestBed.inject(ProfileCollectionFormService);
  });

  describe('Service methods', () => {
    describe('createProfileCollectionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfileCollectionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            isPublic: expect.any(Object),
            profile: expect.any(Object),
          }),
        );
      });

      it('passing IProfileCollection should create a new form with FormGroup', () => {
        const formGroup = service.createProfileCollectionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            isPublic: expect.any(Object),
            profile: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfileCollection', () => {
      it('should return NewProfileCollection for default ProfileCollection initial value', () => {
        const formGroup = service.createProfileCollectionFormGroup(sampleWithNewData);

        const profileCollection = service.getProfileCollection(formGroup);

        expect(profileCollection).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfileCollection for empty ProfileCollection initial value', () => {
        const formGroup = service.createProfileCollectionFormGroup();

        const profileCollection = service.getProfileCollection(formGroup);

        expect(profileCollection).toMatchObject({});
      });

      it('should return IProfileCollection', () => {
        const formGroup = service.createProfileCollectionFormGroup(sampleWithRequiredData);

        const profileCollection = service.getProfileCollection(formGroup);

        expect(profileCollection).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfileCollection should not enable id FormControl', () => {
        const formGroup = service.createProfileCollectionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfileCollection should disable id FormControl', () => {
        const formGroup = service.createProfileCollectionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
