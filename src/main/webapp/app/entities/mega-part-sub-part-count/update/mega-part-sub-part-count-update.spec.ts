import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { MegaPartService } from 'app/entities/mega-part/service/mega-part.service';
import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';
import { MegaPartSubPartCountService } from '../service/mega-part-sub-part-count.service';

import { MegaPartSubPartCountFormService } from './mega-part-sub-part-count-form.service';
import { MegaPartSubPartCountUpdate } from './mega-part-sub-part-count-update';

describe('MegaPartSubPartCount Management Update Component', () => {
  let comp: MegaPartSubPartCountUpdate;
  let fixture: ComponentFixture<MegaPartSubPartCountUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaPartSubPartCountFormService: MegaPartSubPartCountFormService;
  let megaPartSubPartCountService: MegaPartSubPartCountService;
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

    fixture = TestBed.createComponent(MegaPartSubPartCountUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaPartSubPartCountFormService = TestBed.inject(MegaPartSubPartCountFormService);
    megaPartSubPartCountService = TestBed.inject(MegaPartSubPartCountService);
    megaPartService = TestBed.inject(MegaPartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaPart query and add missing value', () => {
      const megaPartSubPartCount: IMegaPartSubPartCount = { id: 23227 };
      const part: IMegaPart = { id: 25333 };
      megaPartSubPartCount.part = part;
      const parentPart: IMegaPart = { id: 25333 };
      megaPartSubPartCount.parentPart = parentPart;

      const megaPartCollection: IMegaPart[] = [{ id: 25333 }];
      vitest.spyOn(megaPartService, 'query').mockReturnValue(of(new HttpResponse({ body: megaPartCollection })));
      const additionalMegaParts = [part, parentPart];
      const expectedCollection: IMegaPart[] = [...additionalMegaParts, ...megaPartCollection];
      vitest.spyOn(megaPartService, 'addMegaPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaPartSubPartCount });
      comp.ngOnInit();

      expect(megaPartService.query).toHaveBeenCalled();
      expect(megaPartService.addMegaPartToCollectionIfMissing).toHaveBeenCalledWith(
        megaPartCollection,
        ...additionalMegaParts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaPartsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaPartSubPartCount: IMegaPartSubPartCount = { id: 23227 };
      const part: IMegaPart = { id: 25333 };
      megaPartSubPartCount.part = part;
      const parentPart: IMegaPart = { id: 25333 };
      megaPartSubPartCount.parentPart = parentPart;

      activatedRoute.data = of({ megaPartSubPartCount });
      comp.ngOnInit();

      expect(comp.megaPartsSharedCollection()).toContainEqual(part);
      expect(comp.megaPartsSharedCollection()).toContainEqual(parentPart);
      expect(comp.megaPartSubPartCount).toEqual(megaPartSubPartCount);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPartSubPartCount>();
      const megaPartSubPartCount = { id: 4746 };
      vitest.spyOn(megaPartSubPartCountFormService, 'getMegaPartSubPartCount').mockReturnValue(megaPartSubPartCount);
      vitest.spyOn(megaPartSubPartCountService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPartSubPartCount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaPartSubPartCount);
      saveSubject.complete();

      // THEN
      expect(megaPartSubPartCountFormService.getMegaPartSubPartCount).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaPartSubPartCountService.update).toHaveBeenCalledWith(expect.objectContaining(megaPartSubPartCount));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPartSubPartCount>();
      const megaPartSubPartCount = { id: 4746 };
      vitest.spyOn(megaPartSubPartCountFormService, 'getMegaPartSubPartCount').mockReturnValue({ id: null });
      vitest.spyOn(megaPartSubPartCountService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPartSubPartCount: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaPartSubPartCount);
      saveSubject.complete();

      // THEN
      expect(megaPartSubPartCountFormService.getMegaPartSubPartCount).toHaveBeenCalled();
      expect(megaPartSubPartCountService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaPartSubPartCount>();
      const megaPartSubPartCount = { id: 4746 };
      vitest.spyOn(megaPartSubPartCountService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaPartSubPartCount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaPartSubPartCountService.update).toHaveBeenCalled();
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
