import { IMegaPart } from 'app/entities/mega-part/mega-part.model';

export interface IMegaPartSubPartCount {
  id: number;
  count?: number | null;
  part?: Pick<IMegaPart, 'id'> | null;
  parentPart?: Pick<IMegaPart, 'id'> | null;
}

export type NewMegaPartSubPartCount = Omit<IMegaPartSubPartCount, 'id'> & { id: null };
