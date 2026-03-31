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
import { MegaSetTypeDeleteDialog } from '../delete/mega-set-type-delete-dialog';
import { IMegaSetType } from '../mega-set-type.model';
import { MegaSetTypeService } from '../service/mega-set-type.service';

@Component({
  selector: 'jhi-mega-set-type',
  templateUrl: './mega-set-type.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaSetType implements OnInit {
  subscription: Subscription | null = null;
  readonly megaSetTypes = signal<IMegaSetType[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaSetTypeService = inject(MegaSetTypeService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaSetTypeService.megaSetTypesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaSetTypes.set(this.fillComponentAttributesFromResponseBody([...this.megaSetTypeService.megaSetTypes()]));
    });
  }

  trackId = (item: IMegaSetType): number => this.megaSetTypeService.getMegaSetTypeIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaSetTypes().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaSetType: IMegaSetType): void {
    const modalRef = this.modalService.open(MegaSetTypeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaSetType = megaSetType;
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

  protected refineData(data: IMegaSetType[]): IMegaSetType[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaSetType[]): IMegaSetType[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaSetTypeService.megaSetTypesParams.set(queryObject);
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
