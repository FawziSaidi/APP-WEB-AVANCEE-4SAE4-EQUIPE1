import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BackofficeLayoutComponent } from './backoffice-layout.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { GestionForumComponent } from './GestionForum/gestion-forum.component';
import { AdminProjectsComponent } from './components/admin-projects/admin-projects.component';

const routes: Routes = [
  {
    path: '',
    component: BackofficeLayoutComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'forum', component: GestionForumComponent },
      { path: 'projects', component: AdminProjectsComponent },
      
      // Module Subscription (lazy loaded)
      {
        path: 'subscription',
        loadChildren: () =>
          import('./subscriptions/subscriptions.module').then(m => m.SubscriptionsModule)
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BackofficeRoutingModule {}