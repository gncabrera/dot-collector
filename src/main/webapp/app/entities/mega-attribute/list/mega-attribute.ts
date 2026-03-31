import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { MegaAttributeDeleteDialog } from '../delete/mega-attribute-delete-dialog';
import { IMegaAttribute } from '../mega-attribute.model';
import { MegaAttributeService } from '../service/mega-attribute.service';

@Component({
  selector: 'jhi-mega-attribute',
  templateUrl: './mega-attribute.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaAttribute implements OnInit {
  subscription: Subscription | null = null;
  readonly megaAttributes = signal<IMegaAttribute[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaAttributeService = inject(MegaAttributeService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaAttributeService.megaAttributesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaAttributes.set(this.fillComponentAttributesFromResponseBody([...this.megaAttributeService.megaAttributes()]));
    });
  }

  trackId = (item: IMegaAttribute): number => this.megaAttributeService.getMegaAttributeIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaAttributes().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaAttribute: IMegaAttribute): void {
    const modalRef = this.modalService.open(MegaAttributeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaAttribute = megaAttribute;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IMegaAttribute[]): IMegaAttribute[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaAttribute[]): IMegaAttribute[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaAttributeService.megaAttributesParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
