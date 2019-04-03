import {NO_ERRORS_SCHEMA} from '@angular/core';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {Subject} from 'rxjs';
import {AppComponent} from './app.component';
import {FileService} from '../file-service/file.service';
import Spy = jasmine.Spy;

describe('AppComponent', () => {

    let fixture: ComponentFixture<AppComponent>

    beforeEach(async(() => {
        const fileService = jasmine.createSpyObj(['getFileList', 'upload', 'delete']);
        (fileService.getFileList as Spy).and.returnValue(new Subject());
        (fileService.upload as Spy).and.returnValue(new Subject());
        (fileService.delete as Spy).and.returnValue(new Subject());
        TestBed.configureTestingModule({
            imports: [
                RouterTestingModule
            ],
            declarations: [
                AppComponent
            ],
            providers: [
                {provide: FileService, useValue: fileService}
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(AppComponent);
        fixture.detectChanges();
    });

    it('should render header', () => {
        const div = fixture.debugElement.nativeElement.querySelector('.header');
        expect(div).toBeTruthy();
    });

    it('should render app-file-picker', () => {
        const div = fixture.debugElement.nativeElement.querySelector('app-file-picker');
        expect(div).toBeTruthy();
    });

    it('should render app-file-list', () => {
        const div = fixture.debugElement.nativeElement.querySelector('app-file-list');
        expect(div).toBeTruthy();
    });

    it('should not render errors if there are none', () => {
        const div = fixture.debugElement.nativeElement.querySelector('app-show-error');
        expect(div).toBeFalsy();
    });

    it('should render errors if there are some', () => {
        fixture.componentInstance.isErrorVisible = true;
        fixture.detectChanges();
        const div = fixture.debugElement.nativeElement.querySelector('app-show-error');
        expect(div).toBeTruthy();
    });

});
