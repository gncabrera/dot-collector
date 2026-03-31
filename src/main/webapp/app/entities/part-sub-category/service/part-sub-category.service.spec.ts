import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPartSubCategory } from '../part-sub-category.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../part-sub-category.test-samples';

import { PartSubCategoryService } from './part-sub-category.service';

const requireRestSample: IPartSubCategory = {
  ...sampleWithRequiredData,
};

describe('PartSubCategory Service', () => {
  let service: PartSubCategoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IPartSubCategory | IPartSubCategory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PartSubCategoryService);
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

    it('should create a PartSubCategory', () => {
      const partSubCategory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(partSubCategory).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PartSubCategory', () => {
      const partSubCategory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(partSubCategory).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PartSubCategory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PartSubCategory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PartSubCategory', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPartSubCategoryToCollectionIfMissing', () => {
      it('should add a PartSubCategory to an empty array', () => {
        const partSubCategory: IPartSubCategory = sampleWithRequiredData;
        expectedResult = service.addPartSubCategoryToCollectionIfMissing([], partSubCategory);
        expect(expectedResult).toEqual([partSubCategory]);
      });

      it('should not add a PartSubCategory to an array that contains it', () => {
        const partSubCategory: IPartSubCategory = sampleWithRequiredData;
        const partSubCategoryCollection: IPartSubCategory[] = [
          {
            ...partSubCategory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPartSubCategoryToCollectionIfMissing(partSubCategoryCollection, partSubCategory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PartSubCategory to an array that doesn't contain it", () => {
        const partSubCategory: IPartSubCategory = sampleWithRequiredData;
        const partSubCategoryCollection: IPartSubCategory[] = [sampleWithPartialData];
        expectedResult = service.addPartSubCategoryToCollectionIfMissing(partSubCategoryCollection, partSubCategory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(partSubCategory);
      });

      it('should add only unique PartSubCategory to an array', () => {
        const partSubCategoryArray: IPartSubCategory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const partSubCategoryCollection: IPartSubCategory[] = [sampleWithRequiredData];
        expectedResult = service.addPartSubCategoryToCollectionIfMissing(partSubCategoryCollection, ...partSubCategoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const partSubCategory: IPartSubCategory = sampleWithRequiredData;
        const partSubCategory2: IPartSubCategory = sampleWithPartialData;
        expectedResult = service.addPartSubCategoryToCollectionIfMissing([], partSubCategory, partSubCategory2);
        expect(expectedResult).toEqual([partSubCategory, partSubCategory2]);
      });

      it('should accept null and undefined values', () => {
        const partSubCategory: IPartSubCategory = sampleWithRequiredData;
        expectedResult = service.addPartSubCategoryToCollectionIfMissing([], null, partSubCategory, undefined);
        expect(expectedResult).toEqual([partSubCategory]);
      });

      it('should return initial array if no PartSubCategory is added', () => {
        const partSubCategoryCollection: IPartSubCategory[] = [sampleWithRequiredData];
        expectedResult = service.addPartSubCategoryToCollectionIfMissing(partSubCategoryCollection, undefined, null);
        expect(expectedResult).toEqual(partSubCategoryCollection);
      });
    });

    describe('comparePartSubCategory', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePartSubCategory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12026 };
        const entity2 = null;

        const compareResult1 = service.comparePartSubCategory(entity1, entity2);
        const compareResult2 = service.comparePartSubCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12026 };
        const entity2 = { id: 2351 };

        const compareResult1 = service.comparePartSubCategory(entity1, entity2);
        const compareResult2 = service.comparePartSubCategory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12026 };
        const entity2 = { id: 12026 };

        const compareResult1 = service.comparePartSubCategory(entity1, entity2);
        const compareResult2 = service.comparePartSubCategory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
