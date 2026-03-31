import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaPartType } from '../mega-part-type.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-part-type.test-samples';

import { MegaPartTypeService } from './mega-part-type.service';

const requireRestSample: IMegaPartType = {
  ...sampleWithRequiredData,
};

describe('MegaPartType Service', () => {
  let service: MegaPartTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaPartType | IMegaPartType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaPartTypeService);
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

    it('should create a MegaPartType', () => {
      const megaPartType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaPartType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaPartType', () => {
      const megaPartType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaPartType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaPartType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaPartType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaPartType', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaPartTypeToCollectionIfMissing', () => {
      it('should add a MegaPartType to an empty array', () => {
        const megaPartType: IMegaPartType = sampleWithRequiredData;
        expectedResult = service.addMegaPartTypeToCollectionIfMissing([], megaPartType);
        expect(expectedResult).toEqual([megaPartType]);
      });

      it('should not add a MegaPartType to an array that contains it', () => {
        const megaPartType: IMegaPartType = sampleWithRequiredData;
        const megaPartTypeCollection: IMegaPartType[] = [
          {
            ...megaPartType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaPartTypeToCollectionIfMissing(megaPartTypeCollection, megaPartType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaPartType to an array that doesn't contain it", () => {
        const megaPartType: IMegaPartType = sampleWithRequiredData;
        const megaPartTypeCollection: IMegaPartType[] = [sampleWithPartialData];
        expectedResult = service.addMegaPartTypeToCollectionIfMissing(megaPartTypeCollection, megaPartType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaPartType);
      });

      it('should add only unique MegaPartType to an array', () => {
        const megaPartTypeArray: IMegaPartType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaPartTypeCollection: IMegaPartType[] = [sampleWithRequiredData];
        expectedResult = service.addMegaPartTypeToCollectionIfMissing(megaPartTypeCollection, ...megaPartTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaPartType: IMegaPartType = sampleWithRequiredData;
        const megaPartType2: IMegaPartType = sampleWithPartialData;
        expectedResult = service.addMegaPartTypeToCollectionIfMissing([], megaPartType, megaPartType2);
        expect(expectedResult).toEqual([megaPartType, megaPartType2]);
      });

      it('should accept null and undefined values', () => {
        const megaPartType: IMegaPartType = sampleWithRequiredData;
        expectedResult = service.addMegaPartTypeToCollectionIfMissing([], null, megaPartType, undefined);
        expect(expectedResult).toEqual([megaPartType]);
      });

      it('should return initial array if no MegaPartType is added', () => {
        const megaPartTypeCollection: IMegaPartType[] = [sampleWithRequiredData];
        expectedResult = service.addMegaPartTypeToCollectionIfMissing(megaPartTypeCollection, undefined, null);
        expect(expectedResult).toEqual(megaPartTypeCollection);
      });
    });

    describe('compareMegaPartType', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaPartType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 8406 };
        const entity2 = null;

        const compareResult1 = service.compareMegaPartType(entity1, entity2);
        const compareResult2 = service.compareMegaPartType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 8406 };
        const entity2 = { id: 11390 };

        const compareResult1 = service.compareMegaPartType(entity1, entity2);
        const compareResult2 = service.compareMegaPartType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 8406 };
        const entity2 = { id: 8406 };

        const compareResult1 = service.compareMegaPartType(entity1, entity2);
        const compareResult2 = service.compareMegaPartType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
