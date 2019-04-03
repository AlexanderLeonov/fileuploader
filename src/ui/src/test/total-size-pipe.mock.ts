import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'totalFileSize'})
export class TotalSizePipeMock implements PipeTransform {
    transform(value: number): number {
        return value;
    }
}
