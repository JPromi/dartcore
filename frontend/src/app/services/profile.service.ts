import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProfileResponse } from '../dtos/profileResponse';
import { PageResponse } from '../dtos/pageResponse';
import { ProfileLightResponse } from '../dtos/profileLightResponse';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  constructor(
    private http: HttpClient,
  ) { }

  public getProfile(username: string): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${environment.baseUrl}/profile/${username}`, { withCredentials: true });
  }

  public searchProfile(q: string, page: number = 0, size: number = 5, isPlayable: boolean | null = null, inGroup: string | null = null, notInGroup: string | null = null): Observable<PageResponse<ProfileLightResponse>> {
    var params: any = {
      q,
      page,
      size
    };
    if (isPlayable != null) params.isPlayable = isPlayable;
    if (inGroup) params.inGroup = inGroup;
    if (notInGroup) params.notInGroup = notInGroup;
    
    return this.http.get<PageResponse<ProfileLightResponse>>(`${environment.baseUrl}/profile/search`, {
      params,
      withCredentials: true
    });
  }
}
