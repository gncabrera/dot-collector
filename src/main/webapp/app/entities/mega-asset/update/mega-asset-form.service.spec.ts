import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-asset.test-samples';

import { MegaAssetFormService } from './mega-asset-form.service';

describe('MegaAsset Form Service', () => {
  let service: MegaAssetFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaAssetFormService);
  });

  describe('Service methods', () => {
    describe('createMegaAssetFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaAssetFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            path: expect.any(Object),
            type: expect.any(Object),
            set: expect.any(Object),
            part: expect.any(Object),
          }),
        );
      });

      it('passing IMegaAsset should create a new form with FormGroup', () => {
        const formGroup = service.createMegaAssetFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            path: expect.any(Object),
            type: expect.any(Object),
            set: expect.any(Object),
            part: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaAsset', () => {
      it('should return NewMegaAsset for default MegaAsset initial value', () => {
        const formGroup = service.createMegaAssetFormGroup(sampleWithNewData);

        const megaAsset = service.getMegaAsset(formGroup);

        expect(megaAsset).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaAsset for empty MegaAsset initial value', () => {
        const formGroup = service.createMegaAssetFormGroup();

        const megaAsset = service.getMegaAsset(formGroup);

        expect(megaAsset).toMatchObject({});
      });

      it('should return IMegaAsset', () => {
        const formGroup = service.createMegaAssetFormGroup(sampleWithRequiredData);

        const megaAsset = service.getMegaAsset(formGroup);

        expect(megaAsset).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaAsset should not enable id FormControl', () => {
        const formGroup = service.createMegaAssetFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaAsset should disable id FormControl', () => {
        const formGroup = service.createMegaAssetFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
