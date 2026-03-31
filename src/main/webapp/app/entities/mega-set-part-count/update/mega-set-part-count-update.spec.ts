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
import { IMegaSetPartCount } from '../mega-set-part-count.model';
import { MegaSetPartCountService } from '../service/mega-set-part-count.service';

import { MegaSetPartCountFormService } from './mega-set-part-count-form.service';
import { MegaSetPartCountUpdate } from './mega-set-part-count-update';

describe('MegaSetPartCount Management Update Component', () => {
  let comp: MegaSetPartCountUpdate;
  let fixture: ComponentFixture<MegaSetPartCountUpdate>;
  let activatedRoute: ActivatedRoute;
  let megaSetPartCountFormService: MegaSetPartCountFormService;
  let megaSetPartCountService: MegaSetPartCountService;
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

    fixture = TestBed.createComponent(MegaSetPartCountUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    megaSetPartCountFormService = TestBed.inject(MegaSetPartCountFormService);
    megaSetPartCountService = TestBed.inject(MegaSetPartCountService);
    megaSetService = TestBed.inject(MegaSetService);
    megaPartService = TestBed.inject(MegaPartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call MegaSet query and add missing value', () => {
      const megaSetPartCount: IMegaSetPartCount = { id: 30993 };
      const set: IMegaSet = { id: 26766 };
      megaSetPartCount.set = set;

      const megaSetCollection: IMegaSet[] = [{ id: 26766 }];
      vitest.spyOn(megaSetService, 'query').mockReturnValue(of(new HttpResponse({ body: megaSetCollection })));
      const additionalMegaSets = [set];
      const expectedCollection: IMegaSet[] = [...additionalMegaSets, ...megaSetCollection];
      vitest.spyOn(megaSetService, 'addMegaSetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaSetPartCount });
      comp.ngOnInit();

      expect(megaSetService.query).toHaveBeenCalled();
      expect(megaSetService.addMegaSetToCollectionIfMissing).toHaveBeenCalledWith(
        megaSetCollection,
        ...additionalMegaSets.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaSetsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call MegaPart query and add missing value', () => {
      const megaSetPartCount: IMegaSetPartCount = { id: 30993 };
      const part: IMegaPart = { id: 25333 };
      megaSetPartCount.part = part;

      const megaPartCollection: IMegaPart[] = [{ id: 25333 }];
      vitest.spyOn(megaPartService, 'query').mockReturnValue(of(new HttpResponse({ body: megaPartCollection })));
      const additionalMegaParts = [part];
      const expectedCollection: IMegaPart[] = [...additionalMegaParts, ...megaPartCollection];
      vitest.spyOn(megaPartService, 'addMegaPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ megaSetPartCount });
      comp.ngOnInit();

      expect(megaPartService.query).toHaveBeenCalled();
      expect(megaPartService.addMegaPartToCollectionIfMissing).toHaveBeenCalledWith(
        megaPartCollection,
        ...additionalMegaParts.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.megaPartsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const megaSetPartCount: IMegaSetPartCount = { id: 30993 };
      const set: IMegaSet = { id: 26766 };
      megaSetPartCount.set = set;
      const part: IMegaPart = { id: 25333 };
      megaSetPartCount.part = part;

      activatedRoute.data = of({ megaSetPartCount });
      comp.ngOnInit();

      expect(comp.megaSetsSharedCollection()).toContainEqual(set);
      expect(comp.megaPartsSharedCollection()).toContainEqual(part);
      expect(comp.megaSetPartCount).toEqual(megaSetPartCount);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSetPartCount>();
      const megaSetPartCount = { id: 31677 };
      vitest.spyOn(megaSetPartCountFormService, 'getMegaSetPartCount').mockReturnValue(megaSetPartCount);
      vitest.spyOn(megaSetPartCountService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSetPartCount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaSetPartCount);
      saveSubject.complete();

      // THEN
      expect(megaSetPartCountFormService.getMegaSetPartCount).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(megaSetPartCountService.update).toHaveBeenCalledWith(expect.objectContaining(megaSetPartCount));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSetPartCount>();
      const megaSetPartCount = { id: 31677 };
      vitest.spyOn(megaSetPartCountFormService, 'getMegaSetPartCount').mockReturnValue({ id: null });
      vitest.spyOn(megaSetPartCountService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSetPartCount: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(megaSetPartCount);
      saveSubject.complete();

      // THEN
      expect(megaSetPartCountFormService.getMegaSetPartCount).toHaveBeenCalled();
      expect(megaSetPartCountService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IMegaSetPartCount>();
      const megaSetPartCount = { id: 31677 };
      vitest.spyOn(megaSetPartCountService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ megaSetPartCount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(megaSetPartCountService.update).toHaveBeenCalled();
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
