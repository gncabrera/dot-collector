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
import { MegaAttributeOptionDeleteDialog } from '../delete/mega-attribute-option-delete-dialog';
import { IMegaAttributeOption } from '../mega-attribute-option.model';
import { MegaAttributeOptionService } from '../service/mega-attribute-option.service';

@Component({
  selector: 'jhi-mega-attribute-option',
  templateUrl: './mega-attribute-option.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaAttributeOption implements OnInit {
  subscription: Subscription | null = null;
  readonly megaAttributeOptions = signal<IMegaAttributeOption[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaAttributeOptionService = inject(MegaAttributeOptionService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaAttributeOptionService.megaAttributeOptionsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaAttributeOptions.set(
        this.fillComponentAttributesFromResponseBody([...this.megaAttributeOptionService.megaAttributeOptions()]),
      );
    });
  }

  trackId = (item: IMegaAttributeOption): number => this.megaAttributeOptionService.getMegaAttributeOptionIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaAttributeOptions().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaAttributeOption: IMegaAttributeOption): void {
    const modalRef = this.modalService.open(MegaAttributeOptionDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaAttributeOption = megaAttributeOption;
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

  protected refineData(data: IMegaAttributeOption[]): IMegaAttributeOption[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaAttributeOption[]): IMegaAttributeOption[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaAttributeOptionService.megaAttributeOptionsParams.set(queryObject);
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
