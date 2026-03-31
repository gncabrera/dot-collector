import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../environment-variable.test-samples';

import { EnvironmentVariableFormService } from './environment-variable-form.service';

describe('EnvironmentVariable Form Service', () => {
  let service: EnvironmentVariableFormService;

  beforeEach(() => {
    service = TestBed.inject(EnvironmentVariableFormService);
  });

  describe('Service methods', () => {
    describe('createEnvironmentVariableFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEnvironmentVariableFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            key: expect.any(Object),
            value: expect.any(Object),
            description: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });

      it('passing IEnvironmentVariable should create a new form with FormGroup', () => {
        const formGroup = service.createEnvironmentVariableFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            key: expect.any(Object),
            value: expect.any(Object),
            description: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });
    });

    describe('getEnvironmentVariable', () => {
      it('should return NewEnvironmentVariable for default EnvironmentVariable initial value', () => {
        const formGroup = service.createEnvironmentVariableFormGroup(sampleWithNewData);

        const environmentVariable = service.getEnvironmentVariable(formGroup);

        expect(environmentVariable).toMatchObject(sampleWithNewData);
      });

      it('should return NewEnvironmentVariable for empty EnvironmentVariable initial value', () => {
        const formGroup = service.createEnvironmentVariableFormGroup();

        const environmentVariable = service.getEnvironmentVariable(formGroup);

        expect(environmentVariable).toMatchObject({});
      });

      it('should return IEnvironmentVariable', () => {
        const formGroup = service.createEnvironmentVariableFormGroup(sampleWithRequiredData);

        const environmentVariable = service.getEnvironmentVariable(formGroup);

        expect(environmentVariable).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEnvironmentVariable should not enable id FormControl', () => {
        const formGroup = service.createEnvironmentVariableFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEnvironmentVariable should disable id FormControl', () => {
        const formGroup = service.createEnvironmentVariableFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
