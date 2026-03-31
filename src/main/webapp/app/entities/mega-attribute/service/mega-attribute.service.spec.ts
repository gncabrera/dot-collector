import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaAttribute } from '../mega-attribute.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../mega-attribute.test-samples';

import { MegaAttributeService } from './mega-attribute.service';

const requireRestSample: IMegaAttribute = {
  ...sampleWithRequiredData,
};

describe('MegaAttribute Service', () => {
  let service: MegaAttributeService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaAttribute | IMegaAttribute[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaAttributeService);
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

    it('should create a MegaAttribute', () => {
      const megaAttribute = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaAttribute).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaAttribute', () => {
      const megaAttribute = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaAttribute).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaAttribute', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaAttribute', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaAttribute', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaAttributeToCollectionIfMissing', () => {
      it('should add a MegaAttribute to an empty array', () => {
        const megaAttribute: IMegaAttribute = sampleWithRequiredData;
        expectedResult = service.addMegaAttributeToCollectionIfMissing([], megaAttribute);
        expect(expectedResult).toEqual([megaAttribute]);
      });

      it('should not add a MegaAttribute to an array that contains it', () => {
        const megaAttribute: IMegaAttribute = sampleWithRequiredData;
        const megaAttributeCollection: IMegaAttribute[] = [
          {
            ...megaAttribute,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaAttributeToCollectionIfMissing(megaAttributeCollection, megaAttribute);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaAttribute to an array that doesn't contain it", () => {
        const megaAttribute: IMegaAttribute = sampleWithRequiredData;
        const megaAttributeCollection: IMegaAttribute[] = [sampleWithPartialData];
        expectedResult = service.addMegaAttributeToCollectionIfMissing(megaAttributeCollection, megaAttribute);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaAttribute);
      });

      it('should add only unique MegaAttribute to an array', () => {
        const megaAttributeArray: IMegaAttribute[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaAttributeCollection: IMegaAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addMegaAttributeToCollectionIfMissing(megaAttributeCollection, ...megaAttributeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaAttribute: IMegaAttribute = sampleWithRequiredData;
        const megaAttribute2: IMegaAttribute = sampleWithPartialData;
        expectedResult = service.addMegaAttributeToCollectionIfMissing([], megaAttribute, megaAttribute2);
        expect(expectedResult).toEqual([megaAttribute, megaAttribute2]);
      });

      it('should accept null and undefined values', () => {
        const megaAttribute: IMegaAttribute = sampleWithRequiredData;
        expectedResult = service.addMegaAttributeToCollectionIfMissing([], null, megaAttribute, undefined);
        expect(expectedResult).toEqual([megaAttribute]);
      });

      it('should return initial array if no MegaAttribute is added', () => {
        const megaAttributeCollection: IMegaAttribute[] = [sampleWithRequiredData];
        expectedResult = service.addMegaAttributeToCollectionIfMissing(megaAttributeCollection, undefined, null);
        expect(expectedResult).toEqual(megaAttributeCollection);
      });
    });

    describe('compareMegaAttribute', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaAttribute(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 12325 };
        const entity2 = null;

        const compareResult1 = service.compareMegaAttribute(entity1, entity2);
        const compareResult2 = service.compareMegaAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 12325 };
        const entity2 = { id: 12871 };

        const compareResult1 = service.compareMegaAttribute(entity1, entity2);
        const compareResult2 = service.compareMegaAttribute(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 12325 };
        const entity2 = { id: 12325 };

        const compareResult1 = service.compareMegaAttribute(entity1, entity2);
        const compareResult2 = service.compareMegaAttribute(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
