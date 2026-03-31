import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaPartType, NewMegaPartType } from '../mega-part-type.model';

export type PartialUpdateMegaPartType = Partial<IMegaPartType> & Pick<IMegaPartType, 'id'>;

@Injectable()
export class MegaPartTypesService {
  readonly megaPartTypesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaPartTypesResource = httpResource<IMegaPartType[]>(() => {
    const params = this.megaPartTypesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaPartType that have been fetched. It is updated when the megaPartTypesResource emits a new value.
   * In case of error while fetching the megaPartTypes, the signal is set to an empty array.
   */
  readonly megaPartTypes = computed(() => (this.megaPartTypesResource.hasValue() ? this.megaPartTypesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-part-types');
}

@Injectable({ providedIn: 'root' })
export class MegaPartTypeService extends MegaPartTypesService {
  protected readonly http = inject(HttpClient);

  create(megaPartType: NewMegaPartType): Observable<IMegaPartType> {
    return this.http.post<IMegaPartType>(this.resourceUrl, megaPartType);
  }

  update(megaPartType: IMegaPartType): Observable<IMegaPartType> {
    return this.http.put<IMegaPartType>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaPartTypeIdentifier(megaPartType))}`,
      megaPartType,
    );
  }

  partialUpdate(megaPartType: PartialUpdateMegaPartType): Observable<IMegaPartType> {
    return this.http.patch<IMegaPartType>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaPartTypeIdentifier(megaPartType))}`,
      megaPartType,
    );
  }

  find(id: number): Observable<IMegaPartType> {
    return this.http.get<IMegaPartType>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaPartType[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaPartType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaPartTypeIdentifier(megaPartType: Pick<IMegaPartType, 'id'>): number {
    return megaPartType.id;
  }

  compareMegaPartType(o1: Pick<IMegaPartType, 'id'> | null, o2: Pick<IMegaPartType, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaPartTypeIdentifier(o1) === this.getMegaPartTypeIdentifier(o2) : o1 === o2;
  }

  addMegaPartTypeToCollectionIfMissing<Type extends Pick<IMegaPartType, 'id'>>(
    megaPartTypeCollection: Type[],
    ...megaPartTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaPartTypes: Type[] = megaPartTypesToCheck.filter(isPresent);
    if (megaPartTypes.length > 0) {
      const megaPartTypeCollectionIdentifiers = megaPartTypeCollection.map(megaPartTypeItem =>
        this.getMegaPartTypeIdentifier(megaPartTypeItem),
      );
      const megaPartTypesToAdd = megaPartTypes.filter(megaPartTypeItem => {
        const megaPartTypeIdentifier = this.getMegaPartTypeIdentifier(megaPartTypeItem);
        if (megaPartTypeCollectionIdentifiers.includes(megaPartTypeIdentifier)) {
          return false;
        }
        megaPartTypeCollectionIdentifiers.push(megaPartTypeIdentifier);
        return true;
      });
      return [...megaPartTypesToAdd, ...megaPartTypeCollection];
    }
    return megaPartTypeCollection;
  }
}
