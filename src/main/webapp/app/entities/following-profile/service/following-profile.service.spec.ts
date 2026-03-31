import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IFollowingProfile } from '../following-profile.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../following-profile.test-samples';

import { FollowingProfileService, RestFollowingProfile } from './following-profile.service';

const requireRestSample: RestFollowingProfile = {
  ...sampleWithRequiredData,
  dateFollowing: sampleWithRequiredData.dateFollowing?.format(DATE_FORMAT),
};

describe('FollowingProfile Service', () => {
  let service: FollowingProfileService;
  let httpMock: HttpTestingController;
  let expectedResult: IFollowingProfile | IFollowingProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(FollowingProfileService);
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

    it('should create a FollowingProfile', () => {
      const followingProfile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(followingProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a FollowingProfile', () => {
      const followingProfile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(followingProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a FollowingProfile', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of FollowingProfile', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a FollowingProfile', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addFollowingProfileToCollectionIfMissing', () => {
      it('should add a FollowingProfile to an empty array', () => {
        const followingProfile: IFollowingProfile = sampleWithRequiredData;
        expectedResult = service.addFollowingProfileToCollectionIfMissing([], followingProfile);
        expect(expectedResult).toEqual([followingProfile]);
      });

      it('should not add a FollowingProfile to an array that contains it', () => {
        const followingProfile: IFollowingProfile = sampleWithRequiredData;
        const followingProfileCollection: IFollowingProfile[] = [
          {
            ...followingProfile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFollowingProfileToCollectionIfMissing(followingProfileCollection, followingProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a FollowingProfile to an array that doesn't contain it", () => {
        const followingProfile: IFollowingProfile = sampleWithRequiredData;
        const followingProfileCollection: IFollowingProfile[] = [sampleWithPartialData];
        expectedResult = service.addFollowingProfileToCollectionIfMissing(followingProfileCollection, followingProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(followingProfile);
      });

      it('should add only unique FollowingProfile to an array', () => {
        const followingProfileArray: IFollowingProfile[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const followingProfileCollection: IFollowingProfile[] = [sampleWithRequiredData];
        expectedResult = service.addFollowingProfileToCollectionIfMissing(followingProfileCollection, ...followingProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const followingProfile: IFollowingProfile = sampleWithRequiredData;
        const followingProfile2: IFollowingProfile = sampleWithPartialData;
        expectedResult = service.addFollowingProfileToCollectionIfMissing([], followingProfile, followingProfile2);
        expect(expectedResult).toEqual([followingProfile, followingProfile2]);
      });

      it('should accept null and undefined values', () => {
        const followingProfile: IFollowingProfile = sampleWithRequiredData;
        expectedResult = service.addFollowingProfileToCollectionIfMissing([], null, followingProfile, undefined);
        expect(expectedResult).toEqual([followingProfile]);
      });

      it('should return initial array if no FollowingProfile is added', () => {
        const followingProfileCollection: IFollowingProfile[] = [sampleWithRequiredData];
        expectedResult = service.addFollowingProfileToCollectionIfMissing(followingProfileCollection, undefined, null);
        expect(expectedResult).toEqual(followingProfileCollection);
      });
    });

    describe('compareFollowingProfile', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFollowingProfile(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 29154 };
        const entity2 = null;

        const compareResult1 = service.compareFollowingProfile(entity1, entity2);
        const compareResult2 = service.compareFollowingProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 29154 };
        const entity2 = { id: 26929 };

        const compareResult1 = service.compareFollowingProfile(entity1, entity2);
        const compareResult2 = service.compareFollowingProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 29154 };
        const entity2 = { id: 29154 };

        const compareResult1 = service.compareFollowingProfile(entity1, entity2);
        const compareResult2 = service.compareFollowingProfile(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
