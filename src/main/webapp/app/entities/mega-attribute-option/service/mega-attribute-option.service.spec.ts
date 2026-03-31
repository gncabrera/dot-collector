import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IMegaAttributeOption } from '../mega-attribute-option.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../mega-attribute-option.test-samples';

import { MegaAttributeOptionService } from './mega-attribute-option.service';

const requireRestSample: IMegaAttributeOption = {
  ...sampleWithRequiredData,
};

describe('MegaAttributeOption Service', () => {
  let service: MegaAttributeOptionService;
  let httpMock: HttpTestingController;
  let expectedResult: IMegaAttributeOption | IMegaAttributeOption[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MegaAttributeOptionService);
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

    it('should create a MegaAttributeOption', () => {
      const megaAttributeOption = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(megaAttributeOption).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MegaAttributeOption', () => {
      const megaAttributeOption = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(megaAttributeOption).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MegaAttributeOption', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MegaAttributeOption', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MegaAttributeOption', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addMegaAttributeOptionToCollectionIfMissing', () => {
      it('should add a MegaAttributeOption to an empty array', () => {
        const megaAttributeOption: IMegaAttributeOption = sampleWithRequiredData;
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing([], megaAttributeOption);
        expect(expectedResult).toEqual([megaAttributeOption]);
      });

      it('should not add a MegaAttributeOption to an array that contains it', () => {
        const megaAttributeOption: IMegaAttributeOption = sampleWithRequiredData;
        const megaAttributeOptionCollection: IMegaAttributeOption[] = [
          {
            ...megaAttributeOption,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing(megaAttributeOptionCollection, megaAttributeOption);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MegaAttributeOption to an array that doesn't contain it", () => {
        const megaAttributeOption: IMegaAttributeOption = sampleWithRequiredData;
        const megaAttributeOptionCollection: IMegaAttributeOption[] = [sampleWithPartialData];
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing(megaAttributeOptionCollection, megaAttributeOption);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(megaAttributeOption);
      });

      it('should add only unique MegaAttributeOption to an array', () => {
        const megaAttributeOptionArray: IMegaAttributeOption[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const megaAttributeOptionCollection: IMegaAttributeOption[] = [sampleWithRequiredData];
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing(megaAttributeOptionCollection, ...megaAttributeOptionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const megaAttributeOption: IMegaAttributeOption = sampleWithRequiredData;
        const megaAttributeOption2: IMegaAttributeOption = sampleWithPartialData;
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing([], megaAttributeOption, megaAttributeOption2);
        expect(expectedResult).toEqual([megaAttributeOption, megaAttributeOption2]);
      });

      it('should accept null and undefined values', () => {
        const megaAttributeOption: IMegaAttributeOption = sampleWithRequiredData;
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing([], null, megaAttributeOption, undefined);
        expect(expectedResult).toEqual([megaAttributeOption]);
      });

      it('should return initial array if no MegaAttributeOption is added', () => {
        const megaAttributeOptionCollection: IMegaAttributeOption[] = [sampleWithRequiredData];
        expectedResult = service.addMegaAttributeOptionToCollectionIfMissing(megaAttributeOptionCollection, undefined, null);
        expect(expectedResult).toEqual(megaAttributeOptionCollection);
      });
    });

    describe('compareMegaAttributeOption', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMegaAttributeOption(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23475 };
        const entity2 = null;

        const compareResult1 = service.compareMegaAttributeOption(entity1, entity2);
        const compareResult2 = service.compareMegaAttributeOption(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23475 };
        const entity2 = { id: 10547 };

        const compareResult1 = service.compareMegaAttributeOption(entity1, entity2);
        const compareResult2 = service.compareMegaAttributeOption(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23475 };
        const entity2 = { id: 23475 };

        const compareResult1 = service.compareMegaAttributeOption(entity1, entity2);
        const compareResult2 = service.compareMegaAttributeOption(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
