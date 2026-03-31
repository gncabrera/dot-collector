import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-part-sub-part-count.test-samples';

import { MegaPartSubPartCountFormService } from './mega-part-sub-part-count-form.service';

describe('MegaPartSubPartCount Form Service', () => {
  let service: MegaPartSubPartCountFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaPartSubPartCountFormService);
  });

  describe('Service methods', () => {
    describe('createMegaPartSubPartCountFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            count: expect.any(Object),
            part: expect.any(Object),
            parentPart: expect.any(Object),
          }),
        );
      });

      it('passing IMegaPartSubPartCount should create a new form with FormGroup', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            count: expect.any(Object),
            part: expect.any(Object),
            parentPart: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaPartSubPartCount', () => {
      it('should return NewMegaPartSubPartCount for default MegaPartSubPartCount initial value', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup(sampleWithNewData);

        const megaPartSubPartCount = service.getMegaPartSubPartCount(formGroup);

        expect(megaPartSubPartCount).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaPartSubPartCount for empty MegaPartSubPartCount initial value', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup();

        const megaPartSubPartCount = service.getMegaPartSubPartCount(formGroup);

        expect(megaPartSubPartCount).toMatchObject({});
      });

      it('should return IMegaPartSubPartCount', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup(sampleWithRequiredData);

        const megaPartSubPartCount = service.getMegaPartSubPartCount(formGroup);

        expect(megaPartSubPartCount).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaPartSubPartCount should not enable id FormControl', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaPartSubPartCount should disable id FormControl', () => {
        const formGroup = service.createMegaPartSubPartCountFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
