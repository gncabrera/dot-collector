export interface IProfileRequestType {
  id: number;
  key?: string | null;
  name?: string | null;
  description?: string | null;
}

export type NewProfileRequestType = Omit<IProfileRequestType, 'id'> & { id: null };
