import { IPartSubCategory, NewPartSubCategory } from './part-sub-category.model';

export const sampleWithRequiredData: IPartSubCategory = {
  id: 32083,
  name: 'rectangular',
};

export const sampleWithPartialData: IPartSubCategory = {
  id: 31172,
  name: 'phooey',
};

export const sampleWithFullData: IPartSubCategory = {
  id: 24769,
  name: 'jagged near',
  description: 'evenly where',
};

export const sampleWithNewData: NewPartSubCategory = {
  name: 'foolishly jagged',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
