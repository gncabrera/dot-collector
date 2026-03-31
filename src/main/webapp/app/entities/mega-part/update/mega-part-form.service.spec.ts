import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-part.test-samples';

import { MegaPartFormService } from './mega-part-form.service';

describe('MegaPart Form Service', () => {
  let service: MegaPartFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaPartFormService);
  });

  describe('Service methods', () => {
    describe('createMegaPartFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaPartFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            releaseDate: expect.any(Object),
            partNumber: expect.any(Object),
            nameEN: expect.any(Object),
            nameES: expect.any(Object),
            nameDE: expect.any(Object),
            nameFR: expect.any(Object),
            description: expect.any(Object),
            notes: expect.any(Object),
            attributes: expect.any(Object),
            type: expect.any(Object),
            partCategory: expect.any(Object),
            partSubCategories: expect.any(Object),
          }),
        );
      });

      it('passing IMegaPart should create a new form with FormGroup', () => {
        const formGroup = service.createMegaPartFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            releaseDate: expect.any(Object),
            partNumber: expect.any(Object),
            nameEN: expect.any(Object),
            nameES: expect.any(Object),
            nameDE: expect.any(Object),
            nameFR: expect.any(Object),
            description: expect.any(Object),
            notes: expect.any(Object),
            attributes: expect.any(Object),
            type: expect.any(Object),
            partCategory: expect.any(Object),
            partSubCategories: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaPart', () => {
      it('should return NewMegaPart for default MegaPart initial value', () => {
        const formGroup = service.createMegaPartFormGroup(sampleWithNewData);

        const megaPart = service.getMegaPart(formGroup);

        expect(megaPart).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaPart for empty MegaPart initial value', () => {
        const formGroup = service.createMegaPartFormGroup();

        const megaPart = service.getMegaPart(formGroup);

        expect(megaPart).toMatchObject({});
      });

      it('should return IMegaPart', () => {
        const formGroup = service.createMegaPartFormGroup(sampleWithRequiredData);

        const megaPart = service.getMegaPart(formGroup);

        expect(megaPart).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaPart should not enable id FormControl', () => {
        const formGroup = service.createMegaPartFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaPart should disable id FormControl', () => {
        const formGroup = service.createMegaPartFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
