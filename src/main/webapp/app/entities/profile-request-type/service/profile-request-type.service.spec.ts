import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IProfileRequestType } from '../profile-request-type.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../profile-request-type.test-samples';

import { ProfileRequestTypeService } from './profile-request-type.service';

const requireRestSample: IProfileRequestType = {
  ...sampleWithRequiredData,
};

describe('ProfileRequestType Service', () => {
  let service: ProfileRequestTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfileRequestType | IProfileRequestType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileRequestTypeService);
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

    it('should create a ProfileRequestType', () => {
      const profileRequestType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profileRequestType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfileRequestType', () => {
      const profileRequestType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profileRequestType).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfileRequestType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfileRequestType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfileRequestType', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfileRequestTypeToCollectionIfMissing', () => {
      it('should add a ProfileRequestType to an empty array', () => {
        const profileRequestType: IProfileRequestType = sampleWithRequiredData;
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing([], profileRequestType);
        expect(expectedResult).toEqual([profileRequestType]);
      });

      it('should not add a ProfileRequestType to an array that contains it', () => {
        const profileRequestType: IProfileRequestType = sampleWithRequiredData;
        const profileRequestTypeCollection: IProfileRequestType[] = [
          {
            ...profileRequestType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing(profileRequestTypeCollection, profileRequestType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfileRequestType to an array that doesn't contain it", () => {
        const profileRequestType: IProfileRequestType = sampleWithRequiredData;
        const profileRequestTypeCollection: IProfileRequestType[] = [sampleWithPartialData];
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing(profileRequestTypeCollection, profileRequestType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profileRequestType);
      });

      it('should add only unique ProfileRequestType to an array', () => {
        const profileRequestTypeArray: IProfileRequestType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profileRequestTypeCollection: IProfileRequestType[] = [sampleWithRequiredData];
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing(profileRequestTypeCollection, ...profileRequestTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profileRequestType: IProfileRequestType = sampleWithRequiredData;
        const profileRequestType2: IProfileRequestType = sampleWithPartialData;
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing([], profileRequestType, profileRequestType2);
        expect(expectedResult).toEqual([profileRequestType, profileRequestType2]);
      });

      it('should accept null and undefined values', () => {
        const profileRequestType: IProfileRequestType = sampleWithRequiredData;
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing([], null, profileRequestType, undefined);
        expect(expectedResult).toEqual([profileRequestType]);
      });

      it('should return initial array if no ProfileRequestType is added', () => {
        const profileRequestTypeCollection: IProfileRequestType[] = [sampleWithRequiredData];
        expectedResult = service.addProfileRequestTypeToCollectionIfMissing(profileRequestTypeCollection, undefined, null);
        expect(expectedResult).toEqual(profileRequestTypeCollection);
      });
    });

    describe('compareProfileRequestType', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfileRequestType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27058 };
        const entity2 = null;

        const compareResult1 = service.compareProfileRequestType(entity1, entity2);
        const compareResult2 = service.compareProfileRequestType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27058 };
        const entity2 = { id: 13100 };

        const compareResult1 = service.compareProfileRequestType(entity1, entity2);
        const compareResult2 = service.compareProfileRequestType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27058 };
        const entity2 = { id: 27058 };

        const compareResult1 = service.compareProfileRequestType(entity1, entity2);
        const compareResult2 = service.compareProfileRequestType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
