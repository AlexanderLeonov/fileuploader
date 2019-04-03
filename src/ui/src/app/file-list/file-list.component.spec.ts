import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {FileListComponent} from './file-list.component';
import {NO_ERRORS_SCHEMA} from '@angular/core';

describe('FileListComponent', () => {
    let component: FileListComponent;
    let fixture: ComponentFixture<FileListComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [FileListComponent],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(FileListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
