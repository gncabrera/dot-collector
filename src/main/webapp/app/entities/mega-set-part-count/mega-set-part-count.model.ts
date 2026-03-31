import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { IMegaSet } from 'app/entities/mega-set/mega-set.model';

export interface IMegaSetPartCount {
  id: number;
  count?: number | null;
  set?: Pick<IMegaSet, 'id'> | null;
  part?: Pick<IMegaPart, 'id'> | null;
}

export type NewMegaSetPartCount = Omit<IMegaSetPartCount, 'id'> & { id: null };
