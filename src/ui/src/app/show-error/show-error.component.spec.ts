import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ShowErrorComponent} from './show-error.component';
import {NO_ERRORS_SCHEMA} from '@angular/core';

describe('ShowErrorComponent', () => {

    let component: ShowErrorComponent;
    let fixture: ComponentFixture<ShowErrorComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ShowErrorComponent],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(ShowErrorComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

});
