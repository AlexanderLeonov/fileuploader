import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    ElementRef,
    EventEmitter,
    Output, Renderer2,
    ViewChild
} from '@angular/core';
import {fileListToArray} from '../shared/common';

@Component({
    selector: 'app-file-picker',
    templateUrl: './file-picker.component.html',
    styleUrls: ['./file-picker.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FilePickerComponent {

    @ViewChild('fileInput')
    fileInput: ElementRef;

    @Output()
    submitFiles = new EventEmitter();

    constructor(private cdr: ChangeDetectorRef, private renderer: Renderer2) {
    }

    private filesInt: File[] = [] as any; // FileList cannot be created, thus using Array as substitution

    get files(): File[] {
        return this.filesInt;
    }

    onChange(files: FileList) {
        this.filesInt = fileListToArray(files);
    }

    onSubmit(event: Event) {
        event.preventDefault();
        if (this.filesInt.length) {
            this.submitFiles.emit(this.filesInt);
        }
    }

    clear() {
        this.renderer.setProperty(this.fileInput.nativeElement, 'value', '');
        this.filesInt = [];
        this.cdr.markForCheck();
    }

}
