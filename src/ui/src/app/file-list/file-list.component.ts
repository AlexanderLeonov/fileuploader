import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ViewFile} from '../shared/viewFile';

@Component({
    selector: 'app-file-list',
    templateUrl: './file-list.component.html',
    styleUrls: ['./file-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileListComponent {

    @Input()
    files: ViewFile[] = [];

    @Output()
    deleteFile = new EventEmitter();

}
