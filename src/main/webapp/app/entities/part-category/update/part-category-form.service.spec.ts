import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../part-category.test-samples';

import { PartCategoryFormService } from './part-category-form.service';

describe('PartCategory Form Service', () => {
  let service: PartCategoryFormService;

  beforeEach(() => {
    service = TestBed.inject(PartCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createPartCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IPartCategory should create a new form with FormGroup', () => {
        const formGroup = service.createPartCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getPartCategory', () => {
      it('should return NewPartCategory for default PartCategory initial value', () => {
        const formGroup = service.createPartCategoryFormGroup(sampleWithNewData);

        const partCategory = service.getPartCategory(formGroup);

        expect(partCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewPartCategory for empty PartCategory initial value', () => {
        const formGroup = service.createPartCategoryFormGroup();

        const partCategory = service.getPartCategory(formGroup);

        expect(partCategory).toMatchObject({});
      });

      it('should return IPartCategory', () => {
        const formGroup = service.createPartCategoryFormGroup(sampleWithRequiredData);

        const partCategory = service.getPartCategory(formGroup);

        expect(partCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPartCategory should not enable id FormControl', () => {
        const formGroup = service.createPartCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPartCategory should disable id FormControl', () => {
        const formGroup = service.createPartCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
