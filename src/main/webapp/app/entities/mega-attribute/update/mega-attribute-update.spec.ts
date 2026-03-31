import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaPartType } from 'app/entities/mega-part-type/mega-part-type.model';
import { MegaPartTypeService } from 'app/entities/mega-part-type/service/mega-part-type.service';
import { IMegaSetType } from 'app/entities/mega-set-type/mega-set-type.model';
import { MegaSetTypeService } from 'app/entities/mega-set-type/service/mega-set-type.service';
import { IMegaAttribute } from '../mega-attribute.model';
import { MegaAttributeService } from '../service/mega-attribute.service';

import { MegaAttributeFormService } from './mega-attribute-form.service';
import { MegaAttributeUpdate } from './mega-attribute-update';

describe('MegaAttribute Management Update Component', () => {
  let comp: MegaAttributeUpdate;
  let fixture: ComponentFixture<MegaAttributeUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaAttributeFormService: MegaAttributeFormService;
  let megaAttributeService: MegaAttributeService;
  let megaSetTypeService: MegaSetTypeService;
  let megaPartTypeService: MegaPartTypeService;

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

    fixture = TestBed.createComponent(MegaAttributeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaAttributeFormService = TestBed.inject(MegaAttributeFormService);
    megaAttributeService = TestBed.inject(MegaAttributeService);
    megaSetTypeService = TestBed.inject(MegaSetTypeService);
    megaPartTypeService = TestBed.inject(MegaPartTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaSetType query and add missing value', () => {
      const megaAttribute: IMegaAttribute = { id: 12871 };
      const setTypes: IMegaSetType[] = [{ id: 18052 }];
      megaAttribute.setTypes = setTypes;

      const megaSetTypeCollection: IMegaSetType[] = [{ id: 18052 }];
      vitest.spyOn(megaSetTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaSetTypeCollection })));
      const additionalMegaSetTypes = [...setTypes];
      const expectedCollection: IMegaSetType[] = [...additionalMegaSetTypes, ...megaSetTypeCollection];
      vitest.spyOn(megaSetTypeService, 'addMegaSetTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaAttribute });
      comp.ngOnInit();

      expect(megaSetTypeService.query).toHaveBeenCalled();
      expect(megaSetTypeService.addMegaSetTypeToCollectionIfMissing).toHaveBeenCalledWith(
        megaSetTypeCollection,
        ...additionalMegaSetTypes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaSetTypesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call MegaPartType query and add missing value', () => {
      const megaAttribute: IMegaAttribute = { id: 12871 };
      const partTypes: IMegaPartType[] = [{ id: 8406 }];
      megaAttribute.partTypes = partTypes;

      const megaPartTypeCollection: IMegaPartType[] = [{ id: 8406 }];
      vitest.spyOn(megaPartTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaPartTypeCollection })));
      const additionalMegaPartTypes = [...partTypes];
      const expectedCollection: IMegaPartType[] = [...additionalMegaPartTypes, ...megaPartTypeCollection];
      vitest.spyOn(megaPartTypeService, 'addMegaPartTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaAttribute });
      comp.ngOnInit();

      expect(megaPartTypeService.query).toHaveBeenCalled();
      expect(megaPartTypeService.addMegaPartTypeToCollectionIfMissing).toHaveBeenCalledWith(
        megaPartTypeCollection,
        ...additionalMegaPartTypes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaPartTypesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaAttribute: IMegaAttribute = { id: 12871 };
      const setType: IMegaSetType = { id: 18052 };
      megaAttribute.setTypes = [setType];
      const partType: IMegaPartType = { id: 8406 };
      megaAttribute.partTypes = [partType];

      activatedRoute.data = of({ megaAttribute });
      comp.ngOnInit();

      expect(comp.megaSetTypesSharedCollection()).toContainEqual(setType);
      expect(comp.megaPartTypesSharedCollection()).toContainEqual(partType);
      expect(comp.megaAttribute).toEqual(megaAttribute);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAttribute>();
      const megaAttribute = { id: 12325 };
      vitest.spyOn(megaAttributeFormService, 'getMegaAttribute').mockReturnValue(megaAttribute);
      vitest.spyOn(megaAttributeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaAttribute);
      saveSubject.complete();

      // THEN
      expect(megaAttributeFormService.getMegaAttribute).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaAttributeService.update).toHaveBeenCalledWith(expect.objectContaining(megaAttribute));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAttribute>();
      const megaAttribute = { id: 12325 };
      vitest.spyOn(megaAttributeFormService, 'getMegaAttribute').mockReturnValue({ id: null });
      vitest.spyOn(megaAttributeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAttribute: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaAttribute);
      saveSubject.complete();

      // THEN
      expect(megaAttributeFormService.getMegaAttribute).toHaveBeenCalled();
      expect(megaAttributeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAttribute>();
      const megaAttribute = { id: 12325 };
      vitest.spyOn(megaAttributeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAttribute });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaAttributeService.update).toHaveBeenCalled();
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

    describe('compareMegaPartType', () => {
      it('should forward to megaPartTypeService', () => {
        const entity = { id: 8406 };
        const entity2 = { id: 11390 };
        vitest.spyOn(megaPartTypeService, 'compareMegaPartType');
        comp.compareMegaPartType(entity, entity2);
        expect(megaPartTypeService.compareMegaPartType).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
