import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-set.test-samples';

import { MegaSetFormService } from './mega-set-form.service';

describe('MegaSet Form Service', () => {
  let service: MegaSetFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaSetFormService);
  });

  describe('Service methods', () => {
    describe('createMegaSetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaSetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            setNumber: expect.any(Object),
            releaseDate: expect.any(Object),
            notes: expect.any(Object),
            nameEN: expect.any(Object),
            nameES: expect.any(Object),
            nameDE: expect.any(Object),
            nameFR: expect.any(Object),
            descriptionEN: expect.any(Object),
            descriptionES: expect.any(Object),
            descriptionDE: expect.any(Object),
            descriptionFR: expect.any(Object),
            attributes: expect.any(Object),
            type: expect.any(Object),
            profileCollectionSets: expect.any(Object),
          }),
        );
      });

      it('passing IMegaSet should create a new form with FormGroup', () => {
        const formGroup = service.createMegaSetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            setNumber: expect.any(Object),
            releaseDate: expect.any(Object),
            notes: expect.any(Object),
            nameEN: expect.any(Object),
            nameES: expect.any(Object),
            nameDE: expect.any(Object),
            nameFR: expect.any(Object),
            descriptionEN: expect.any(Object),
            descriptionES: expect.any(Object),
            descriptionDE: expect.any(Object),
            descriptionFR: expect.any(Object),
            attributes: expect.any(Object),
            type: expect.any(Object),
            profileCollectionSets: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaSet', () => {
      it('should return NewMegaSet for default MegaSet initial value', () => {
        const formGroup = service.createMegaSetFormGroup(sampleWithNewData);

        const megaSet = service.getMegaSet(formGroup);

        expect(megaSet).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaSet for empty MegaSet initial value', () => {
        const formGroup = service.createMegaSetFormGroup();

        const megaSet = service.getMegaSet(formGroup);

        expect(megaSet).toMatchObject({});
      });

      it('should return IMegaSet', () => {
        const formGroup = service.createMegaSetFormGroup(sampleWithRequiredData);

        const megaSet = service.getMegaSet(formGroup);

        expect(megaSet).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaSet should not enable id FormControl', () => {
        const formGroup = service.createMegaSetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaSet should disable id FormControl', () => {
        const formGroup = service.createMegaSetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
