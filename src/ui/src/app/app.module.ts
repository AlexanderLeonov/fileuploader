import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {library} from '@fortawesome/fontawesome-svg-core';
import {faDownload, faTrash} from '@fortawesome/free-solid-svg-icons';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app-root/app.component';
import {FilePickerComponent} from './file-picker/file-picker.component';
import {Config} from './shared/config';
import {environment} from '../environments/environment';
import {FileListComponent} from './file-list/file-list.component';
import {FileItemComponent} from './file-item/file-item.component';
import {TotalFileSizePipe} from './total-file-size/total-file-size.pipe';
import { ShowErrorComponent } from './show-error/show-error.component';

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        FontAwesomeModule,
        AppRoutingModule
    ],
    declarations: [
        AppComponent,
        FilePickerComponent,
        FileListComponent,
        FileItemComponent,
        TotalFileSizePipe,
        ShowErrorComponent
    ],
    providers: [
        {provide: Config, useValue: environment}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
    constructor() {
        library.add(faDownload, faTrash);
    }
}
