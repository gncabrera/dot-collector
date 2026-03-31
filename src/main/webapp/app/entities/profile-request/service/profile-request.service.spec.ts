import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IProfileRequest } from '../profile-request.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../profile-request.test-samples';

import { ProfileRequestService } from './profile-request.service';

const requireRestSample: IProfileRequest = {
  ...sampleWithRequiredData,
};

describe('ProfileRequest Service', () => {
  let service: ProfileRequestService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfileRequest | IProfileRequest[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ProfileRequestService);
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

    it('should create a ProfileRequest', () => {
      const profileRequest = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profileRequest).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProfileRequest', () => {
      const profileRequest = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profileRequest).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProfileRequest', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProfileRequest', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProfileRequest', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addProfileRequestToCollectionIfMissing', () => {
      it('should add a ProfileRequest to an empty array', () => {
        const profileRequest: IProfileRequest = sampleWithRequiredData;
        expectedResult = service.addProfileRequestToCollectionIfMissing([], profileRequest);
        expect(expectedResult).toEqual([profileRequest]);
      });

      it('should not add a ProfileRequest to an array that contains it', () => {
        const profileRequest: IProfileRequest = sampleWithRequiredData;
        const profileRequestCollection: IProfileRequest[] = [
          {
            ...profileRequest,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfileRequestToCollectionIfMissing(profileRequestCollection, profileRequest);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProfileRequest to an array that doesn't contain it", () => {
        const profileRequest: IProfileRequest = sampleWithRequiredData;
        const profileRequestCollection: IProfileRequest[] = [sampleWithPartialData];
        expectedResult = service.addProfileRequestToCollectionIfMissing(profileRequestCollection, profileRequest);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profileRequest);
      });

      it('should add only unique ProfileRequest to an array', () => {
        const profileRequestArray: IProfileRequest[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profileRequestCollection: IProfileRequest[] = [sampleWithRequiredData];
        expectedResult = service.addProfileRequestToCollectionIfMissing(profileRequestCollection, ...profileRequestArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profileRequest: IProfileRequest = sampleWithRequiredData;
        const profileRequest2: IProfileRequest = sampleWithPartialData;
        expectedResult = service.addProfileRequestToCollectionIfMissing([], profileRequest, profileRequest2);
        expect(expectedResult).toEqual([profileRequest, profileRequest2]);
      });

      it('should accept null and undefined values', () => {
        const profileRequest: IProfileRequest = sampleWithRequiredData;
        expectedResult = service.addProfileRequestToCollectionIfMissing([], null, profileRequest, undefined);
        expect(expectedResult).toEqual([profileRequest]);
      });

      it('should return initial array if no ProfileRequest is added', () => {
        const profileRequestCollection: IProfileRequest[] = [sampleWithRequiredData];
        expectedResult = service.addProfileRequestToCollectionIfMissing(profileRequestCollection, undefined, null);
        expect(expectedResult).toEqual(profileRequestCollection);
      });
    });

    describe('compareProfileRequest', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfileRequest(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 10331 };
        const entity2 = null;

        const compareResult1 = service.compareProfileRequest(entity1, entity2);
        const compareResult2 = service.compareProfileRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 10331 };
        const entity2 = { id: 11236 };

        const compareResult1 = service.compareProfileRequest(entity1, entity2);
        const compareResult2 = service.compareProfileRequest(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 10331 };
        const entity2 = { id: 10331 };

        const compareResult1 = service.compareProfileRequest(entity1, entity2);
        const compareResult2 = service.compareProfileRequest(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
