import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-attribute-option.test-samples';

import { MegaAttributeOptionFormService } from './mega-attribute-option-form.service';

describe('MegaAttributeOption Form Service', () => {
  let service: MegaAttributeOptionFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaAttributeOptionFormService);
  });

  describe('Service methods', () => {
    describe('createMegaAttributeOptionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            value: expect.any(Object),
            description: expect.any(Object),
            attribute: expect.any(Object),
          }),
        );
      });

      it('passing IMegaAttributeOption should create a new form with FormGroup', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            value: expect.any(Object),
            description: expect.any(Object),
            attribute: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaAttributeOption', () => {
      it('should return NewMegaAttributeOption for default MegaAttributeOption initial value', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup(sampleWithNewData);

        const megaAttributeOption = service.getMegaAttributeOption(formGroup);

        expect(megaAttributeOption).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaAttributeOption for empty MegaAttributeOption initial value', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup();

        const megaAttributeOption = service.getMegaAttributeOption(formGroup);

        expect(megaAttributeOption).toMatchObject({});
      });

      it('should return IMegaAttributeOption', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup(sampleWithRequiredData);

        const megaAttributeOption = service.getMegaAttributeOption(formGroup);

        expect(megaAttributeOption).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaAttributeOption should not enable id FormControl', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaAttributeOption should disable id FormControl', () => {
        const formGroup = service.createMegaAttributeOptionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
