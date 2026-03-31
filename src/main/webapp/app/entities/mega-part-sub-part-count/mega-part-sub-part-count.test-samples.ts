import { IMegaPartSubPartCount, NewMegaPartSubPartCount } from './mega-part-sub-part-count.model';

export const sampleWithRequiredData: IMegaPartSubPartCount = {
  id: 18971,
};

export const sampleWithPartialData: IMegaPartSubPartCount = {
  id: 1092,
  count: 30369,
};

export const sampleWithFullData: IMegaPartSubPartCount = {
  id: 23378,
  count: 31385,
};

export const sampleWithNewData: NewMegaPartSubPartCount = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
