import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaAttribute } from 'app/entities/mega-attribute/mega-attribute.model';
import { MegaAttributeService } from 'app/entities/mega-attribute/service/mega-attribute.service';
import { IMegaAttributeOption } from '../mega-attribute-option.model';
import { MegaAttributeOptionService } from '../service/mega-attribute-option.service';

import { MegaAttributeOptionFormService } from './mega-attribute-option-form.service';
import { MegaAttributeOptionUpdate } from './mega-attribute-option-update';

describe('MegaAttributeOption Management Update Component', () => {
  let comp: MegaAttributeOptionUpdate;
  let fixture: ComponentFixture<MegaAttributeOptionUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaAttributeOptionFormService: MegaAttributeOptionFormService;
  let megaAttributeOptionService: MegaAttributeOptionService;
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

    fixture = TestBed.createComponent(MegaAttributeOptionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaAttributeOptionFormService = TestBed.inject(MegaAttributeOptionFormService);
    megaAttributeOptionService = TestBed.inject(MegaAttributeOptionService);
    megaAttributeService = TestBed.inject(MegaAttributeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaAttribute query and add missing value', () => {
      const megaAttributeOption: IMegaAttributeOption = { id: 10547 };
      const attribute: IMegaAttribute = { id: 12325 };
      megaAttributeOption.attribute = attribute;

      const megaAttributeCollection: IMegaAttribute[] = [{ id: 12325 }];
      vitest.spyOn(megaAttributeService, 'query').mockReturnValue(of(new HttpResponse({ body: megaAttributeCollection })));
      const additionalMegaAttributes = [attribute];
      const expectedCollection: IMegaAttribute[] = [...additionalMegaAttributes, ...megaAttributeCollection];
      vitest.spyOn(megaAttributeService, 'addMegaAttributeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaAttributeOption });
      comp.ngOnInit();

      expect(megaAttributeService.query).toHaveBeenCalled();
      expect(megaAttributeService.addMegaAttributeToCollectionIfMissing).toHaveBeenCalledWith(
        megaAttributeCollection,
        ...additionalMegaAttributes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaAttributesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaAttributeOption: IMegaAttributeOption = { id: 10547 };
      const attribute: IMegaAttribute = { id: 12325 };
      megaAttributeOption.attribute = attribute;

      activatedRoute.data = of({ megaAttributeOption });
      comp.ngOnInit();

      expect(comp.megaAttributesSharedCollection()).toContainEqual(attribute);
      expect(comp.megaAttributeOption).toEqual(megaAttributeOption);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAttributeOption>();
      const megaAttributeOption = { id: 23475 };
      vitest.spyOn(megaAttributeOptionFormService, 'getMegaAttributeOption').mockReturnValue(megaAttributeOption);
      vitest.spyOn(megaAttributeOptionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAttributeOption });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaAttributeOption);
      saveSubject.complete();

      // THEN
      expect(megaAttributeOptionFormService.getMegaAttributeOption).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaAttributeOptionService.update).toHaveBeenCalledWith(expect.objectContaining(megaAttributeOption));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAttributeOption>();
      const megaAttributeOption = { id: 23475 };
      vitest.spyOn(megaAttributeOptionFormService, 'getMegaAttributeOption').mockReturnValue({ id: null });
      vitest.spyOn(megaAttributeOptionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAttributeOption: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaAttributeOption);
      saveSubject.complete();

      // THEN
      expect(megaAttributeOptionFormService.getMegaAttributeOption).toHaveBeenCalled();
      expect(megaAttributeOptionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaAttributeOption>();
      const megaAttributeOption = { id: 23475 };
      vitest.spyOn(megaAttributeOptionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaAttributeOption });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaAttributeOptionService.update).toHaveBeenCalled();
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
