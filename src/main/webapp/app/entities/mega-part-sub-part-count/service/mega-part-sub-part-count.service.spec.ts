import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../mega-part-sub-part-count.test-samples';

import { MegaPartSubPartCountService } from './mega-part-sub-part-count.service';

const requireRestSample: IMegaPartSubPartCount = {
  ...sampleWithRequiredData,
};

describe('MegaPartSubPartCount Service', () => {
  let service: MegaPartSubPartCountService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaPartSubPartCount | IMegaPartSubPartCount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaPartSubPartCountService);
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

    it('should create a MegaPartSubPartCount', () => {
      const megaPartSubPartCount = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaPartSubPartCount).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaPartSubPartCount', () => {
      const megaPartSubPartCount = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaPartSubPartCount).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaPartSubPartCount', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaPartSubPartCount', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaPartSubPartCount', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaPartSubPartCountToCollectionIfMissing', () => {
      it('should add a MegaPartSubPartCount to an empty array', () => {
        const megaPartSubPartCount: IMegaPartSubPartCount = sampleWithRequiredData;
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing([], megaPartSubPartCount);
        expect(expectedResult).toEqual([megaPartSubPartCount]);
      });

      it('should not add a MegaPartSubPartCount to an array that contains it', () => {
        const megaPartSubPartCount: IMegaPartSubPartCount = sampleWithRequiredData;
        const megaPartSubPartCountCollection: IMegaPartSubPartCount[] = [
          {
            ...megaPartSubPartCount,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing(megaPartSubPartCountCollection, megaPartSubPartCount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaPartSubPartCount to an array that doesn't contain it", () => {
        const megaPartSubPartCount: IMegaPartSubPartCount = sampleWithRequiredData;
        const megaPartSubPartCountCollection: IMegaPartSubPartCount[] = [sampleWithPartialData];
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing(megaPartSubPartCountCollection, megaPartSubPartCount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaPartSubPartCount);
      });

      it('should add only unique MegaPartSubPartCount to an array', () => {
        const megaPartSubPartCountArray: IMegaPartSubPartCount[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaPartSubPartCountCollection: IMegaPartSubPartCount[] = [sampleWithRequiredData];
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing(megaPartSubPartCountCollection, ...megaPartSubPartCountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaPartSubPartCount: IMegaPartSubPartCount = sampleWithRequiredData;
        const megaPartSubPartCount2: IMegaPartSubPartCount = sampleWithPartialData;
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing([], megaPartSubPartCount, megaPartSubPartCount2);
        expect(expectedResult).toEqual([megaPartSubPartCount, megaPartSubPartCount2]);
      });

      it('should accept null and undefined values', () => {
        const megaPartSubPartCount: IMegaPartSubPartCount = sampleWithRequiredData;
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing([], null, megaPartSubPartCount, undefined);
        expect(expectedResult).toEqual([megaPartSubPartCount]);
      });

      it('should return initial array if no MegaPartSubPartCount is added', () => {
        const megaPartSubPartCountCollection: IMegaPartSubPartCount[] = [sampleWithRequiredData];
        expectedResult = service.addMegaPartSubPartCountToCollectionIfMissing(megaPartSubPartCountCollection, undefined, null);
        expect(expectedResult).toEqual(megaPartSubPartCountCollection);
      });
    });

    describe('compareMegaPartSubPartCount', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaPartSubPartCount(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4746 };
        const entity2 = null;

        const compareResult1 = service.compareMegaPartSubPartCount(entity1, entity2);
        const compareResult2 = service.compareMegaPartSubPartCount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4746 };
        const entity2 = { id: 23227 };

        const compareResult1 = service.compareMegaPartSubPartCount(entity1, entity2);
        const compareResult2 = service.compareMegaPartSubPartCount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4746 };
        const entity2 = { id: 4746 };

        const compareResult1 = service.compareMegaPartSubPartCount(entity1, entity2);
        const compareResult2 = service.compareMegaPartSubPartCount(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
