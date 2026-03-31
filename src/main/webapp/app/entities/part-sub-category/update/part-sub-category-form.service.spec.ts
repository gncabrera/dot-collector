import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../part-sub-category.test-samples';

import { PartSubCategoryFormService } from './part-sub-category-form.service';

describe('PartSubCategory Form Service', () => {
  let service: PartSubCategoryFormService;

  beforeEach(() => {
    service = TestBed.inject(PartSubCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createPartSubCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartSubCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            megaParts: expect.any(Object),
          }),
        );
      });

      it('passing IPartSubCategory should create a new form with FormGroup', () => {
        const formGroup = service.createPartSubCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            megaParts: expect.any(Object),
          }),
        );
      });
    });

    describe('getPartSubCategory', () => {
      it('should return NewPartSubCategory for default PartSubCategory initial value', () => {
        const formGroup = service.createPartSubCategoryFormGroup(sampleWithNewData);

        const partSubCategory = service.getPartSubCategory(formGroup);

        expect(partSubCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewPartSubCategory for empty PartSubCategory initial value', () => {
        const formGroup = service.createPartSubCategoryFormGroup();

        const partSubCategory = service.getPartSubCategory(formGroup);

        expect(partSubCategory).toMatchObject({});
      });

      it('should return IPartSubCategory', () => {
        const formGroup = service.createPartSubCategoryFormGroup(sampleWithRequiredData);

        const partSubCategory = service.getPartSubCategory(formGroup);

        expect(partSubCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPartSubCategory should not enable id FormControl', () => {
        const formGroup = service.createPartSubCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPartSubCategory should disable id FormControl', () => {
        const formGroup = service.createPartSubCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
