import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IEnvironmentVariable } from '../environment-variable.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../environment-variable.test-samples';

import { EnvironmentVariableService } from './environment-variable.service';

const requireRestSample: IEnvironmentVariable = {
  ...sampleWithRequiredData,
};

describe('EnvironmentVariable Service', () => {
  let service: EnvironmentVariableService;
  let httpMock: HttpTestingController;
  let expectedResult: IEnvironmentVariable | IEnvironmentVariable[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(EnvironmentVariableService);
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

    it('should create a EnvironmentVariable', () => {
      const environmentVariable = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(environmentVariable).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a EnvironmentVariable', () => {
      const environmentVariable = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(environmentVariable).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a EnvironmentVariable', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of EnvironmentVariable', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a EnvironmentVariable', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addEnvironmentVariableToCollectionIfMissing', () => {
      it('should add a EnvironmentVariable to an empty array', () => {
        const environmentVariable: IEnvironmentVariable = sampleWithRequiredData;
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing([], environmentVariable);
        expect(expectedResult).toEqual([environmentVariable]);
      });

      it('should not add a EnvironmentVariable to an array that contains it', () => {
        const environmentVariable: IEnvironmentVariable = sampleWithRequiredData;
        const environmentVariableCollection: IEnvironmentVariable[] = [
          {
            ...environmentVariable,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing(environmentVariableCollection, environmentVariable);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a EnvironmentVariable to an array that doesn't contain it", () => {
        const environmentVariable: IEnvironmentVariable = sampleWithRequiredData;
        const environmentVariableCollection: IEnvironmentVariable[] = [sampleWithPartialData];
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing(environmentVariableCollection, environmentVariable);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(environmentVariable);
      });

      it('should add only unique EnvironmentVariable to an array', () => {
        const environmentVariableArray: IEnvironmentVariable[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const environmentVariableCollection: IEnvironmentVariable[] = [sampleWithRequiredData];
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing(environmentVariableCollection, ...environmentVariableArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const environmentVariable: IEnvironmentVariable = sampleWithRequiredData;
        const environmentVariable2: IEnvironmentVariable = sampleWithPartialData;
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing([], environmentVariable, environmentVariable2);
        expect(expectedResult).toEqual([environmentVariable, environmentVariable2]);
      });

      it('should accept null and undefined values', () => {
        const environmentVariable: IEnvironmentVariable = sampleWithRequiredData;
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing([], null, environmentVariable, undefined);
        expect(expectedResult).toEqual([environmentVariable]);
      });

      it('should return initial array if no EnvironmentVariable is added', () => {
        const environmentVariableCollection: IEnvironmentVariable[] = [sampleWithRequiredData];
        expectedResult = service.addEnvironmentVariableToCollectionIfMissing(environmentVariableCollection, undefined, null);
        expect(expectedResult).toEqual(environmentVariableCollection);
      });
    });

    describe('compareEnvironmentVariable', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareEnvironmentVariable(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 22355 };
        const entity2 = null;

        const compareResult1 = service.compareEnvironmentVariable(entity1, entity2);
        const compareResult2 = service.compareEnvironmentVariable(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 22355 };
        const entity2 = { id: 8466 };

        const compareResult1 = service.compareEnvironmentVariable(entity1, entity2);
        const compareResult2 = service.compareEnvironmentVariable(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 22355 };
        const entity2 = { id: 22355 };

        const compareResult1 = service.compareEnvironmentVariable(entity1, entity2);
        const compareResult2 = service.compareEnvironmentVariable(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
