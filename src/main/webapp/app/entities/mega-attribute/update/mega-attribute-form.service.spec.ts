import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../mega-attribute.test-samples';

import { MegaAttributeFormService } from './mega-attribute-form.service';

describe('MegaAttribute Form Service', () => {
  let service: MegaAttributeFormService;

  beforeEach(() => {
    service = TestBed.inject(MegaAttributeFormService);
  });

  describe('Service methods', () => {
    describe('createMegaAttributeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMegaAttributeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            label: expect.any(Object),
            description: expect.any(Object),
            uiComponent: expect.any(Object),
            type: expect.any(Object),
            required: expect.any(Object),
            multiple: expect.any(Object),
            defaultValue: expect.any(Object),
            minNumber: expect.any(Object),
            maxNumber: expect.any(Object),
            minLength: expect.any(Object),
            maxLength: expect.any(Object),
            regex: expect.any(Object),
            order: expect.any(Object),
            attributeGroup: expect.any(Object),
            active: expect.any(Object),
            setTypes: expect.any(Object),
            partTypes: expect.any(Object),
          }),
        );
      });

      it('passing IMegaAttribute should create a new form with FormGroup', () => {
        const formGroup = service.createMegaAttributeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            label: expect.any(Object),
            description: expect.any(Object),
            uiComponent: expect.any(Object),
            type: expect.any(Object),
            required: expect.any(Object),
            multiple: expect.any(Object),
            defaultValue: expect.any(Object),
            minNumber: expect.any(Object),
            maxNumber: expect.any(Object),
            minLength: expect.any(Object),
            maxLength: expect.any(Object),
            regex: expect.any(Object),
            order: expect.any(Object),
            attributeGroup: expect.any(Object),
            active: expect.any(Object),
            setTypes: expect.any(Object),
            partTypes: expect.any(Object),
          }),
        );
      });
    });

    describe('getMegaAttribute', () => {
      it('should return NewMegaAttribute for default MegaAttribute initial value', () => {
        const formGroup = service.createMegaAttributeFormGroup(sampleWithNewData);

        const megaAttribute = service.getMegaAttribute(formGroup);

        expect(megaAttribute).toMatchObject(sampleWithNewData);
      });

      it('should return NewMegaAttribute for empty MegaAttribute initial value', () => {
        const formGroup = service.createMegaAttributeFormGroup();

        const megaAttribute = service.getMegaAttribute(formGroup);

        expect(megaAttribute).toMatchObject({});
      });

      it('should return IMegaAttribute', () => {
        const formGroup = service.createMegaAttributeFormGroup(sampleWithRequiredData);

        const megaAttribute = service.getMegaAttribute(formGroup);

        expect(megaAttribute).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMegaAttribute should not enable id FormControl', () => {
        const formGroup = service.createMegaAttributeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMegaAttribute should disable id FormControl', () => {
        const formGroup = service.createMegaAttributeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
