import {
  ApplicationConfig,
  DEFAULT_CURRENCY_CODE,
  LOCALE_ID,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import localeAT from '@angular/common/locales/de-AT';
import localeATExtra from '@angular/common/locales/extra/de-AT';
import { registerLocaleData } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';

registerLocaleData(localeAT, 'de-AT', localeATExtra);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    { provide: LOCALE_ID, useValue: 'de-AT' },
    { provide: DEFAULT_CURRENCY_CODE, useValue: 'EUR' },
  ],
};
