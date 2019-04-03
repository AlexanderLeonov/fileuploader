import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NO_ERRORS_SCHEMA} from '@angular/core';

import {FilePickerComponent} from './file-picker.component';
import {TotalSizePipeMock} from '../../test/total-size-pipe.mock';

describe('FilePickerComponent', () => {
    let component: FilePickerComponent;
    let fixture: ComponentFixture<FilePickerComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [
                FilePickerComponent,
                TotalSizePipeMock
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(FilePickerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
