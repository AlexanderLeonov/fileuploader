import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {ViewFile} from '../shared/viewFile';

@Component({
    selector: 'app-file-item',
    templateUrl: './file-item.component.html',
    styleUrls: ['./file-item.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FileItemComponent {

    @Input()
    file: ViewFile;

    @Output()
    deleteFile = new EventEmitter();

}
