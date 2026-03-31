import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-part-type.test-samples';

import { MegaPartTypeFormService } from './mega-part-type-form.service';

describe('MegaPartType Form Service', () => {
  let service: MegaPartTypeFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaPartTypeFormService);
  });

  describe('Service methods', () => {
    describe('createMegaPartTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaPartTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            version: expect.any(Object),
            active: expect.any(Object),
            isLatest: expect.any(Object),
            attributes: expect.any(Object),
          }),
        );
      });

      it('passing IMegaPartType should create a new form with FormGroup', () => {
        const formGroup = service.createMegaPartTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            version: expect.any(Object),
            active: expect.any(Object),
            isLatest: expect.any(Object),
            attributes: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaPartType', () => {
      it('should return NewMegaPartType for default MegaPartType initial value', () => {
        const formGroup = service.createMegaPartTypeFormGroup(sampleWithNewData);

        const megaPartType = service.getMegaPartType(formGroup);

        expect(megaPartType).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaPartType for empty MegaPartType initial value', () => {
        const formGroup = service.createMegaPartTypeFormGroup();

        const megaPartType = service.getMegaPartType(formGroup);

        expect(megaPartType).toMatchObject({});
      });

      it('should return IMegaPartType', () => {
        const formGroup = service.createMegaPartTypeFormGroup(sampleWithRequiredData);

        const megaPartType = service.getMegaPartType(formGroup);

        expect(megaPartType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaPartType should not enable id FormControl', () => {
        const formGroup = service.createMegaPartTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaPartType should disable id FormControl', () => {
        const formGroup = service.createMegaPartTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
