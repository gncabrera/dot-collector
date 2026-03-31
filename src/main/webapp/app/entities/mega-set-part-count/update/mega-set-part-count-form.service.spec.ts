import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-set-part-count.test-samples';

import { MegaSetPartCountFormService } from './mega-set-part-count-form.service';

describe('MegaSetPartCount Form Service', () => {
  let service: MegaSetPartCountFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaSetPartCountFormService);
  });

  describe('Service methods', () => {
    describe('createMegaSetPartCountFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaSetPartCountFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            count: expect.any(Object),
            set: expect.any(Object),
            part: expect.any(Object),
          }),
        );
      });

      it('passing IMegaSetPartCount should create a new form with FormGroup', () => {
        const formGroup = service.createMegaSetPartCountFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            count: expect.any(Object),
            set: expect.any(Object),
            part: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaSetPartCount', () => {
      it('should return NewMegaSetPartCount for default MegaSetPartCount initial value', () => {
        const formGroup = service.createMegaSetPartCountFormGroup(sampleWithNewData);

        const megaSetPartCount = service.getMegaSetPartCount(formGroup);

        expect(megaSetPartCount).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaSetPartCount for empty MegaSetPartCount initial value', () => {
        const formGroup = service.createMegaSetPartCountFormGroup();

        const megaSetPartCount = service.getMegaSetPartCount(formGroup);

        expect(megaSetPartCount).toMatchObject({});
      });

      it('should return IMegaSetPartCount', () => {
        const formGroup = service.createMegaSetPartCountFormGroup(sampleWithRequiredData);

        const megaSetPartCount = service.getMegaSetPartCount(formGroup);

        expect(megaSetPartCount).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaSetPartCount should not enable id FormControl', () => {
        const formGroup = service.createMegaSetPartCountFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaSetPartCount should disable id FormControl', () => {
        const formGroup = service.createMegaSetPartCountFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
