import dayjs from 'dayjs/esm';

import { IProfileCollectionSet, NewProfileCollectionSet } from './profile-collection-set.model';

export const sampleWithRequiredData: IProfileCollectionSet = {
  id: 11848,
};

export const sampleWithPartialData: IProfileCollectionSet = {
  id: 32133,
  owned: false,
};

export const sampleWithFullData: IProfileCollectionSet = {
  id: 31552,
  owned: false,
  wanted: false,
  dateAdded: dayjs('2026-03-31'),
};

export const sampleWithNewData: NewProfileCollectionSet = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
