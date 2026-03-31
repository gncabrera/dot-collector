import { IPartCategory, NewPartCategory } from './part-category.model';

export const sampleWithRequiredData: IPartCategory = {
  id: 4401,
  name: 'frantically bar redesign',
};

export const sampleWithPartialData: IPartCategory = {
  id: 4021,
  name: 'hmph meanwhile',
  description: 'musty shameless',
};

export const sampleWithFullData: IPartCategory = {
  id: 18267,
  name: 'slime ah',
  description: 'stable psst',
};

export const sampleWithNewData: NewPartCategory = {
  name: 'orient yet heating',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
