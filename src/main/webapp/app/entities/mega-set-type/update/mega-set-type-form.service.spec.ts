import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-set-type.test-samples';

import { MegaSetTypeFormService } from './mega-set-type-form.service';

describe('MegaSetType Form Service', () => {
  let service: MegaSetTypeFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaSetTypeFormService);
  });

  describe('Service methods', () => {
    describe('createMegaSetTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaSetTypeFormGroup();

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

      it('passing IMegaSetType should create a new form with FormGroup', () => {
        const formGroup = service.createMegaSetTypeFormGroup(sampleWithRequiredData);

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

    describe('getMegaSetType', () => {
      it('should return NewMegaSetType for default MegaSetType initial value', () => {
        const formGroup = service.createMegaSetTypeFormGroup(sampleWithNewData);

        const megaSetType = service.getMegaSetType(formGroup);

        expect(megaSetType).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaSetType for empty MegaSetType initial value', () => {
        const formGroup = service.createMegaSetTypeFormGroup();

        const megaSetType = service.getMegaSetType(formGroup);

        expect(megaSetType).toMatchObject({});
      });

      it('should return IMegaSetType', () => {
        const formGroup = service.createMegaSetTypeFormGroup(sampleWithRequiredData);

        const megaSetType = service.getMegaSetType(formGroup);

        expect(megaSetType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaSetType should not enable id FormControl', () => {
        const formGroup = service.createMegaSetTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaSetType should disable id FormControl', () => {
        const formGroup = service.createMegaSetTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
