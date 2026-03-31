export interface IEnvironmentVariable {
  id: number;
  key?: string | null;
  value?: string | null;
  description?: string | null;
  type?: string | null;
}

export type NewEnvironmentVariable = Omit<IEnvironmentVariable, 'id'> & { id: null };
