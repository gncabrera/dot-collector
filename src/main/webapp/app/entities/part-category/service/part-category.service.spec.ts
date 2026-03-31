import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPartCategory } from '../part-category.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../part-category.test-samples';

import { PartCategoryService } from './part-category.service';

const requireRestSample: IPartCategory = {
  ...sampleWithRequiredData,
};

describe('PartCategory Service', () => {
  let service: PartCategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IPartCategory | IPartCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PartCategoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a PartCategory', () => {
      const partCategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(partCategory).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PartCategory', () => {
      const partCategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(partCategory).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PartCategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PartCategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PartCategory', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPartCategoryToCollectionIfMissing', () => {
      it('should add a PartCategory to an empty array', () => {
        const partCategory: IPartCategory = sampleWithRequiredData;
        expectedResult = service.addPartCategoryToCollectionIfMissing([], partCategory);
        expect(expectedResult).toEqual([partCategory]);
      });

      it('should not add a PartCategory to an array that contains it', () => {
        const partCategory: IPartCategory = sampleWithRequiredData;
        const partCategoryCollection: IPartCategory[] = [
          {
            ...partCategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPartCategoryToCollectionIfMissing(partCategoryCollection, partCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PartCategory to an array that doesn't contain it", () => {
        const partCategory: IPartCategory = sampleWithRequiredData;
        const partCategoryCollection: IPartCategory[] = [sampleWithPartialData];
        expectedResult = service.addPartCategoryToCollectionIfMissing(partCategoryCollection, partCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(partCategory);
      });

      it('should add only unique PartCategory to an array', () => {
        const partCategoryArray: IPartCategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const partCategoryCollection: IPartCategory[] = [sampleWithRequiredData];
        expectedResult = service.addPartCategoryToCollectionIfMissing(partCategoryCollection, ...partCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const partCategory: IPartCategory = sampleWithRequiredData;
        const partCategory2: IPartCategory = sampleWithPartialData;
        expectedResult = service.addPartCategoryToCollectionIfMissing([], partCategory, partCategory2);
        expect(expectedResult).toEqual([partCategory, partCategory2]);
      });

      it('should accept null and undefined values', () => {
        const partCategory: IPartCategory = sampleWithRequiredData;
        expectedResult = service.addPartCategoryToCollectionIfMissing([], null, partCategory, undefined);
        expect(expectedResult).toEqual([partCategory]);
      });

      it('should return initial array if no PartCategory is added', () => {
        const partCategoryCollection: IPartCategory[] = [sampleWithRequiredData];
        expectedResult = service.addPartCategoryToCollectionIfMissing(partCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(partCategoryCollection);
      });
    });

    describe('comparePartCategory', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePartCategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10447 };
        const entity2 = null;

        const compareResult1 = service.comparePartCategory(entity1, entity2);
        const compareResult2 = service.comparePartCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10447 };
        const entity2 = { id: 13269 };

        const compareResult1 = service.comparePartCategory(entity1, entity2);
        const compareResult2 = service.comparePartCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 10447 };
        const entity2 = { id: 10447 };

        const compareResult1 = service.comparePartCategory(entity1, entity2);
        const compareResult2 = service.comparePartCategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
