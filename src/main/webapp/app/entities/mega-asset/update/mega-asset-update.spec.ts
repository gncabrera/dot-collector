import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { MegaPartService } from 'app/entities/mega-part/service/mega-part.service';
import { IMegaSet } from 'app/entities/mega-set/mega-set.model';
import { MegaSetService } from 'app/entities/mega-set/service/mega-set.service';
import { IMegaAsset } from '../mega-asset.model';
import { MegaAssetService } from '../service/mega-asset.service';

import { MegaAssetFormService } from './mega-asset-form.service';
import { MegaAssetUpdate } from './mega-asset-update';

describe('MegaAsset Management Update Component', () => {
  let comp: MegaAssetUpdate;
  let fixture: ComponentFixture<MegaAssetUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaAssetFormService: MegaAssetFormService;
  let megaAssetService: MegaAssetService;
  let megaSetService: MegaSetService;
  let megaPartService: MegaPartService;

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

    fixture = TestBed.createComponent(MegaAssetUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaAssetFormService = TestBed.inject(MegaAssetFormService);
    megaAssetService = TestBed.inject(MegaAssetService);
    megaSetService = TestBed.inject(MegaSetService);
    megaPartService = TestBed.inject(MegaPartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaSet query and add missing value', () => {
      const megaAsset: IMegaAsset = { id: 13538 };
      const set: IMegaSet = { id: 26766 };
      megaAsset.set = set;

      const megaSetCollection: IMegaSet[] = [{ id: 26766 }];
      vitest.spyOn(megaSetService, 'query').mockReturnValue(of(new HttpResponse({ body: megaSetCollection })));
      const additionalMegaSets = [set];
      const expectedCollection: IMegaSet[] = [...additionalMegaSets, ...megaSetCollection];
      vitest.spyOn(megaSetService, 'addMegaSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaAsset });
      comp.ngOnInit();

      expect(megaSetService.query).toHaveBeenCalled();
      expect(megaSetService.addMegaSetToCollectionIfMissing).toHaveBeenCalledWith(
        megaSetCollection,
        ...additionalMegaSets.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaSetsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call MegaPart query and add missing value', () => {
      const megaAsset: IMegaAsset = { id: 13538 };
      const part: IMegaPart = { id: 25333 };
      megaAsset.part = part;

      const megaPartCollection: IMegaPart[] = [{ id: 25333 }];
      vitest.spyOn(megaPartService, 'query').mockReturnValue(of(new HttpResponse({ body: megaPartCollection })));
      const additionalMegaParts = [part];
      const expectedCollection: IMegaPart[] = [...additionalMegaParts, ...megaPartCollection];
      vitest.spyOn(megaPartService, 'addMegaPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaAsset });
      comp.ngOnInit();

      expect(megaPartService.query).toHaveBeenCalled();
      expect(megaPartService.addMegaPartToCollectionIfMissing).toHaveBeenCalledWith(
        megaPartCollection,
        ...additionalMegaParts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaPartsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaAsset: IMegaAsset = { id: 13538 };
      const set: IMegaSet = { id: 26766 };
      megaAsset.set = set;
      const part: IMegaPart = { id: 25333 };
      megaAsset.part = part;

      activatedRoute.data = of({ megaAsset });
      comp.ngOnInit();

      expect(comp.megaSetsSharedCollection()).toContainEqual(set);
      expect(comp.megaPartsSharedCollection()).toContainEqual(part);
      expect(comp.megaAsset).toEqual(megaAsset);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAsset>();
      const megaAsset = { id: 9989 };
      vitest.spyOn(megaAssetFormService, 'getMegaAsset').mockReturnValue(megaAsset);
      vitest.spyOn(megaAssetService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAsset });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaAsset);
      saveSubject.complete();

      // THEN
      expect(megaAssetFormService.getMegaAsset).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaAssetService.update).toHaveBeenCalledWith(expect.objectContaining(megaAsset));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAsset>();
      const megaAsset = { id: 9989 };
      vitest.spyOn(megaAssetFormService, 'getMegaAsset').mockReturnValue({ id: null });
      vitest.spyOn(megaAssetService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAsset: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaAsset);
      saveSubject.complete();

      // THEN
      expect(megaAssetFormService.getMegaAsset).toHaveBeenCalled();
      expect(megaAssetService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAsset>();
      const megaAsset = { id: 9989 };
      vitest.spyOn(megaAssetService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAsset });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaAssetService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMegaSet', () => {
      it('should forward to megaSetService', () => {
        const entity = { id: 26766 };
        const entity2 = { id: 10614 };
        vitest.spyOn(megaSetService, 'compareMegaSet');
        comp.compareMegaSet(entity, entity2);
        expect(megaSetService.compareMegaSet).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareMegaPart', () => {
      it('should forward to megaPartService', () => {
        const entity = { id: 25333 };
        const entity2 = { id: 5536 };
        vitest.spyOn(megaPartService, 'compareMegaPart');
        comp.compareMegaPart(entity, entity2);
        expect(megaPartService.compareMegaPart).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
