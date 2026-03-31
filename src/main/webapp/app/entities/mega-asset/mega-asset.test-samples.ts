import { IMegaAsset, NewMegaAsset } from './mega-asset.model';

export const sampleWithRequiredData: IMegaAsset = {
  id: 13147,
  name: 'issue unwilling frilly',
  path: 'insignificant fast what',
};

export const sampleWithPartialData: IMegaAsset = {
  id: 8734,
  name: 'gee factorise unless',
  path: 'home astride fumigate',
  type: 'IMAGE',
};

export const sampleWithFullData: IMegaAsset = {
  id: 30147,
  name: 'instructive brr',
  description: 'hmph phooey monasticism',
  path: 'violin',
  type: 'IMAGE',
};

export const sampleWithNewData: NewMegaAsset = {
  name: 'furthermore thyme',
  path: 'times till hmph',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
