import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaPartType } from 'app/entities/mega-part-type/mega-part-type.model';
import { MegaPartTypeService } from 'app/entities/mega-part-type/service/mega-part-type.service';
import { IPartCategory } from 'app/entities/part-category/part-category.model';
import { PartCategoryService } from 'app/entities/part-category/service/part-category.service';
import { IPartSubCategory } from 'app/entities/part-sub-category/part-sub-category.model';
import { PartSubCategoryService } from 'app/entities/part-sub-category/service/part-sub-category.service';
import { IMegaPart } from '../mega-part.model';
import { MegaPartService } from '../service/mega-part.service';

import { MegaPartFormService } from './mega-part-form.service';
import { MegaPartUpdate } from './mega-part-update';

describe('MegaPart Management Update Component', () => {
  let comp: MegaPartUpdate;
  let fixture: ComponentFixture<MegaPartUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaPartFormService: MegaPartFormService;
  let megaPartService: MegaPartService;
  let megaPartTypeService: MegaPartTypeService;
  let partCategoryService: PartCategoryService;
  let partSubCategoryService: PartSubCategoryService;

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

    fixture = TestBed.createComponent(MegaPartUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaPartFormService = TestBed.inject(MegaPartFormService);
    megaPartService = TestBed.inject(MegaPartService);
    megaPartTypeService = TestBed.inject(MegaPartTypeService);
    partCategoryService = TestBed.inject(PartCategoryService);
    partSubCategoryService = TestBed.inject(PartSubCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaPartType query and add missing value', () => {
      const megaPart: IMegaPart = { id: 5536 };
      const type: IMegaPartType = { id: 8406 };
      megaPart.type = type;

      const megaPartTypeCollection: IMegaPartType[] = [{ id: 8406 }];
      vitest.spyOn(megaPartTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaPartTypeCollection })));
      const additionalMegaPartTypes = [type];
      const expectedCollection: IMegaPartType[] = [...additionalMegaPartTypes, ...megaPartTypeCollection];
      vitest.spyOn(megaPartTypeService, 'addMegaPartTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaPart });
      comp.ngOnInit();

      expect(megaPartTypeService.query).toHaveBeenCalled();
      expect(megaPartTypeService.addMegaPartTypeToCollectionIfMissing).toHaveBeenCalledWith(
        megaPartTypeCollection,
        ...additionalMegaPartTypes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaPartTypesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call PartCategory query and add missing value', () => {
      const megaPart: IMegaPart = { id: 5536 };
      const partCategory: IPartCategory = { id: 10447 };
      megaPart.partCategory = partCategory;

      const partCategoryCollection: IPartCategory[] = [{ id: 10447 }];
      vitest.spyOn(partCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: partCategoryCollection })));
      const additionalPartCategories = [partCategory];
      const expectedCollection: IPartCategory[] = [...additionalPartCategories, ...partCategoryCollection];
      vitest.spyOn(partCategoryService, 'addPartCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaPart });
      comp.ngOnInit();

      expect(partCategoryService.query).toHaveBeenCalled();
      expect(partCategoryService.addPartCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        partCategoryCollection,
        ...additionalPartCategories.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.partCategoriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call PartSubCategory query and add missing value', () => {
      const megaPart: IMegaPart = { id: 5536 };
      const partSubCategories: IPartSubCategory[] = [{ id: 12026 }];
      megaPart.partSubCategories = partSubCategories;

      const partSubCategoryCollection: IPartSubCategory[] = [{ id: 12026 }];
      vitest.spyOn(partSubCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: partSubCategoryCollection })));
      const additionalPartSubCategories = [...partSubCategories];
      const expectedCollection: IPartSubCategory[] = [...additionalPartSubCategories, ...partSubCategoryCollection];
      vitest.spyOn(partSubCategoryService, 'addPartSubCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaPart });
      comp.ngOnInit();

      expect(partSubCategoryService.query).toHaveBeenCalled();
      expect(partSubCategoryService.addPartSubCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        partSubCategoryCollection,
        ...additionalPartSubCategories.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.partSubCategoriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaPart: IMegaPart = { id: 5536 };
      const type: IMegaPartType = { id: 8406 };
      megaPart.type = type;
      const partCategory: IPartCategory = { id: 10447 };
      megaPart.partCategory = partCategory;
      const partSubCategory: IPartSubCategory = { id: 12026 };
      megaPart.partSubCategories = [partSubCategory];

      activatedRoute.data = of({ megaPart });
      comp.ngOnInit();

      expect(comp.megaPartTypesSharedCollection()).toContainEqual(type);
      expect(comp.partCategoriesSharedCollection()).toContainEqual(partCategory);
      expect(comp.partSubCategoriesSharedCollection()).toContainEqual(partSubCategory);
      expect(comp.megaPart).toEqual(megaPart);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPart>();
      const megaPart = { id: 25333 };
      vitest.spyOn(megaPartFormService, 'getMegaPart').mockReturnValue(megaPart);
      vitest.spyOn(megaPartService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPart });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaPart);
      saveSubject.complete();

      // THEN
      expect(megaPartFormService.getMegaPart).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaPartService.update).toHaveBeenCalledWith(expect.objectContaining(megaPart));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPart>();
      const megaPart = { id: 25333 };
      vitest.spyOn(megaPartFormService, 'getMegaPart').mockReturnValue({ id: null });
      vitest.spyOn(megaPartService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPart: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaPart);
      saveSubject.complete();

      // THEN
      expect(megaPartFormService.getMegaPart).toHaveBeenCalled();
      expect(megaPartService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPart>();
      const megaPart = { id: 25333 };
      vitest.spyOn(megaPartService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPart });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaPartService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMegaPartType', () => {
      it('should forward to megaPartTypeService', () => {
        const entity = { id: 8406 };
        const entity2 = { id: 11390 };
        vitest.spyOn(megaPartTypeService, 'compareMegaPartType');
        comp.compareMegaPartType(entity, entity2);
        expect(megaPartTypeService.compareMegaPartType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePartCategory', () => {
      it('should forward to partCategoryService', () => {
        const entity = { id: 10447 };
        const entity2 = { id: 13269 };
        vitest.spyOn(partCategoryService, 'comparePartCategory');
        comp.comparePartCategory(entity, entity2);
        expect(partCategoryService.comparePartCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePartSubCategory', () => {
      it('should forward to partSubCategoryService', () => {
        const entity = { id: 12026 };
        const entity2 = { id: 2351 };
        vitest.spyOn(partSubCategoryService, 'comparePartSubCategory');
        comp.comparePartSubCategory(entity, entity2);
        expect(partSubCategoryService.comparePartSubCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
