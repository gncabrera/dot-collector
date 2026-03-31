import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IProfileCollectionSet } from '../profile-collection-set.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../profile-collection-set.test-samples';

import { ProfileCollectionSetService, RestProfileCollectionSet } from './profile-collection-set.service';

const requireRestSample: RestProfileCollectionSet = {
  ...sampleWithRequiredData,
  dateAdded: sampleWithRequiredData.dateAdded?.format(DATE_FORMAT),
};

describe('ProfileCollectionSet Service', () => {
  let service: ProfileCollectionSetService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfileCollectionSet | IProfileCollectionSet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileCollectionSetService);
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

    it('should create a ProfileCollectionSet', () => {
      const profileCollectionSet = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profileCollectionSet).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfileCollectionSet', () => {
      const profileCollectionSet = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profileCollectionSet).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfileCollectionSet', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfileCollectionSet', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfileCollectionSet', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfileCollectionSetToCollectionIfMissing', () => {
      it('should add a ProfileCollectionSet to an empty array', () => {
        const profileCollectionSet: IProfileCollectionSet = sampleWithRequiredData;
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing([], profileCollectionSet);
        expect(expectedResult).toEqual([profileCollectionSet]);
      });

      it('should not add a ProfileCollectionSet to an array that contains it', () => {
        const profileCollectionSet: IProfileCollectionSet = sampleWithRequiredData;
        const profileCollectionSetCollection: IProfileCollectionSet[] = [
          {
            ...profileCollectionSet,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing(profileCollectionSetCollection, profileCollectionSet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfileCollectionSet to an array that doesn't contain it", () => {
        const profileCollectionSet: IProfileCollectionSet = sampleWithRequiredData;
        const profileCollectionSetCollection: IProfileCollectionSet[] = [sampleWithPartialData];
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing(profileCollectionSetCollection, profileCollectionSet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profileCollectionSet);
      });

      it('should add only unique ProfileCollectionSet to an array', () => {
        const profileCollectionSetArray: IProfileCollectionSet[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profileCollectionSetCollection: IProfileCollectionSet[] = [sampleWithRequiredData];
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing(profileCollectionSetCollection, ...profileCollectionSetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profileCollectionSet: IProfileCollectionSet = sampleWithRequiredData;
        const profileCollectionSet2: IProfileCollectionSet = sampleWithPartialData;
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing([], profileCollectionSet, profileCollectionSet2);
        expect(expectedResult).toEqual([profileCollectionSet, profileCollectionSet2]);
      });

      it('should accept null and undefined values', () => {
        const profileCollectionSet: IProfileCollectionSet = sampleWithRequiredData;
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing([], null, profileCollectionSet, undefined);
        expect(expectedResult).toEqual([profileCollectionSet]);
      });

      it('should return initial array if no ProfileCollectionSet is added', () => {
        const profileCollectionSetCollection: IProfileCollectionSet[] = [sampleWithRequiredData];
        expectedResult = service.addProfileCollectionSetToCollectionIfMissing(profileCollectionSetCollection, undefined, null);
        expect(expectedResult).toEqual(profileCollectionSetCollection);
      });
    });

    describe('compareProfileCollectionSet', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfileCollectionSet(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 5128 };
        const entity2 = null;

        const compareResult1 = service.compareProfileCollectionSet(entity1, entity2);
        const compareResult2 = service.compareProfileCollectionSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 5128 };
        const entity2 = { id: 633 };

        const compareResult1 = service.compareProfileCollectionSet(entity1, entity2);
        const compareResult2 = service.compareProfileCollectionSet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 5128 };
        const entity2 = { id: 5128 };

        const compareResult1 = service.compareProfileCollectionSet(entity1, entity2);
        const compareResult2 = service.compareProfileCollectionSet(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
