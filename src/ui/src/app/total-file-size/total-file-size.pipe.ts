import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'totalFileSize' // can't be pure since value can stay the same but its content may change
})
export class TotalFileSizePipe implements PipeTransform {

    transform(value: File[]): number {
        return value.reduce((sz, file) => sz + file.size, 0);
    }

}
