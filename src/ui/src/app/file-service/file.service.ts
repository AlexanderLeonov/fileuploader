import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {ViewFile} from '../shared/viewFile';

@Injectable({
    providedIn: 'root'
})
export class FileService {

    constructor(private httpClient: HttpClient) {
    }

    private static readonly formParamName = 'files';
    private static readonly httpResourceName = 'api/files';

    private static toFormData(files: File[]) {
        const formData = new FormData();
        for (const file of files) {
            formData.append(FileService.formParamName, file, file.name);
        }
        return formData;
    }

    private static getFilesResourceUrl() {
        return `/${FileService.httpResourceName}`;
    }

    private static getFileUrl(fileName: string): string {
        return `${FileService.getFilesResourceUrl()}/${fileName}`;
    }

    getFileList(): Observable<ViewFile[]> {
        const url = FileService.getFilesResourceUrl();
        return this.httpClient.get<string[]>(url).pipe(
            map(files => {
                return files.map(f => ({name: f, url: FileService.getFileUrl(f)}));
            }),
            catchError(() => [])
        );
    }

    upload(files: File[]): Observable<any> {
        const url = FileService.getFilesResourceUrl();
        const formData = FileService.toFormData(files);
        return this.httpClient.post(url, formData);
    }

    delete(file: string): Observable<any> {
        const url = FileService.getFileUrl(file);
        return this.httpClient.delete(url);
    }

}
