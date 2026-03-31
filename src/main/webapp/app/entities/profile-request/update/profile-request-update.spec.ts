import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IProfileRequestType } from 'app/entities/profile-request-type/profile-request-type.model';
import { ProfileRequestTypeService } from 'app/entities/profile-request-type/service/profile-request-type.service';
import { IProfileRequest } from '../profile-request.model';
import { ProfileRequestService } from '../service/profile-request.service';

import { ProfileRequestFormService } from './profile-request-form.service';
import { ProfileRequestUpdate } from './profile-request-update';

describe('ProfileRequest Management Update Component', () => {
  let comp: ProfileRequestUpdate;
  let fixture: ComponentFixture<ProfileRequestUpdate>;
  let activatedRoute: ActivatedRoute;
  let profileRequestFormService: ProfileRequestFormService;
  let profileRequestService: ProfileRequestService;
  let profileRequestTypeService: ProfileRequestTypeService;
  let profileService: ProfileService;

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

    fixture = TestBed.createComponent(ProfileRequestUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileRequestFormService = TestBed.inject(ProfileRequestFormService);
    profileRequestService = TestBed.inject(ProfileRequestService);
    profileRequestTypeService = TestBed.inject(ProfileRequestTypeService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ProfileRequestType query and add missing value', () => {
      const profileRequest: IProfileRequest = { id: 11236 };
      const type: IProfileRequestType = { id: 27058 };
      profileRequest.type = type;

      const profileRequestTypeCollection: IProfileRequestType[] = [{ id: 27058 }];
      vitest.spyOn(profileRequestTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: profileRequestTypeCollection })));
      const additionalProfileRequestTypes = [type];
      const expectedCollection: IProfileRequestType[] = [...additionalProfileRequestTypes, ...profileRequestTypeCollection];
      vitest.spyOn(profileRequestTypeService, 'addProfileRequestTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profileRequest });
      comp.ngOnInit();

      expect(profileRequestTypeService.query).toHaveBeenCalled();
      expect(profileRequestTypeService.addProfileRequestTypeToCollectionIfMissing).toHaveBeenCalledWith(
        profileRequestTypeCollection,
        ...additionalProfileRequestTypes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profileRequestTypesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Profile query and add missing value', () => {
      const profileRequest: IProfileRequest = { id: 11236 };
      const profile: IProfile = { id: 32255 };
      profileRequest.profile = profile;

      const profileCollection: IProfile[] = [{ id: 32255 }];
      vitest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      vitest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profileRequest });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const profileRequest: IProfileRequest = { id: 11236 };
      const type: IProfileRequestType = { id: 27058 };
      profileRequest.type = type;
      const profile: IProfile = { id: 32255 };
      profileRequest.profile = profile;

      activatedRoute.data = of({ profileRequest });
      comp.ngOnInit();

      expect(comp.profileRequestTypesSharedCollection()).toContainEqual(type);
      expect(comp.profilesSharedCollection()).toContainEqual(profile);
      expect(comp.profileRequest).toEqual(profileRequest);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileRequest>();
      const profileRequest = { id: 10331 };
      vitest.spyOn(profileRequestFormService, 'getProfileRequest').mockReturnValue(profileRequest);
      vitest.spyOn(profileRequestService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileRequest);
      saveSubject.complete();

      // THEN
      expect(profileRequestFormService.getProfileRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileRequestService.update).toHaveBeenCalledWith(expect.objectContaining(profileRequest));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileRequest>();
      const profileRequest = { id: 10331 };
      vitest.spyOn(profileRequestFormService, 'getProfileRequest').mockReturnValue({ id: null });
      vitest.spyOn(profileRequestService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileRequest: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileRequest);
      saveSubject.complete();

      // THEN
      expect(profileRequestFormService.getProfileRequest).toHaveBeenCalled();
      expect(profileRequestService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileRequest>();
      const profileRequest = { id: 10331 };
      vitest.spyOn(profileRequestService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileRequest });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileRequestService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProfileRequestType', () => {
      it('should forward to profileRequestTypeService', () => {
        const entity = { id: 27058 };
        const entity2 = { id: 13100 };
        vitest.spyOn(profileRequestTypeService, 'compareProfileRequestType');
        comp.compareProfileRequestType(entity, entity2);
        expect(profileRequestTypeService.compareProfileRequestType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProfile', () => {
      it('should forward to profileService', () => {
        const entity = { id: 32255 };
        const entity2 = { id: 13324 };
        vitest.spyOn(profileService, 'compareProfile');
        comp.compareProfile(entity, entity2);
        expect(profileService.compareProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
