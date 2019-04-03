import {Component, ViewChild} from '@angular/core';
import {Subject} from 'rxjs';
import {startWith, switchMap} from 'rxjs/operators';
import {FileService} from '../file-service/file.service';
import {FilePickerComponent} from '../file-picker/file-picker.component';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent {

    private update = new Subject<void>();

    @ViewChild(FilePickerComponent)
    filePicker: FilePickerComponent;

    fileList$ = this.update.pipe(
        startWith(null),
        switchMap(() => this.fileService.getFileList())
    );

    isErrorVisible: boolean;
    error: string;

    constructor(private fileService: FileService) {
    }

    onSubmit(files: File[]) {
        this.fileService.upload(files).subscribe(
            () => {
                this.update.next();
                this.filePicker.clear();
            },
            err => {
                this.showError(err);
            });
    }

    deleteFile(file: string) {
        this.fileService.delete(file).subscribe(
            () => {
                this.update.next();
            },
            response => {
                this.showError(response);
            });
    }

    private showError(response: any) {
        this.error = response.error.message || response.message;
        this.isErrorVisible = true;
        setTimeout(() => {
            this.isErrorVisible = false;
        }, 5000);
    }

}
