import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { Subject, from, of } from 'rxjs';

import { IPartCategory } from '../part-category.model';
import { PartCategoryService } from '../service/part-category.service';

import { PartCategoryFormService } from './part-category-form.service';
import { PartCategoryUpdate } from './part-category-update';

describe('PartCategory Management Update Component', () => {
  let comp: PartCategoryUpdate;
  let fixture: ComponentFixture<PartCategoryUpdate>;
  let activatedRoute: ActivatedRoute;
  let partCategoryFormService: PartCategoryFormService;
  let partCategoryService: PartCategoryService;

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

    fixture = TestBed.createComponent(PartCategoryUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partCategoryFormService = TestBed.inject(PartCategoryFormService);
    partCategoryService = TestBed.inject(PartCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const partCategory: IPartCategory = { id: 13269 };

      activatedRoute.data = of({ partCategory });
      comp.ngOnInit();

      expect(comp.partCategory).toEqual(partCategory);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPartCategory>();
      const partCategory = { id: 10447 };
      vitest.spyOn(partCategoryFormService, 'getPartCategory').mockReturnValue(partCategory);
      vitest.spyOn(partCategoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(partCategory);
      saveSubject.complete();

      // THEN
      expect(partCategoryFormService.getPartCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partCategoryService.update).toHaveBeenCalledWith(expect.objectContaining(partCategory));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPartCategory>();
      const partCategory = { id: 10447 };
      vitest.spyOn(partCategoryFormService, 'getPartCategory').mockReturnValue({ id: null });
      vitest.spyOn(partCategoryService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partCategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(partCategory);
      saveSubject.complete();

      // THEN
      expect(partCategoryFormService.getPartCategory).toHaveBeenCalled();
      expect(partCategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPartCategory>();
      const partCategory = { id: 10447 };
      vitest.spyOn(partCategoryService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ partCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partCategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
