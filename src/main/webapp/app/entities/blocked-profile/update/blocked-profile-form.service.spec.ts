import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../blocked-profile.test-samples';

import { BlockedProfileFormService } from './blocked-profile-form.service';

describe('BlockedProfile Form Service', () => {
  let service: BlockedProfileFormService;

  beforeEach(() => {
    service = TestBed.inject(BlockedProfileFormService);
  });

  describe('Service methods', () => {
    describe('createBlockedProfileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBlockedProfileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            dateBlocked: expect.any(Object),
            profile: expect.any(Object),
            blockedProfile: expect.any(Object),
          }),
        );
      });

      it('passing IBlockedProfile should create a new form with FormGroup', () => {
        const formGroup = service.createBlockedProfileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            reason: expect.any(Object),
            dateBlocked: expect.any(Object),
            profile: expect.any(Object),
            blockedProfile: expect.any(Object),
          }),
        );
      });
    });

    describe('getBlockedProfile', () => {
      it('should return NewBlockedProfile for default BlockedProfile initial value', () => {
        const formGroup = service.createBlockedProfileFormGroup(sampleWithNewData);

        const blockedProfile = service.getBlockedProfile(formGroup);

        expect(blockedProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewBlockedProfile for empty BlockedProfile initial value', () => {
        const formGroup = service.createBlockedProfileFormGroup();

        const blockedProfile = service.getBlockedProfile(formGroup);

        expect(blockedProfile).toMatchObject({});
      });

      it('should return IBlockedProfile', () => {
        const formGroup = service.createBlockedProfileFormGroup(sampleWithRequiredData);

        const blockedProfile = service.getBlockedProfile(formGroup);

        expect(blockedProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBlockedProfile should not enable id FormControl', () => {
        const formGroup = service.createBlockedProfileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBlockedProfile should disable id FormControl', () => {
        const formGroup = service.createBlockedProfileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
