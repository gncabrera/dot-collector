import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaAttribute } from 'app/entities/mega-attribute/mega-attribute.model';
import { MegaAttributeService } from 'app/entities/mega-attribute/service/mega-attribute.service';
import { IMegaSetType } from '../mega-set-type.model';
import { MegaSetTypeService } from '../service/mega-set-type.service';

import { MegaSetTypeFormService } from './mega-set-type-form.service';
import { MegaSetTypeUpdate } from './mega-set-type-update';

describe('MegaSetType Management Update Component', () => {
  let comp: MegaSetTypeUpdate;
  let fixture: ComponentFixture<MegaSetTypeUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaSetTypeFormService: MegaSetTypeFormService;
  let megaSetTypeService: MegaSetTypeService;
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

    fixture = TestBed.createComponent(MegaSetTypeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaSetTypeFormService = TestBed.inject(MegaSetTypeFormService);
    megaSetTypeService = TestBed.inject(MegaSetTypeService);
    megaAttributeService = TestBed.inject(MegaAttributeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaAttribute query and add missing value', () => {
      const megaSetType: IMegaSetType = { id: 12521 };
      const attributes: IMegaAttribute[] = [{ id: 12325 }];
      megaSetType.attributes = attributes;

      const megaAttributeCollection: IMegaAttribute[] = [{ id: 12325 }];
      vitest.spyOn(megaAttributeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaAttributeCollection })));
      const additionalMegaAttributes = [...attributes];
      const expectedCollection: IMegaAttribute[] = [...additionalMegaAttributes, ...megaAttributeCollection];
      vitest.spyOn(megaAttributeService, 'addMegaAttributeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaSetType });
      comp.ngOnInit();

      expect(megaAttributeService.query).toHaveBeenCalled();
      expect(megaAttributeService.addMegaAttributeToCollectionIfMissing).toHaveBeenCalledWith(
        megaAttributeCollection,
        ...additionalMegaAttributes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaAttributesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaSetType: IMegaSetType = { id: 12521 };
      const attribute: IMegaAttribute = { id: 12325 };
      megaSetType.attributes = [attribute];

      activatedRoute.data = of({ megaSetType });
      comp.ngOnInit();

      expect(comp.megaAttributesSharedCollection()).toContainEqual(attribute);
      expect(comp.megaSetType).toEqual(megaSetType);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSetType>();
      const megaSetType = { id: 18052 };
      vitest.spyOn(megaSetTypeFormService, 'getMegaSetType').mockReturnValue(megaSetType);
      vitest.spyOn(megaSetTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSetType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaSetType);
      saveSubject.complete();

      // THEN
      expect(megaSetTypeFormService.getMegaSetType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaSetTypeService.update).toHaveBeenCalledWith(expect.objectContaining(megaSetType));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSetType>();
      const megaSetType = { id: 18052 };
      vitest.spyOn(megaSetTypeFormService, 'getMegaSetType').mockReturnValue({ id: null });
      vitest.spyOn(megaSetTypeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSetType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaSetType);
      saveSubject.complete();

      // THEN
      expect(megaSetTypeFormService.getMegaSetType).toHaveBeenCalled();
      expect(megaSetTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSetType>();
      const megaSetType = { id: 18052 };
      vitest.spyOn(megaSetTypeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSetType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaSetTypeService.update).toHaveBeenCalled();
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
