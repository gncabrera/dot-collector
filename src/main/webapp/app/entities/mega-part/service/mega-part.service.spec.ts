import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IMegaPart } from '../mega-part.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-part.test-samples';

import { MegaPartService, RestMegaPart } from './mega-part.service';

const requireRestSample: RestMegaPart = {
  ...sampleWithRequiredData,
  releaseDate: sampleWithRequiredData.releaseDate?.format(DATE_FORMAT),
};

describe('MegaPart Service', () => {
  let service: MegaPartService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaPart | IMegaPart[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaPartService);
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

    it('should create a MegaPart', () => {
      const megaPart = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaPart).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaPart', () => {
      const megaPart = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaPart).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaPart', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaPart', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaPart', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaPartToCollectionIfMissing', () => {
      it('should add a MegaPart to an empty array', () => {
        const megaPart: IMegaPart = sampleWithRequiredData;
        expectedResult = service.addMegaPartToCollectionIfMissing([], megaPart);
        expect(expectedResult).toEqual([megaPart]);
      });

      it('should not add a MegaPart to an array that contains it', () => {
        const megaPart: IMegaPart = sampleWithRequiredData;
        const megaPartCollection: IMegaPart[] = [
          {
            ...megaPart,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaPartToCollectionIfMissing(megaPartCollection, megaPart);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaPart to an array that doesn't contain it", () => {
        const megaPart: IMegaPart = sampleWithRequiredData;
        const megaPartCollection: IMegaPart[] = [sampleWithPartialData];
        expectedResult = service.addMegaPartToCollectionIfMissing(megaPartCollection, megaPart);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaPart);
      });

      it('should add only unique MegaPart to an array', () => {
        const megaPartArray: IMegaPart[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaPartCollection: IMegaPart[] = [sampleWithRequiredData];
        expectedResult = service.addMegaPartToCollectionIfMissing(megaPartCollection, ...megaPartArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaPart: IMegaPart = sampleWithRequiredData;
        const megaPart2: IMegaPart = sampleWithPartialData;
        expectedResult = service.addMegaPartToCollectionIfMissing([], megaPart, megaPart2);
        expect(expectedResult).toEqual([megaPart, megaPart2]);
      });

      it('should accept null and undefined values', () => {
        const megaPart: IMegaPart = sampleWithRequiredData;
        expectedResult = service.addMegaPartToCollectionIfMissing([], null, megaPart, undefined);
        expect(expectedResult).toEqual([megaPart]);
      });

      it('should return initial array if no MegaPart is added', () => {
        const megaPartCollection: IMegaPart[] = [sampleWithRequiredData];
        expectedResult = service.addMegaPartToCollectionIfMissing(megaPartCollection, undefined, null);
        expect(expectedResult).toEqual(megaPartCollection);
      });
    });

    describe('compareMegaPart', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaPart(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25333 };
        const entity2 = null;

        const compareResult1 = service.compareMegaPart(entity1, entity2);
        const compareResult2 = service.compareMegaPart(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25333 };
        const entity2 = { id: 5536 };

        const compareResult1 = service.compareMegaPart(entity1, entity2);
        const compareResult2 = service.compareMegaPart(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25333 };
        const entity2 = { id: 25333 };

        const compareResult1 = service.compareMegaPart(entity1, entity2);
        const compareResult2 = service.compareMegaPart(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
