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
import { MegaAssetDeleteDialog } from '../delete/mega-asset-delete-dialog';
import { IMegaAsset } from '../mega-asset.model';
import { MegaAssetService } from '../service/mega-asset.service';

@Component({
  selector: 'jhi-mega-asset',
  templateUrl: './mega-asset.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class MegaAsset implements OnInit {
  subscription: Subscription | null = null;
  readonly megaAssets = signal<IMegaAsset[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly megaAssetService = inject(MegaAssetService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.megaAssetService.megaAssetsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.megaAssets.set(this.fillComponentAttributesFromResponseBody([...this.megaAssetService.megaAssets()]));
    });
  }

  trackId = (item: IMegaAsset): number => this.megaAssetService.getMegaAssetIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.megaAssets().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(megaAsset: IMegaAsset): void {
    const modalRef = this.modalService.open(MegaAssetDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.megaAsset = megaAsset;
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

  protected refineData(data: IMegaAsset[]): IMegaAsset[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IMegaAsset[]): IMegaAsset[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.megaAssetService.megaAssetsParams.set(queryObject);
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
