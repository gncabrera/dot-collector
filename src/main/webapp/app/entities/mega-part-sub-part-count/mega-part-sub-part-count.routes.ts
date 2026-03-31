import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import MegaPartSubPartCountResolve from './route/mega-part-sub-part-count-routing-resolve.service';

const megaPartSubPartCountRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/mega-part-sub-part-count').then(m => m.MegaPartSubPartCount),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/mega-part-sub-part-count-detail').then(m => m.MegaPartSubPartCountDetail),
    resolve: {
      megaPartSubPartCount: MegaPartSubPartCountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/mega-part-sub-part-count-update').then(m => m.MegaPartSubPartCountUpdate),
    resolve: {
      megaPartSubPartCount: MegaPartSubPartCountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/mega-part-sub-part-count-update').then(m => m.MegaPartSubPartCountUpdate),
    resolve: {
      megaPartSubPartCount: MegaPartSubPartCountResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default megaPartSubPartCountRoute;
