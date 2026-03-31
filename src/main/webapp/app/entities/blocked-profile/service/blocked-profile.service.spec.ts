import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IBlockedProfile } from '../blocked-profile.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../blocked-profile.test-samples';

import { BlockedProfileService, RestBlockedProfile } from './blocked-profile.service';

const requireRestSample: RestBlockedProfile = {
  ...sampleWithRequiredData,
  dateBlocked: sampleWithRequiredData.dateBlocked?.format(DATE_FORMAT),
};

describe('BlockedProfile Service', () => {
  let service: BlockedProfileService;
  let httpMock: HttpTestingController;
  let expectedResult: IBlockedProfile | IBlockedProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(BlockedProfileService);
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

    it('should create a BlockedProfile', () => {
      const blockedProfile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(blockedProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BlockedProfile', () => {
      const blockedProfile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(blockedProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BlockedProfile', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BlockedProfile', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BlockedProfile', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addBlockedProfileToCollectionIfMissing', () => {
      it('should add a BlockedProfile to an empty array', () => {
        const blockedProfile: IBlockedProfile = sampleWithRequiredData;
        expectedResult = service.addBlockedProfileToCollectionIfMissing([], blockedProfile);
        expect(expectedResult).toEqual([blockedProfile]);
      });

      it('should not add a BlockedProfile to an array that contains it', () => {
        const blockedProfile: IBlockedProfile = sampleWithRequiredData;
        const blockedProfileCollection: IBlockedProfile[] = [
          {
            ...blockedProfile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBlockedProfileToCollectionIfMissing(blockedProfileCollection, blockedProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BlockedProfile to an array that doesn't contain it", () => {
        const blockedProfile: IBlockedProfile = sampleWithRequiredData;
        const blockedProfileCollection: IBlockedProfile[] = [sampleWithPartialData];
        expectedResult = service.addBlockedProfileToCollectionIfMissing(blockedProfileCollection, blockedProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(blockedProfile);
      });

      it('should add only unique BlockedProfile to an array', () => {
        const blockedProfileArray: IBlockedProfile[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const blockedProfileCollection: IBlockedProfile[] = [sampleWithRequiredData];
        expectedResult = service.addBlockedProfileToCollectionIfMissing(blockedProfileCollection, ...blockedProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const blockedProfile: IBlockedProfile = sampleWithRequiredData;
        const blockedProfile2: IBlockedProfile = sampleWithPartialData;
        expectedResult = service.addBlockedProfileToCollectionIfMissing([], blockedProfile, blockedProfile2);
        expect(expectedResult).toEqual([blockedProfile, blockedProfile2]);
      });

      it('should accept null and undefined values', () => {
        const blockedProfile: IBlockedProfile = sampleWithRequiredData;
        expectedResult = service.addBlockedProfileToCollectionIfMissing([], null, blockedProfile, undefined);
        expect(expectedResult).toEqual([blockedProfile]);
      });

      it('should return initial array if no BlockedProfile is added', () => {
        const blockedProfileCollection: IBlockedProfile[] = [sampleWithRequiredData];
        expectedResult = service.addBlockedProfileToCollectionIfMissing(blockedProfileCollection, undefined, null);
        expect(expectedResult).toEqual(blockedProfileCollection);
      });
    });

    describe('compareBlockedProfile', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBlockedProfile(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20306 };
        const entity2 = null;

        const compareResult1 = service.compareBlockedProfile(entity1, entity2);
        const compareResult2 = service.compareBlockedProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20306 };
        const entity2 = { id: 9787 };

        const compareResult1 = service.compareBlockedProfile(entity1, entity2);
        const compareResult2 = service.compareBlockedProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20306 };
        const entity2 = { id: 20306 };

        const compareResult1 = service.compareBlockedProfile(entity1, entity2);
        const compareResult2 = service.compareBlockedProfile(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
