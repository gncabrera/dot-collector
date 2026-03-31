import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaSetPartCountResolve from './route/mega-set-part-count-routing-resolve.service';

const megaSetPartCountRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-set-part-count').then(m => m.MegaSetPartCount),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-set-part-count-detail').then(m => m.MegaSetPartCountDetail),
    resolve: {
      megaSetPartCount: MegaSetPartCountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-set-part-count-update').then(m => m.MegaSetPartCountUpdate),
    resolve: {
      megaSetPartCount: MegaSetPartCountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-set-part-count-update').then(m => m.MegaSetPartCountUpdate),
    resolve: {
      megaSetPartCount: MegaSetPartCountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaSetPartCountRoute;
