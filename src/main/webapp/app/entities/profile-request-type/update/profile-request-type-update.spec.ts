import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IProfileRequestType } from '../profile-request-type.model';
import { ProfileRequestTypeService } from '../service/profile-request-type.service';

import { ProfileRequestTypeFormService } from './profile-request-type-form.service';
import { ProfileRequestTypeUpdate } from './profile-request-type-update';

describe('ProfileRequestType Management Update Component', () => {
  let comp: ProfileRequestTypeUpdate;
  let fixture: ComponentFixture<ProfileRequestTypeUpdate>;
  let activatedRoute: ActivatedRoute;
  let profileRequestTypeFormService: ProfileRequestTypeFormService;
  let profileRequestTypeService: ProfileRequestTypeService;

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

    fixture = TestBed.createComponent(ProfileRequestTypeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileRequestTypeFormService = TestBed.inject(ProfileRequestTypeFormService);
    profileRequestTypeService = TestBed.inject(ProfileRequestTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const profileRequestType: IProfileRequestType = { id: 13100 };

      activatedRoute.data = of({ profileRequestType });
      comp.ngOnInit();

      expect(comp.profileRequestType).toEqual(profileRequestType);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileRequestType>();
      const profileRequestType = { id: 27058 };
      vitest.spyOn(profileRequestTypeFormService, 'getProfileRequestType').mockReturnValue(profileRequestType);
      vitest.spyOn(profileRequestTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileRequestType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileRequestType);
      saveSubject.complete();

      // THEN
      expect(profileRequestTypeFormService.getProfileRequestType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileRequestTypeService.update).toHaveBeenCalledWith(expect.objectContaining(profileRequestType));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileRequestType>();
      const profileRequestType = { id: 27058 };
      vitest.spyOn(profileRequestTypeFormService, 'getProfileRequestType').mockReturnValue({ id: null });
      vitest.spyOn(profileRequestTypeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileRequestType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileRequestType);
      saveSubject.complete();

      // THEN
      expect(profileRequestTypeFormService.getProfileRequestType).toHaveBeenCalled();
      expect(profileRequestTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileRequestType>();
      const profileRequestType = { id: 27058 };
      vitest.spyOn(profileRequestTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileRequestType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileRequestTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
