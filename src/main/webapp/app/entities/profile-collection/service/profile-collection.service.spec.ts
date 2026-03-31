import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IProfileCollection } from '../profile-collection.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../profile-collection.test-samples';

import { ProfileCollectionService } from './profile-collection.service';

const requireRestSample: IProfileCollection = {
  ...sampleWithRequiredData,
};

describe('ProfileCollection Service', () => {
  let service: ProfileCollectionService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfileCollection | IProfileCollection[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileCollectionService);
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

    it('should create a ProfileCollection', () => {
      const profileCollection = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profileCollection).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfileCollection', () => {
      const profileCollection = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profileCollection).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfileCollection', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfileCollection', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfileCollection', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfileCollectionToCollectionIfMissing', () => {
      it('should add a ProfileCollection to an empty array', () => {
        const profileCollection: IProfileCollection = sampleWithRequiredData;
        expectedResult = service.addProfileCollectionToCollectionIfMissing([], profileCollection);
        expect(expectedResult).toEqual([profileCollection]);
      });

      it('should not add a ProfileCollection to an array that contains it', () => {
        const profileCollection: IProfileCollection = sampleWithRequiredData;
        const profileCollectionCollection: IProfileCollection[] = [
          {
            ...profileCollection,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfileCollectionToCollectionIfMissing(profileCollectionCollection, profileCollection);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfileCollection to an array that doesn't contain it", () => {
        const profileCollection: IProfileCollection = sampleWithRequiredData;
        const profileCollectionCollection: IProfileCollection[] = [sampleWithPartialData];
        expectedResult = service.addProfileCollectionToCollectionIfMissing(profileCollectionCollection, profileCollection);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profileCollection);
      });

      it('should add only unique ProfileCollection to an array', () => {
        const profileCollectionArray: IProfileCollection[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profileCollectionCollection: IProfileCollection[] = [sampleWithRequiredData];
        expectedResult = service.addProfileCollectionToCollectionIfMissing(profileCollectionCollection, ...profileCollectionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profileCollection: IProfileCollection = sampleWithRequiredData;
        const profileCollection2: IProfileCollection = sampleWithPartialData;
        expectedResult = service.addProfileCollectionToCollectionIfMissing([], profileCollection, profileCollection2);
        expect(expectedResult).toEqual([profileCollection, profileCollection2]);
      });

      it('should accept null and undefined values', () => {
        const profileCollection: IProfileCollection = sampleWithRequiredData;
        expectedResult = service.addProfileCollectionToCollectionIfMissing([], null, profileCollection, undefined);
        expect(expectedResult).toEqual([profileCollection]);
      });

      it('should return initial array if no ProfileCollection is added', () => {
        const profileCollectionCollection: IProfileCollection[] = [sampleWithRequiredData];
        expectedResult = service.addProfileCollectionToCollectionIfMissing(profileCollectionCollection, undefined, null);
        expect(expectedResult).toEqual(profileCollectionCollection);
      });
    });

    describe('compareProfileCollection', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfileCollection(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29855 };
        const entity2 = null;

        const compareResult1 = service.compareProfileCollection(entity1, entity2);
        const compareResult2 = service.compareProfileCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29855 };
        const entity2 = { id: 25147 };

        const compareResult1 = service.compareProfileCollection(entity1, entity2);
        const compareResult2 = service.compareProfileCollection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29855 };
        const entity2 = { id: 29855 };

        const compareResult1 = service.compareProfileCollection(entity1, entity2);
        const compareResult2 = service.compareProfileCollection(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
