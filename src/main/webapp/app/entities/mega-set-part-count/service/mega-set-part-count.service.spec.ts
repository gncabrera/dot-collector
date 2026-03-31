import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaSetPartCount } from '../mega-set-part-count.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-set-part-count.test-samples';

import { MegaSetPartCountService } from './mega-set-part-count.service';

const requireRestSample: IMegaSetPartCount = {
  ...sampleWithRequiredData,
};

describe('MegaSetPartCount Service', () => {
  let service: MegaSetPartCountService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaSetPartCount | IMegaSetPartCount[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaSetPartCountService);
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

    it('should create a MegaSetPartCount', () => {
      const megaSetPartCount = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaSetPartCount).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaSetPartCount', () => {
      const megaSetPartCount = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaSetPartCount).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaSetPartCount', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaSetPartCount', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaSetPartCount', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaSetPartCountToCollectionIfMissing', () => {
      it('should add a MegaSetPartCount to an empty array', () => {
        const megaSetPartCount: IMegaSetPartCount = sampleWithRequiredData;
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing([], megaSetPartCount);
        expect(expectedResult).toEqual([megaSetPartCount]);
      });

      it('should not add a MegaSetPartCount to an array that contains it', () => {
        const megaSetPartCount: IMegaSetPartCount = sampleWithRequiredData;
        const megaSetPartCountCollection: IMegaSetPartCount[] = [
          {
            ...megaSetPartCount,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing(megaSetPartCountCollection, megaSetPartCount);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaSetPartCount to an array that doesn't contain it", () => {
        const megaSetPartCount: IMegaSetPartCount = sampleWithRequiredData;
        const megaSetPartCountCollection: IMegaSetPartCount[] = [sampleWithPartialData];
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing(megaSetPartCountCollection, megaSetPartCount);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaSetPartCount);
      });

      it('should add only unique MegaSetPartCount to an array', () => {
        const megaSetPartCountArray: IMegaSetPartCount[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaSetPartCountCollection: IMegaSetPartCount[] = [sampleWithRequiredData];
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing(megaSetPartCountCollection, ...megaSetPartCountArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaSetPartCount: IMegaSetPartCount = sampleWithRequiredData;
        const megaSetPartCount2: IMegaSetPartCount = sampleWithPartialData;
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing([], megaSetPartCount, megaSetPartCount2);
        expect(expectedResult).toEqual([megaSetPartCount, megaSetPartCount2]);
      });

      it('should accept null and undefined values', () => {
        const megaSetPartCount: IMegaSetPartCount = sampleWithRequiredData;
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing([], null, megaSetPartCount, undefined);
        expect(expectedResult).toEqual([megaSetPartCount]);
      });

      it('should return initial array if no MegaSetPartCount is added', () => {
        const megaSetPartCountCollection: IMegaSetPartCount[] = [sampleWithRequiredData];
        expectedResult = service.addMegaSetPartCountToCollectionIfMissing(megaSetPartCountCollection, undefined, null);
        expect(expectedResult).toEqual(megaSetPartCountCollection);
      });
    });

    describe('compareMegaSetPartCount', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaSetPartCount(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31677 };
        const entity2 = null;

        const compareResult1 = service.compareMegaSetPartCount(entity1, entity2);
        const compareResult2 = service.compareMegaSetPartCount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31677 };
        const entity2 = { id: 30993 };

        const compareResult1 = service.compareMegaSetPartCount(entity1, entity2);
        const compareResult2 = service.compareMegaSetPartCount(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31677 };
        const entity2 = { id: 31677 };

        const compareResult1 = service.compareMegaSetPartCount(entity1, entity2);
        const compareResult2 = service.compareMegaSetPartCount(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
