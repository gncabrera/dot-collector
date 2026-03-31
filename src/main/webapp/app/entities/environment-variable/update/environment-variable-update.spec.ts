import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IEnvironmentVariable } from '../environment-variable.model';
import { EnvironmentVariableService } from '../service/environment-variable.service';

import { EnvironmentVariableFormService } from './environment-variable-form.service';
import { EnvironmentVariableUpdate } from './environment-variable-update';

describe('EnvironmentVariable Management Update Component', () => {
  let comp: EnvironmentVariableUpdate;
  let fixture: ComponentFixture<EnvironmentVariableUpdate>;
  let activatedRoute: ActivatedRoute;
  let environmentVariableFormService: EnvironmentVariableFormService;
  let environmentVariableService: EnvironmentVariableService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(EnvironmentVariableUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    environmentVariableFormService = TestBed.inject(EnvironmentVariableFormService);
    environmentVariableService = TestBed.inject(EnvironmentVariableService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const environmentVariable: IEnvironmentVariable = { id: 8466 };

      activatedRoute.data = of({ environmentVariable });
      comp.ngOnInit();

      expect(comp.environmentVariable).toEqual(environmentVariable);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEnvironmentVariable>();
      const environmentVariable = { id: 22355 };
      vitest.spyOn(environmentVariableFormService, 'getEnvironmentVariable').mockReturnValue(environmentVariable);
      vitest.spyOn(environmentVariableService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ environmentVariable });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(environmentVariable);
      saveSubject.complete();

      // THEN
      expect(environmentVariableFormService.getEnvironmentVariable).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(environmentVariableService.update).toHaveBeenCalledWith(expect.objectContaining(environmentVariable));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IEnvironmentVariable>();
      const environmentVariable = { id: 22355 };
      vitest.spyOn(environmentVariableFormService, 'getEnvironmentVariable').mockReturnValue({ id: null });
      vitest.spyOn(environmentVariableService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ environmentVariable: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(environmentVariable);
      saveSubject.complete();

      // THEN
      expect(environmentVariableFormService.getEnvironmentVariable).toHaveBeenCalled();
      expect(environmentVariableService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IEnvironmentVariable>();
      const environmentVariable = { id: 22355 };
      vitest.spyOn(environmentVariableService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ environmentVariable });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(environmentVariableService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
