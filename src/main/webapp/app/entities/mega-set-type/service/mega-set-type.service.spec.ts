import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaSetType } from '../mega-set-type.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-set-type.test-samples';

import { MegaSetTypeService } from './mega-set-type.service';

const requireRestSample: IMegaSetType = {
  ...sampleWithRequiredData,
};

describe('MegaSetType Service', () => {
  let service: MegaSetTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaSetType | IMegaSetType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaSetTypeService);
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

    it('should create a MegaSetType', () => {
      const megaSetType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaSetType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaSetType', () => {
      const megaSetType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaSetType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaSetType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaSetType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaSetType', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaSetTypeToCollectionIfMissing', () => {
      it('should add a MegaSetType to an empty array', () => {
        const megaSetType: IMegaSetType = sampleWithRequiredData;
        expectedResult = service.addMegaSetTypeToCollectionIfMissing([], megaSetType);
        expect(expectedResult).toEqual([megaSetType]);
      });

      it('should not add a MegaSetType to an array that contains it', () => {
        const megaSetType: IMegaSetType = sampleWithRequiredData;
        const megaSetTypeCollection: IMegaSetType[] = [
          {
            ...megaSetType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaSetTypeToCollectionIfMissing(megaSetTypeCollection, megaSetType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaSetType to an array that doesn't contain it", () => {
        const megaSetType: IMegaSetType = sampleWithRequiredData;
        const megaSetTypeCollection: IMegaSetType[] = [sampleWithPartialData];
        expectedResult = service.addMegaSetTypeToCollectionIfMissing(megaSetTypeCollection, megaSetType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaSetType);
      });

      it('should add only unique MegaSetType to an array', () => {
        const megaSetTypeArray: IMegaSetType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaSetTypeCollection: IMegaSetType[] = [sampleWithRequiredData];
        expectedResult = service.addMegaSetTypeToCollectionIfMissing(megaSetTypeCollection, ...megaSetTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaSetType: IMegaSetType = sampleWithRequiredData;
        const megaSetType2: IMegaSetType = sampleWithPartialData;
        expectedResult = service.addMegaSetTypeToCollectionIfMissing([], megaSetType, megaSetType2);
        expect(expectedResult).toEqual([megaSetType, megaSetType2]);
      });

      it('should accept null and undefined values', () => {
        const megaSetType: IMegaSetType = sampleWithRequiredData;
        expectedResult = service.addMegaSetTypeToCollectionIfMissing([], null, megaSetType, undefined);
        expect(expectedResult).toEqual([megaSetType]);
      });

      it('should return initial array if no MegaSetType is added', () => {
        const megaSetTypeCollection: IMegaSetType[] = [sampleWithRequiredData];
        expectedResult = service.addMegaSetTypeToCollectionIfMissing(megaSetTypeCollection, undefined, null);
        expect(expectedResult).toEqual(megaSetTypeCollection);
      });
    });

    describe('compareMegaSetType', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaSetType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18052 };
        const entity2 = null;

        const compareResult1 = service.compareMegaSetType(entity1, entity2);
        const compareResult2 = service.compareMegaSetType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18052 };
        const entity2 = { id: 12521 };

        const compareResult1 = service.compareMegaSetType(entity1, entity2);
        const compareResult2 = service.compareMegaSetType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18052 };
        const entity2 = { id: 18052 };

        const compareResult1 = service.compareMegaSetType(entity1, entity2);
        const compareResult2 = service.compareMegaSetType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
