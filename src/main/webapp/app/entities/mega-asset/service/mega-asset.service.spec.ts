import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaAsset } from '../mega-asset.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-asset.test-samples';

import { MegaAssetService } from './mega-asset.service';

const requireRestSample: IMegaAsset = {
  ...sampleWithRequiredData,
};

describe('MegaAsset Service', () => {
  let service: MegaAssetService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaAsset | IMegaAsset[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaAssetService);
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

    it('should create a MegaAsset', () => {
      const megaAsset = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaAsset).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaAsset', () => {
      const megaAsset = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaAsset).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaAsset', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaAsset', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaAsset', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaAssetToCollectionIfMissing', () => {
      it('should add a MegaAsset to an empty array', () => {
        const megaAsset: IMegaAsset = sampleWithRequiredData;
        expectedResult = service.addMegaAssetToCollectionIfMissing([], megaAsset);
        expect(expectedResult).toEqual([megaAsset]);
      });

      it('should not add a MegaAsset to an array that contains it', () => {
        const megaAsset: IMegaAsset = sampleWithRequiredData;
        const megaAssetCollection: IMegaAsset[] = [
          {
            ...megaAsset,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaAssetToCollectionIfMissing(megaAssetCollection, megaAsset);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaAsset to an array that doesn't contain it", () => {
        const megaAsset: IMegaAsset = sampleWithRequiredData;
        const megaAssetCollection: IMegaAsset[] = [sampleWithPartialData];
        expectedResult = service.addMegaAssetToCollectionIfMissing(megaAssetCollection, megaAsset);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaAsset);
      });

      it('should add only unique MegaAsset to an array', () => {
        const megaAssetArray: IMegaAsset[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaAssetCollection: IMegaAsset[] = [sampleWithRequiredData];
        expectedResult = service.addMegaAssetToCollectionIfMissing(megaAssetCollection, ...megaAssetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaAsset: IMegaAsset = sampleWithRequiredData;
        const megaAsset2: IMegaAsset = sampleWithPartialData;
        expectedResult = service.addMegaAssetToCollectionIfMissing([], megaAsset, megaAsset2);
        expect(expectedResult).toEqual([megaAsset, megaAsset2]);
      });

      it('should accept null and undefined values', () => {
        const megaAsset: IMegaAsset = sampleWithRequiredData;
        expectedResult = service.addMegaAssetToCollectionIfMissing([], null, megaAsset, undefined);
        expect(expectedResult).toEqual([megaAsset]);
      });

      it('should return initial array if no MegaAsset is added', () => {
        const megaAssetCollection: IMegaAsset[] = [sampleWithRequiredData];
        expectedResult = service.addMegaAssetToCollectionIfMissing(megaAssetCollection, undefined, null);
        expect(expectedResult).toEqual(megaAssetCollection);
      });
    });

    describe('compareMegaAsset', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaAsset(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 9989 };
        const entity2 = null;

        const compareResult1 = service.compareMegaAsset(entity1, entity2);
        const compareResult2 = service.compareMegaAsset(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 9989 };
        const entity2 = { id: 13538 };

        const compareResult1 = service.compareMegaAsset(entity1, entity2);
        const compareResult2 = service.compareMegaAsset(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 9989 };
        const entity2 = { id: 9989 };

        const compareResult1 = service.compareMegaAsset(entity1, entity2);
        const compareResult2 = service.compareMegaAsset(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
