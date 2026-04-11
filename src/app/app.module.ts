import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app.routing';
import { ComponentsModule } from './frontoffice/components.module';
import { AppComponent } from './app.component';
import { UserLayoutComponent } from './frontoffice/user-layout/user-layout.component';
import { LandingModule } from './authentification/landing/landing.module';
import { AuthModule } from './authentification/auth/auth.module';

// ✅ KEYCLOAK : intercepteur qui injecte le Bearer token dans chaque requête
import { AuthInterceptor } from './services/auth.interceptor';

@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    ComponentsModule,
    RouterModule,
    AppRoutingModule,
    LandingModule,
    AuthModule,
  ],
  declarations: [
    AppComponent,
    UserLayoutComponent,
  ],
  providers: [
    // ✅ KEYCLOAK : enregistrement de l'intercepteur HTTP
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }