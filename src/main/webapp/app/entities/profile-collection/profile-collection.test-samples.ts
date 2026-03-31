import { IProfileCollection, NewProfileCollection } from './profile-collection.model';

export const sampleWithRequiredData: IProfileCollection = {
  id: 16298,
};

export const sampleWithPartialData: IProfileCollection = {
  id: 10750,
  title: 'fabricate yin',
  isPublic: false,
};

export const sampleWithFullData: IProfileCollection = {
  id: 1606,
  title: 'uh-huh apud and',
  description: 'brandish including summer',
  isPublic: false,
};

export const sampleWithNewData: NewProfileCollection = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
