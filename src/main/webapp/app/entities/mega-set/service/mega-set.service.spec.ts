import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IMegaSet } from '../mega-set.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-set.test-samples';

import { MegaSetService, RestMegaSet } from './mega-set.service';

const requireRestSample: RestMegaSet = {
  ...sampleWithRequiredData,
  releaseDate: sampleWithRequiredData.releaseDate?.format(DATE_FORMAT),
};

describe('MegaSet Service', () => {
  let service: MegaSetService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaSet | IMegaSet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaSetService);
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

    it('should create a MegaSet', () => {
      const megaSet = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaSet).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaSet', () => {
      const megaSet = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaSet).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaSet', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaSet', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaSet', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaSetToCollectionIfMissing', () => {
      it('should add a MegaSet to an empty array', () => {
        const megaSet: IMegaSet = sampleWithRequiredData;
        expectedResult = service.addMegaSetToCollectionIfMissing([], megaSet);
        expect(expectedResult).toEqual([megaSet]);
      });

      it('should not add a MegaSet to an array that contains it', () => {
        const megaSet: IMegaSet = sampleWithRequiredData;
        const megaSetCollection: IMegaSet[] = [
          {
            ...megaSet,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaSetToCollectionIfMissing(megaSetCollection, megaSet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaSet to an array that doesn't contain it", () => {
        const megaSet: IMegaSet = sampleWithRequiredData;
        const megaSetCollection: IMegaSet[] = [sampleWithPartialData];
        expectedResult = service.addMegaSetToCollectionIfMissing(megaSetCollection, megaSet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaSet);
      });

      it('should add only unique MegaSet to an array', () => {
        const megaSetArray: IMegaSet[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaSetCollection: IMegaSet[] = [sampleWithRequiredData];
        expectedResult = service.addMegaSetToCollectionIfMissing(megaSetCollection, ...megaSetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaSet: IMegaSet = sampleWithRequiredData;
        const megaSet2: IMegaSet = sampleWithPartialData;
        expectedResult = service.addMegaSetToCollectionIfMissing([], megaSet, megaSet2);
        expect(expectedResult).toEqual([megaSet, megaSet2]);
      });

      it('should accept null and undefined values', () => {
        const megaSet: IMegaSet = sampleWithRequiredData;
        expectedResult = service.addMegaSetToCollectionIfMissing([], null, megaSet, undefined);
        expect(expectedResult).toEqual([megaSet]);
      });

      it('should return initial array if no MegaSet is added', () => {
        const megaSetCollection: IMegaSet[] = [sampleWithRequiredData];
        expectedResult = service.addMegaSetToCollectionIfMissing(megaSetCollection, undefined, null);
        expect(expectedResult).toEqual(megaSetCollection);
      });
    });

    describe('compareMegaSet', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaSet(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 26766 };
        const entity2 = null;

        const compareResult1 = service.compareMegaSet(entity1, entity2);
        const compareResult2 = service.compareMegaSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 26766 };
        const entity2 = { id: 10614 };

        const compareResult1 = service.compareMegaSet(entity1, entity2);
        const compareResult2 = service.compareMegaSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 26766 };
        const entity2 = { id: 26766 };

        const compareResult1 = service.compareMegaSet(entity1, entity2);
        const compareResult2 = service.compareMegaSet(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
