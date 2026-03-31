import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaAttributeOption, NewMegaAttributeOption } from '../mega-attribute-option.model';

export type PartialUpdateMegaAttributeOption = Partial<IMegaAttributeOption> & Pick<IMegaAttributeOption, 'id'>;

@Injectable()
export class MegaAttributeOptionsService {
  readonly megaAttributeOptionsParams = signal<
    Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined
  >(undefined);
  readonly megaAttributeOptionsResource = httpResource<IMegaAttributeOption[]>(() => {
    const params = this.megaAttributeOptionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaAttributeOption that have been fetched. It is updated when the megaAttributeOptionsResource emits a new value.
   * In case of error while fetching the megaAttributeOptions, the signal is set to an empty array.
   */
  readonly megaAttributeOptions = computed(() =>
    this.megaAttributeOptionsResource.hasValue() ? this.megaAttributeOptionsResource.value() : [],
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-attribute-options');
}

@Injectable({ providedIn: 'root' })
export class MegaAttributeOptionService extends MegaAttributeOptionsService {
  protected readonly http = inject(HttpClient);

  create(megaAttributeOption: NewMegaAttributeOption): Observable<IMegaAttributeOption> {
    return this.http.post<IMegaAttributeOption>(this.resourceUrl, megaAttributeOption);
  }

  update(megaAttributeOption: IMegaAttributeOption): Observable<IMegaAttributeOption> {
    return this.http.put<IMegaAttributeOption>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaAttributeOptionIdentifier(megaAttributeOption))}`,
      megaAttributeOption,
    );
  }

  partialUpdate(megaAttributeOption: PartialUpdateMegaAttributeOption): Observable<IMegaAttributeOption> {
    return this.http.patch<IMegaAttributeOption>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaAttributeOptionIdentifier(megaAttributeOption))}`,
      megaAttributeOption,
    );
  }

  find(id: number): Observable<IMegaAttributeOption> {
    return this.http.get<IMegaAttributeOption>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaAttributeOption[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaAttributeOption[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaAttributeOptionIdentifier(megaAttributeOption: Pick<IMegaAttributeOption, 'id'>): number {
    return megaAttributeOption.id;
  }

  compareMegaAttributeOption(o1: Pick<IMegaAttributeOption, 'id'> | null, o2: Pick<IMegaAttributeOption, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaAttributeOptionIdentifier(o1) === this.getMegaAttributeOptionIdentifier(o2) : o1 === o2;
  }

  addMegaAttributeOptionToCollectionIfMissing<Type extends Pick<IMegaAttributeOption, 'id'>>(
    megaAttributeOptionCollection: Type[],
    ...megaAttributeOptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaAttributeOptions: Type[] = megaAttributeOptionsToCheck.filter(isPresent);
    if (megaAttributeOptions.length > 0) {
      const megaAttributeOptionCollectionIdentifiers = megaAttributeOptionCollection.map(megaAttributeOptionItem =>
        this.getMegaAttributeOptionIdentifier(megaAttributeOptionItem),
      );
      const megaAttributeOptionsToAdd = megaAttributeOptions.filter(megaAttributeOptionItem => {
        const megaAttributeOptionIdentifier = this.getMegaAttributeOptionIdentifier(megaAttributeOptionItem);
        if (megaAttributeOptionCollectionIdentifiers.includes(megaAttributeOptionIdentifier)) {
          return false;
        }
        megaAttributeOptionCollectionIdentifiers.push(megaAttributeOptionIdentifier);
        return true;
      });
      return [...megaAttributeOptionsToAdd, ...megaAttributeOptionCollection];
    }
    return megaAttributeOptionCollection;
  }
}
