import { IMegaSetPartCount, NewMegaSetPartCount } from './mega-set-part-count.model';

export const sampleWithRequiredData: IMegaSetPartCount = {
  id: 21108,
};

export const sampleWithPartialData: IMegaSetPartCount = {
  id: 17296,
};

export const sampleWithFullData: IMegaSetPartCount = {
  id: 12631,
  count: 5685,
};

export const sampleWithNewData: NewMegaSetPartCount = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
