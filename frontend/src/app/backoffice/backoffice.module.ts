import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
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
import { AdminStatsComponent } from './components/admin-stats/admin-stats.component';
import { AdminEventsComponent } from './components/admin-evenement/admin-event.component';
import { ActivityFormComponent } from './components/activity-form/activity-form-component';
import { EventFormComponent } from './components/event-form/event-form.component';
import { ParticipantBadgeComponent } from './components/participant-badge/participant-badge.component';
import { AdminCoursComponent } from './cours/admin-cours.component'; // ← ADD THIS IMPORT

@NgModule({
  declarations: [
    BackofficeLayoutComponent,
    HeaderComponent,
    SidebarComponent,
    FooterComponent,
    DashboardComponent,
    GestionForumComponent,
    AdminProjectsComponent,
    AdminStatsComponent,
    AdminEventsComponent,
    ActivityFormComponent,
    EventFormComponent,
    ParticipantBadgeComponent,
    AdminCoursComponent // ← ADD THIS HERE
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule, // This is already here - good!
    HttpClientModule,
    RouterModule,
    BackofficeRoutingModule
  ]
})
export class BackofficeModule { }