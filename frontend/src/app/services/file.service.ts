import { HttpClient, HttpEvent, HttpEventType, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { filter, map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FileCustom } from '../entities/fileCustom';

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(
    private http: HttpClient,
  ) { }

  public uploadFile(file: File): Observable<FileCustom> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<FileCustom>(`${environment.baseUrl}/file/upload`, formData, { withCredentials: true });
  }

  public uploadImage(file: File, maxSize: number = 1920): Observable<{ progress?: number, result?: FileCustom }> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    // return this.http.post<FileCustom>(`${environment.baseUrl}/file/upload/image?size=${maxSize}`, formData, { withCredentials: true });

    const req = new HttpRequest(
      'POST',
      `${environment.baseUrl}/file/upload/image?size=${maxSize}`,
      formData,
      {
        withCredentials: true,
        reportProgress: true,
        responseType: 'json', // optional, default
      }
    );

    return this.http.request<FileCustom>(req).pipe(
      map((event: HttpEvent<FileCustom>) => {
        if (event.type === HttpEventType.UploadProgress) {
          const total = event.total ?? 0;
          const progress = total ? Math.round((100 * event.loaded) / total) : undefined;
          return { progress };
        }

        if (event.type === HttpEventType.Response) {
          return { progress: 100, result: event.body ?? undefined };
        }

        return {};
      }),
      filter(v => v.progress !== undefined || v.result !== undefined)
    );
  }
}
