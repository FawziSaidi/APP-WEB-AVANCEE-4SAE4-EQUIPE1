import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { BackofficeRoutingModule } from './backoffice-routing.module';

import { HeaderComponent } from './components/header/header.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { FooterComponent } from './components/footer/footer.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { BackofficeLayoutComponent } from './backoffice-layout.component';
import { GestionForumComponent } from './GestionForum/gestion-forum.component';
import { AdminProjectsComponent } from './components/admin-projects/admin-projects.component';

@NgModule({
  declarations: [
    BackofficeLayoutComponent,
    HeaderComponent,
    SidebarComponent,
    FooterComponent,
    DashboardComponent,
    GestionForumComponent,
    AdminProjectsComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    HttpClientModule,
    RouterModule,
    BackofficeRoutingModule
  ]
})
export class BackofficeModule { }