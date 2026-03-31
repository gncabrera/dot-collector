import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaSet } from 'app/entities/mega-set/mega-set.model';
import { MegaSetService } from 'app/entities/mega-set/service/mega-set.service';
import { IProfileCollection } from 'app/entities/profile-collection/profile-collection.model';
import { ProfileCollectionService } from 'app/entities/profile-collection/service/profile-collection.service';
import { IProfileCollectionSet } from '../profile-collection-set.model';
import { ProfileCollectionSetService } from '../service/profile-collection-set.service';

import { ProfileCollectionSetFormService } from './profile-collection-set-form.service';
import { ProfileCollectionSetUpdate } from './profile-collection-set-update';

describe('ProfileCollectionSet Management Update Component', () => {
  let comp: ProfileCollectionSetUpdate;
  let fixture: ComponentFixture<ProfileCollectionSetUpdate>;
  let activatedRoute: ActivatedRoute;
  let profileCollectionSetFormService: ProfileCollectionSetFormService;
  let profileCollectionSetService: ProfileCollectionSetService;
  let profileCollectionService: ProfileCollectionService;
  let megaSetService: MegaSetService;

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

    fixture = TestBed.createComponent(ProfileCollectionSetUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profileCollectionSetFormService = TestBed.inject(ProfileCollectionSetFormService);
    profileCollectionSetService = TestBed.inject(ProfileCollectionSetService);
    profileCollectionService = TestBed.inject(ProfileCollectionService);
    megaSetService = TestBed.inject(MegaSetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ProfileCollection query and add missing value', () => {
      const profileCollectionSet: IProfileCollectionSet = { id: 633 };
      const collection: IProfileCollection = { id: 29855 };
      profileCollectionSet.collection = collection;

      const profileCollectionCollection: IProfileCollection[] = [{ id: 29855 }];
      vitest.spyOn(profileCollectionService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollectionCollection })));
      const additionalProfileCollections = [collection];
      const expectedCollection: IProfileCollection[] = [...additionalProfileCollections, ...profileCollectionCollection];
      vitest.spyOn(profileCollectionService, 'addProfileCollectionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profileCollectionSet });
      comp.ngOnInit();

      expect(profileCollectionService.query).toHaveBeenCalled();
      expect(profileCollectionService.addProfileCollectionToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollectionCollection,
        ...additionalProfileCollections.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profileCollectionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call MegaSet query and add missing value', () => {
      const profileCollectionSet: IProfileCollectionSet = { id: 633 };
      const sets: IMegaSet[] = [{ id: 26766 }];
      profileCollectionSet.sets = sets;

      const megaSetCollection: IMegaSet[] = [{ id: 26766 }];
      vitest.spyOn(megaSetService, 'query').mockReturnValue(of(new HttpResponse({ body: megaSetCollection })));
      const additionalMegaSets = [...sets];
      const expectedCollection: IMegaSet[] = [...additionalMegaSets, ...megaSetCollection];
      vitest.spyOn(megaSetService, 'addMegaSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ profileCollectionSet });
      comp.ngOnInit();

      expect(megaSetService.query).toHaveBeenCalled();
      expect(megaSetService.addMegaSetToCollectionIfMissing).toHaveBeenCalledWith(
        megaSetCollection,
        ...additionalMegaSets.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaSetsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const profileCollectionSet: IProfileCollectionSet = { id: 633 };
      const collection: IProfileCollection = { id: 29855 };
      profileCollectionSet.collection = collection;
      const set: IMegaSet = { id: 26766 };
      profileCollectionSet.sets = [set];

      activatedRoute.data = of({ profileCollectionSet });
      comp.ngOnInit();

      expect(comp.profileCollectionsSharedCollection()).toContainEqual(collection);
      expect(comp.megaSetsSharedCollection()).toContainEqual(set);
      expect(comp.profileCollectionSet).toEqual(profileCollectionSet);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileCollectionSet>();
      const profileCollectionSet = { id: 5128 };
      vitest.spyOn(profileCollectionSetFormService, 'getProfileCollectionSet').mockReturnValue(profileCollectionSet);
      vitest.spyOn(profileCollectionSetService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileCollectionSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileCollectionSet);
      saveSubject.complete();

      // THEN
      expect(profileCollectionSetFormService.getProfileCollectionSet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profileCollectionSetService.update).toHaveBeenCalledWith(expect.objectContaining(profileCollectionSet));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileCollectionSet>();
      const profileCollectionSet = { id: 5128 };
      vitest.spyOn(profileCollectionSetFormService, 'getProfileCollectionSet').mockReturnValue({ id: null });
      vitest.spyOn(profileCollectionSetService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileCollectionSet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(profileCollectionSet);
      saveSubject.complete();

      // THEN
      expect(profileCollectionSetFormService.getProfileCollectionSet).toHaveBeenCalled();
      expect(profileCollectionSetService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProfileCollectionSet>();
      const profileCollectionSet = { id: 5128 };
      vitest.spyOn(profileCollectionSetService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profileCollectionSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profileCollectionSetService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProfileCollection', () => {
      it('should forward to profileCollectionService', () => {
        const entity = { id: 29855 };
        const entity2 = { id: 25147 };
        vitest.spyOn(profileCollectionService, 'compareProfileCollection');
        comp.compareProfileCollection(entity, entity2);
        expect(profileCollectionService.compareProfileCollection).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareMegaSet', () => {
      it('should forward to megaSetService', () => {
        const entity = { id: 26766 };
        const entity2 = { id: 10614 };
        vitest.spyOn(megaSetService, 'compareMegaSet');
        comp.compareMegaSet(entity, entity2);
        expect(megaSetService.compareMegaSet).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
