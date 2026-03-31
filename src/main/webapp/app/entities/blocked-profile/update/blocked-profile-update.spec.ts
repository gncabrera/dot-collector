import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IBlockedProfile } from '../blocked-profile.model';
import { BlockedProfileService } from '../service/blocked-profile.service';

import { BlockedProfileFormService } from './blocked-profile-form.service';
import { BlockedProfileUpdate } from './blocked-profile-update';

describe('BlockedProfile Management Update Component', () => {
  let comp: BlockedProfileUpdate;
  let fixture: ComponentFixture<BlockedProfileUpdate>;
  let activatedRoute: ActivatedRoute;
  let blockedProfileFormService: BlockedProfileFormService;
  let blockedProfileService: BlockedProfileService;
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

    fixture = TestBed.createComponent(BlockedProfileUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    blockedProfileFormService = TestBed.inject(BlockedProfileFormService);
    blockedProfileService = TestBed.inject(BlockedProfileService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Profile query and add missing value', () => {
      const blockedProfile: IBlockedProfile = { id: 9787 };
      const profile: IProfile = { id: 32255 };
      blockedProfile.profile = profile;
      const blockedProfile: IProfile = { id: 32255 };
      blockedProfile.blockedProfile = blockedProfile;

      const profileCollection: IProfile[] = [{ id: 32255 }];
      vitest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile, blockedProfile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      vitest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ blockedProfile });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const blockedProfile: IBlockedProfile = { id: 9787 };
      const profile: IProfile = { id: 32255 };
      blockedProfile.profile = profile;
      const blockedProfile: IProfile = { id: 32255 };
      blockedProfile.blockedProfile = blockedProfile;

      activatedRoute.data = of({ blockedProfile });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection()).toContainEqual(profile);
      expect(comp.profilesSharedCollection()).toContainEqual(blockedProfile);
      expect(comp.blockedProfile).toEqual(blockedProfile);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBlockedProfile>();
      const blockedProfile = { id: 20306 };
      vitest.spyOn(blockedProfileFormService, 'getBlockedProfile').mockReturnValue(blockedProfile);
      vitest.spyOn(blockedProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ blockedProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(blockedProfile);
      saveSubject.complete();

      // THEN
      expect(blockedProfileFormService.getBlockedProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(blockedProfileService.update).toHaveBeenCalledWith(expect.objectContaining(blockedProfile));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IBlockedProfile>();
      const blockedProfile = { id: 20306 };
      vitest.spyOn(blockedProfileFormService, 'getBlockedProfile').mockReturnValue({ id: null });
      vitest.spyOn(blockedProfileService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ blockedProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(blockedProfile);
      saveSubject.complete();

      // THEN
      expect(blockedProfileFormService.getBlockedProfile).toHaveBeenCalled();
      expect(blockedProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IBlockedProfile>();
      const blockedProfile = { id: 20306 };
      vitest.spyOn(blockedProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ blockedProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(blockedProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
