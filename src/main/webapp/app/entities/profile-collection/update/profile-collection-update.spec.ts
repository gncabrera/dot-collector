import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IProfileCollection } from '../profile-collection.model';
import { ProfileCollectionService } from '../service/profile-collection.service';

import { ProfileCollectionFormService } from './profile-collection-form.service';
import { ProfileCollectionUpdate } from './profile-collection-update';

describe('ProfileCollection Management Update Component', () => {
  let comp: ProfileCollectionUpdate;
  let fixture: ComponentFixture<ProfileCollectionUpdate>;
  let activatedRoute: ActivatedRoute;
  let profileCollectionFormService: ProfileCollectionFormService;
  let profileCollectionService: ProfileCollectionService;
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

    fixture = TestBed.createComponent(ProfileCollectionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileCollectionFormService = TestBed.inject(ProfileCollectionFormService);
    profileCollectionService = TestBed.inject(ProfileCollectionService);
    profileService = TestBed.inject(ProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Profile query and add missing value', () => {
      const profileCollection: IProfileCollection = { id: 25147 };
      const profile: IProfile = { id: 32255 };
      profileCollection.profile = profile;

      const profileCollection: IProfile[] = [{ id: 32255 }];
      vitest.spyOn(profileService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollection })));
      const additionalProfiles = [profile];
      const expectedCollection: IProfile[] = [...additionalProfiles, ...profileCollection];
      vitest.spyOn(profileService, 'addProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profileCollection });
      comp.ngOnInit();

      expect(profileService.query).toHaveBeenCalled();
      expect(profileService.addProfileToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollection,
        ...additionalProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const profileCollection: IProfileCollection = { id: 25147 };
      const profile: IProfile = { id: 32255 };
      profileCollection.profile = profile;

      activatedRoute.data = of({ profileCollection });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection()).toContainEqual(profile);
      expect(comp.profileCollection).toEqual(profileCollection);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileCollection>();
      const profileCollection = { id: 29855 };
      vitest.spyOn(profileCollectionFormService, 'getProfileCollection').mockReturnValue(profileCollection);
      vitest.spyOn(profileCollectionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileCollection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileCollection);
      saveSubject.complete();

      // THEN
      expect(profileCollectionFormService.getProfileCollection).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileCollectionService.update).toHaveBeenCalledWith(expect.objectContaining(profileCollection));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileCollection>();
      const profileCollection = { id: 29855 };
      vitest.spyOn(profileCollectionFormService, 'getProfileCollection').mockReturnValue({ id: null });
      vitest.spyOn(profileCollectionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileCollection: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileCollection);
      saveSubject.complete();

      // THEN
      expect(profileCollectionFormService.getProfileCollection).toHaveBeenCalled();
      expect(profileCollectionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileCollection>();
      const profileCollection = { id: 29855 };
      vitest.spyOn(profileCollectionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileCollection });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileCollectionService.update).toHaveBeenCalled();
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
