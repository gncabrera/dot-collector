import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaSetResolve from './route/mega-set-routing-resolve.service';

const megaSetRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-set').then(m => m.MegaSet),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-set-detail').then(m => m.MegaSetDetail),
    resolve: {
      megaSet: MegaSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-set-update').then(m => m.MegaSetUpdate),
    resolve: {
      megaSet: MegaSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-set-update').then(m => m.MegaSetUpdate),
    resolve: {
      megaSet: MegaSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaSetRoute;
