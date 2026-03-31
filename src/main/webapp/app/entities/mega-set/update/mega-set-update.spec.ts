import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaSetType } from 'app/entities/mega-set-type/mega-set-type.model';
import { MegaSetTypeService } from 'app/entities/mega-set-type/service/mega-set-type.service';
import { IProfileCollectionSet } from 'app/entities/profile-collection-set/profile-collection-set.model';
import { ProfileCollectionSetService } from 'app/entities/profile-collection-set/service/profile-collection-set.service';
import { IMegaSet } from '../mega-set.model';
import { MegaSetService } from '../service/mega-set.service';

import { MegaSetFormService } from './mega-set-form.service';
import { MegaSetUpdate } from './mega-set-update';

describe('MegaSet Management Update Component', () => {
  let comp: MegaSetUpdate;
  let fixture: ComponentFixture<MegaSetUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaSetFormService: MegaSetFormService;
  let megaSetService: MegaSetService;
  let megaSetTypeService: MegaSetTypeService;
  let profileCollectionSetService: ProfileCollectionSetService;

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

    fixture = TestBed.createComponent(MegaSetUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaSetFormService = TestBed.inject(MegaSetFormService);
    megaSetService = TestBed.inject(MegaSetService);
    megaSetTypeService = TestBed.inject(MegaSetTypeService);
    profileCollectionSetService = TestBed.inject(ProfileCollectionSetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaSetType query and add missing value', () => {
      const megaSet: IMegaSet = { id: 10614 };
      const type: IMegaSetType = { id: 18052 };
      megaSet.type = type;

      const megaSetTypeCollection: IMegaSetType[] = [{ id: 18052 }];
      vitest.spyOn(megaSetTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaSetTypeCollection })));
      const additionalMegaSetTypes = [type];
      const expectedCollection: IMegaSetType[] = [...additionalMegaSetTypes, ...megaSetTypeCollection];
      vitest.spyOn(megaSetTypeService, 'addMegaSetTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaSet });
      comp.ngOnInit();

      expect(megaSetTypeService.query).toHaveBeenCalled();
      expect(megaSetTypeService.addMegaSetTypeToCollectionIfMissing).toHaveBeenCalledWith(
        megaSetTypeCollection,
        ...additionalMegaSetTypes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaSetTypesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call ProfileCollectionSet query and add missing value', () => {
      const megaSet: IMegaSet = { id: 10614 };
      const profileCollectionSets: IProfileCollectionSet[] = [{ id: 5128 }];
      megaSet.profileCollectionSets = profileCollectionSets;

      const profileCollectionSetCollection: IProfileCollectionSet[] = [{ id: 5128 }];
      vitest.spyOn(profileCollectionSetService, 'query').mockReturnValue(of(new HttpResponse({ body: profileCollectionSetCollection })));
      const additionalProfileCollectionSets = [...profileCollectionSets];
      const expectedCollection: IProfileCollectionSet[] = [...additionalProfileCollectionSets, ...profileCollectionSetCollection];
      vitest.spyOn(profileCollectionSetService, 'addProfileCollectionSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaSet });
      comp.ngOnInit();

      expect(profileCollectionSetService.query).toHaveBeenCalled();
      expect(profileCollectionSetService.addProfileCollectionSetToCollectionIfMissing).toHaveBeenCalledWith(
        profileCollectionSetCollection,
        ...additionalProfileCollectionSets.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.profileCollectionSetsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaSet: IMegaSet = { id: 10614 };
      const type: IMegaSetType = { id: 18052 };
      megaSet.type = type;
      const profileCollectionSet: IProfileCollectionSet = { id: 5128 };
      megaSet.profileCollectionSets = [profileCollectionSet];

      activatedRoute.data = of({ megaSet });
      comp.ngOnInit();

      expect(comp.megaSetTypesSharedCollection()).toContainEqual(type);
      expect(comp.profileCollectionSetsSharedCollection()).toContainEqual(profileCollectionSet);
      expect(comp.megaSet).toEqual(megaSet);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSet>();
      const megaSet = { id: 26766 };
      vitest.spyOn(megaSetFormService, 'getMegaSet').mockReturnValue(megaSet);
      vitest.spyOn(megaSetService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaSet);
      saveSubject.complete();

      // THEN
      expect(megaSetFormService.getMegaSet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaSetService.update).toHaveBeenCalledWith(expect.objectContaining(megaSet));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSet>();
      const megaSet = { id: 26766 };
      vitest.spyOn(megaSetFormService, 'getMegaSet').mockReturnValue({ id: null });
      vitest.spyOn(megaSetService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaSet);
      saveSubject.complete();

      // THEN
      expect(megaSetFormService.getMegaSet).toHaveBeenCalled();
      expect(megaSetService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSet>();
      const megaSet = { id: 26766 };
      vitest.spyOn(megaSetService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaSetService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMegaSetType', () => {
      it('should forward to megaSetTypeService', () => {
        const entity = { id: 18052 };
        const entity2 = { id: 12521 };
        vitest.spyOn(megaSetTypeService, 'compareMegaSetType');
        comp.compareMegaSetType(entity, entity2);
        expect(megaSetTypeService.compareMegaSetType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProfileCollectionSet', () => {
      it('should forward to profileCollectionSetService', () => {
        const entity = { id: 5128 };
        const entity2 = { id: 633 };
        vitest.spyOn(profileCollectionSetService, 'compareProfileCollectionSet');
        comp.compareProfileCollectionSet(entity, entity2);
        expect(profileCollectionSetService.compareProfileCollectionSet).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
