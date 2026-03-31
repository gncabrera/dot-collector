import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaAttribute } from 'app/entities/mega-attribute/mega-attribute.model';
import { MegaAttributeService } from 'app/entities/mega-attribute/service/mega-attribute.service';
import { IMegaPartType } from '../mega-part-type.model';
import { MegaPartTypeService } from '../service/mega-part-type.service';

import { MegaPartTypeFormService } from './mega-part-type-form.service';
import { MegaPartTypeUpdate } from './mega-part-type-update';

describe('MegaPartType Management Update Component', () => {
  let comp: MegaPartTypeUpdate;
  let fixture: ComponentFixture<MegaPartTypeUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaPartTypeFormService: MegaPartTypeFormService;
  let megaPartTypeService: MegaPartTypeService;
  let megaAttributeService: MegaAttributeService;

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

    fixture = TestBed.createComponent(MegaPartTypeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaPartTypeFormService = TestBed.inject(MegaPartTypeFormService);
    megaPartTypeService = TestBed.inject(MegaPartTypeService);
    megaAttributeService = TestBed.inject(MegaAttributeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaAttribute query and add missing value', () => {
      const megaPartType: IMegaPartType = { id: 11390 };
      const attributes: IMegaAttribute[] = [{ id: 12325 }];
      megaPartType.attributes = attributes;

      const megaAttributeCollection: IMegaAttribute[] = [{ id: 12325 }];
      vitest.spyOn(megaAttributeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaAttributeCollection })));
      const additionalMegaAttributes = [...attributes];
      const expectedCollection: IMegaAttribute[] = [...additionalMegaAttributes, ...megaAttributeCollection];
      vitest.spyOn(megaAttributeService, 'addMegaAttributeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaPartType });
      comp.ngOnInit();

      expect(megaAttributeService.query).toHaveBeenCalled();
      expect(megaAttributeService.addMegaAttributeToCollectionIfMissing).toHaveBeenCalledWith(
        megaAttributeCollection,
        ...additionalMegaAttributes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaAttributesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaPartType: IMegaPartType = { id: 11390 };
      const attribute: IMegaAttribute = { id: 12325 };
      megaPartType.attributes = [attribute];

      activatedRoute.data = of({ megaPartType });
      comp.ngOnInit();

      expect(comp.megaAttributesSharedCollection()).toContainEqual(attribute);
      expect(comp.megaPartType).toEqual(megaPartType);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPartType>();
      const megaPartType = { id: 8406 };
      vitest.spyOn(megaPartTypeFormService, 'getMegaPartType').mockReturnValue(megaPartType);
      vitest.spyOn(megaPartTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPartType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaPartType);
      saveSubject.complete();

      // THEN
      expect(megaPartTypeFormService.getMegaPartType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaPartTypeService.update).toHaveBeenCalledWith(expect.objectContaining(megaPartType));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPartType>();
      const megaPartType = { id: 8406 };
      vitest.spyOn(megaPartTypeFormService, 'getMegaPartType').mockReturnValue({ id: null });
      vitest.spyOn(megaPartTypeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPartType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaPartType);
      saveSubject.complete();

      // THEN
      expect(megaPartTypeFormService.getMegaPartType).toHaveBeenCalled();
      expect(megaPartTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPartType>();
      const megaPartType = { id: 8406 };
      vitest.spyOn(megaPartTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPartType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaPartTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareMegaAttribute', () => {
      it('should forward to megaAttributeService', () => {
        const entity = { id: 12325 };
        const entity2 = { id: 12871 };
        vitest.spyOn(megaAttributeService, 'compareMegaAttribute');
        comp.compareMegaAttribute(entity, entity2);
        expect(megaAttributeService.compareMegaAttribute).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
