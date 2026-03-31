import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IFollowingProfile } from '../following-profile.model';
import { FollowingProfileService } from '../service/following-profile.service';

import { FollowingProfileFormService } from './following-profile-form.service';
import { FollowingProfileUpdate } from './following-profile-update';

describe('FollowingProfile Management Update Component', () => {
  let comp: FollowingProfileUpdate;
  let fixture: ComponentFixture<FollowingProfileUpdate>;
  let activatedRoute: ActivatedRoute;
  let followingProfileFormService: FollowingProfileFormService;
  let followingProfileService: FollowingProfileService;
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

    fixture = TestBed.createComponent(FollowingProfileUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    followingProfileFormService = TestBed.inject(FollowingProfileFormService);
    followingProfileService = TestBed.inject(FollowingProfileService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Profile query and add missing value', () => {
      const followingProfile: IFollowingProfile = { id: 26929 };
      const profile: IProfile = { id: 32255 };
      followingProfile.profile = profile;
      const followedProfile: IProfile = { id: 32255 };
      followingProfile.followedProfile = followedProfile;

      const profileCollection: IProfile[] = [{ id: 32255 }];
      vitest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile, followedProfile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      vitest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ followingProfile });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const followingProfile: IFollowingProfile = { id: 26929 };
      const profile: IProfile = { id: 32255 };
      followingProfile.profile = profile;
      const followedProfile: IProfile = { id: 32255 };
      followingProfile.followedProfile = followedProfile;

      activatedRoute.data = of({ followingProfile });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection()).toContainEqual(profile);
      expect(comp.profilesSharedCollection()).toContainEqual(followedProfile);
      expect(comp.followingProfile).toEqual(followingProfile);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFollowingProfile>();
      const followingProfile = { id: 29154 };
      vitest.spyOn(followingProfileFormService, 'getFollowingProfile').mockReturnValue(followingProfile);
      vitest.spyOn(followingProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ followingProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(followingProfile);
      saveSubject.complete();

      // THEN
      expect(followingProfileFormService.getFollowingProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(followingProfileService.update).toHaveBeenCalledWith(expect.objectContaining(followingProfile));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IFollowingProfile>();
      const followingProfile = { id: 29154 };
      vitest.spyOn(followingProfileFormService, 'getFollowingProfile').mockReturnValue({ id: null });
      vitest.spyOn(followingProfileService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ followingProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(followingProfile);
      saveSubject.complete();

      // THEN
      expect(followingProfileFormService.getFollowingProfile).toHaveBeenCalled();
      expect(followingProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IFollowingProfile>();
      const followingProfile = { id: 29154 };
      vitest.spyOn(followingProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ followingProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(followingProfileService.update).toHaveBeenCalled();
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
