import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaSetTypeResolve from './route/mega-set-type-routing-resolve.service';

const megaSetTypeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-set-type').then(m => m.MegaSetType),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-set-type-detail').then(m => m.MegaSetTypeDetail),
    resolve: {
      megaSetType: MegaSetTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-set-type-update').then(m => m.MegaSetTypeUpdate),
    resolve: {
      megaSetType: MegaSetTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-set-type-update').then(m => m.MegaSetTypeUpdate),
    resolve: {
      megaSetType: MegaSetTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaSetTypeRoute;
