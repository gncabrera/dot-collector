import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { MegaPartService } from 'app/entities/mega-part/service/mega-part.service';
import { IPartSubCategory } from '../part-sub-category.model';
import { PartSubCategoryService } from '../service/part-sub-category.service';

import { PartSubCategoryFormService } from './part-sub-category-form.service';
import { PartSubCategoryUpdate } from './part-sub-category-update';

describe('PartSubCategory Management Update Component', () => {
  let comp: PartSubCategoryUpdate;
  let fixture: ComponentFixture<PartSubCategoryUpdate>;
  let activatedRoute: ActivatedRoute;
  let partSubCategoryFormService: PartSubCategoryFormService;
  let partSubCategoryService: PartSubCategoryService;
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

    fixture = TestBed.createComponent(PartSubCategoryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partSubCategoryFormService = TestBed.inject(PartSubCategoryFormService);
    partSubCategoryService = TestBed.inject(PartSubCategoryService);
    megaPartService = TestBed.inject(MegaPartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaPart query and add missing value', () => {
      const partSubCategory: IPartSubCategory = { id: 2351 };
      const megaParts: IMegaPart[] = [{ id: 25333 }];
      partSubCategory.megaParts = megaParts;

      const megaPartCollection: IMegaPart[] = [{ id: 25333 }];
      vitest.spyOn(megaPartService, 'query').mockReturnValue(of(new HttpResponse({ body: megaPartCollection })));
      const additionalMegaParts = [...megaParts];
      const expectedCollection: IMegaPart[] = [...additionalMegaParts, ...megaPartCollection];
      vitest.spyOn(megaPartService, 'addMegaPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ partSubCategory });
      comp.ngOnInit();

      expect(megaPartService.query).toHaveBeenCalled();
      expect(megaPartService.addMegaPartToCollectionIfMissing).toHaveBeenCalledWith(
        megaPartCollection,
        ...additionalMegaParts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaPartsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const partSubCategory: IPartSubCategory = { id: 2351 };
      const megaPart: IMegaPart = { id: 25333 };
      partSubCategory.megaParts = [megaPart];

      activatedRoute.data = of({ partSubCategory });
      comp.ngOnInit();

      expect(comp.megaPartsSharedCollection()).toContainEqual(megaPart);
      expect(comp.partSubCategory).toEqual(partSubCategory);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPartSubCategory>();
      const partSubCategory = { id: 12026 };
      vitest.spyOn(partSubCategoryFormService, 'getPartSubCategory').mockReturnValue(partSubCategory);
      vitest.spyOn(partSubCategoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partSubCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(partSubCategory);
      saveSubject.complete();

      // THEN
      expect(partSubCategoryFormService.getPartSubCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partSubCategoryService.update).toHaveBeenCalledWith(expect.objectContaining(partSubCategory));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPartSubCategory>();
      const partSubCategory = { id: 12026 };
      vitest.spyOn(partSubCategoryFormService, 'getPartSubCategory').mockReturnValue({ id: null });
      vitest.spyOn(partSubCategoryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partSubCategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(partSubCategory);
      saveSubject.complete();

      // THEN
      expect(partSubCategoryFormService.getPartSubCategory).toHaveBeenCalled();
      expect(partSubCategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPartSubCategory>();
      const partSubCategory = { id: 12026 };
      vitest.spyOn(partSubCategoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partSubCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partSubCategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
