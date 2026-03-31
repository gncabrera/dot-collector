import { IMegaPartType, NewMegaPartType } from './mega-part-type.model';

export const sampleWithRequiredData: IMegaPartType = {
  id: 15704,
  name: 'now likely',
  version: 3572,
};

export const sampleWithPartialData: IMegaPartType = {
  id: 11567,
  name: 'than piglet yowza',
  version: 868,
  active: false,
  isLatest: true,
};

export const sampleWithFullData: IMegaPartType = {
  id: 26575,
  name: 'unique',
  version: 14907,
  active: false,
  isLatest: false,
};

export const sampleWithNewData: NewMegaPartType = {
  name: 'above',
  version: 21208,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
