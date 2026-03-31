import { IMegaSetType, NewMegaSetType } from './mega-set-type.model';

export const sampleWithRequiredData: IMegaSetType = {
  id: 10199,
  name: 'potable',
  version: 16276,
};

export const sampleWithPartialData: IMegaSetType = {
  id: 20822,
  name: 'ah except contractor',
  version: 228,
  isLatest: false,
};

export const sampleWithFullData: IMegaSetType = {
  id: 12811,
  name: 'wallop apricot',
  version: 29973,
  active: false,
  isLatest: true,
};

export const sampleWithNewData: NewMegaSetType = {
  name: 'innocently tricky',
  version: 16827,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
