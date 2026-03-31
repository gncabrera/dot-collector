import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaAssetResolve from './route/mega-asset-routing-resolve.service';

const megaAssetRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-asset').then(m => m.MegaAsset),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-asset-detail').then(m => m.MegaAssetDetail),
    resolve: {
      megaAsset: MegaAssetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-asset-update').then(m => m.MegaAssetUpdate),
    resolve: {
      megaAsset: MegaAssetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-asset-update').then(m => m.MegaAssetUpdate),
    resolve: {
      megaAsset: MegaAssetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaAssetRoute;
