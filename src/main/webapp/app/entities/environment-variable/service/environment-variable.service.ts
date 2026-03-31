import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IEnvironmentVariable, NewEnvironmentVariable } from '../environment-variable.model';

export type PartialUpdateEnvironmentVariable = Partial<IEnvironmentVariable> & Pick<IEnvironmentVariable, 'id'>;

@Injectable()
export class EnvironmentVariablesService {
  readonly environmentVariablesParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly environmentVariablesResource = httpResource<IEnvironmentVariable[]>(() => {
    const params = this.environmentVariablesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of environmentVariable that have been fetched. It is updated when the environmentVariablesResource emits a new value.
   * In case of error while fetching the environmentVariables, the signal is set to an empty array.
   */
  readonly environmentVariables = computed(() =>
    this.environmentVariablesResource.hasValue() ? this.environmentVariablesResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/environment-variables');
}

@Injectable({ providedIn: 'root' })
export class EnvironmentVariableService extends EnvironmentVariablesService {
  protected readonly http = inject(HttpClient);

  create(environmentVariable: NewEnvironmentVariable): Observable<IEnvironmentVariable> {
    return this.http.post<IEnvironmentVariable>(this.resourceUrl, environmentVariable);
  }

  update(environmentVariable: IEnvironmentVariable): Observable<IEnvironmentVariable> {
    return this.http.put<IEnvironmentVariable>(
      `${this.resourceUrl}/${encodeURIComponent(this.getEnvironmentVariableIdentifier(environmentVariable))}`,
      environmentVariable,
    );
  }

  partialUpdate(environmentVariable: PartialUpdateEnvironmentVariable): Observable<IEnvironmentVariable> {
    return this.http.patch<IEnvironmentVariable>(
      `${this.resourceUrl}/${encodeURIComponent(this.getEnvironmentVariableIdentifier(environmentVariable))}`,
      environmentVariable,
    );
  }

  find(id: number): Observable<IEnvironmentVariable> {
    return this.http.get<IEnvironmentVariable>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IEnvironmentVariable[]>> {
    const options = createRequestOption(req);
    return this.http.get<IEnvironmentVariable[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getEnvironmentVariableIdentifier(environmentVariable: Pick<IEnvironmentVariable, 'id'>): number {
    return environmentVariable.id;
  }

  compareEnvironmentVariable(o1: Pick<IEnvironmentVariable, 'id'> | null, o2: Pick<IEnvironmentVariable, 'id'> | null): boolean {
    return o1 && o2 ? this.getEnvironmentVariableIdentifier(o1) === this.getEnvironmentVariableIdentifier(o2) : o1 === o2;
  }

  addEnvironmentVariableToCollectionIfMissing<Type extends Pick<IEnvironmentVariable, 'id'>>(
    environmentVariableCollection: Type[],
    ...environmentVariablesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const environmentVariables: Type[] = environmentVariablesToCheck.filter(isPresent);
    if (environmentVariables.length > 0) {
      const environmentVariableCollectionIdentifiers = environmentVariableCollection.map(environmentVariableItem =>
        this.getEnvironmentVariableIdentifier(environmentVariableItem),
      );
      const environmentVariablesToAdd = environmentVariables.filter(environmentVariableItem => {
        const environmentVariableIdentifier = this.getEnvironmentVariableIdentifier(environmentVariableItem);
        if (environmentVariableCollectionIdentifiers.includes(environmentVariableIdentifier)) {
          return false;
        }
        environmentVariableCollectionIdentifiers.push(environmentVariableIdentifier);
        return true;
      });
      return [...environmentVariablesToAdd, ...environmentVariableCollection];
    }
    return environmentVariableCollection;
  }
}
