import {InjectionToken} from '@angular/core';

export interface Configuration {
    production: boolean;
}

export const Config = new InjectionToken<Configuration>('Config');
