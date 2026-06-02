import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GroupLightResponse } from '../dtos/groupLightResponse';
import { environment } from '../../environments/environment';
import { GroupResponse } from '../dtos/groupResponse';
import { PageResponse } from '../dtos/pageResponse';
import { GroupEditRequest } from '../dtos/groupEditRequest';
import { GroupInvitationResponse } from '../dtos/groupInvitationResponse';
import { InvitationStatusAccountEnum } from '../enums/invitationStatusAccountEnum';
import { GroupAdminMember } from '../dtos/groupAdminMember';
import { GroupMemberAdminRequest } from '../dtos/groupMemberAdminRequest';
import { GroupSettingsGeneral } from '../entities/groupSettingsGeneral';
import { LocationResourceResponse } from '../dtos/locationResourceResponse';
import { LocationResponse } from '../dtos/locationResponse';

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(
    private http: HttpClient
  ) { }

  public getGroupList(): Observable<GroupLightResponse[]> {
    return this.http.get<GroupLightResponse[]>(`${environment.baseUrl}/group`, { withCredentials: true });
  }

  public getGroup(uuid: string): Observable<GroupResponse> {
    return this.http.get<GroupResponse>(`${environment.baseUrl}/group/${uuid}`, { withCredentials: true });
  }

  public searchGroup(q: string, isMember: boolean | null = null, isPublic: boolean | null = null, page: number = 0, size: number = 36): Observable<PageResponse<GroupLightResponse>> {
    const params: any = {
      q: q,
      page: page,
      size: size
    };
    if (isMember != null) params.isMember = isMember;
    if (isPublic !== null) params.isPublic = isPublic;

    return this.http.get<PageResponse<GroupLightResponse>>(`${environment.baseUrl}/group/search`, { withCredentials: true, params });
  }

  // invitation
  public getInvitationList(): Observable<GroupInvitationResponse[]> {
    return this.http.get<GroupInvitationResponse[]>(`${environment.baseUrl}/group/invitation`, { withCredentials: true });
  }

  public countInvitationList(): Observable<number> {
    return this.http.get<number>(`${environment.baseUrl}/group/invitation/count`, { withCredentials: true });
  }

  public sendInvitationResponse(invitationUuid: string, response: InvitationStatusAccountEnum): Observable<void> {
    return this.http.put<void>(`${environment.baseUrl}/group/invitation/${invitationUuid}`, JSON.stringify(response), { withCredentials: true, headers: { 'Content-Type': 'application/json' } });
  }

  // edit
  public createGroup(data: GroupEditRequest): Observable<GroupResponse> {
    return this.http.post<GroupResponse>(`${environment.baseUrl}/group`, data, { withCredentials: true });
  }

  public getGeneralGroupSettings(groupUuid: string): Observable<GroupSettingsGeneral> {
    return this.http.get<GroupSettingsGeneral>(`${environment.baseUrl}/group/${groupUuid}/general`, { withCredentials: true });
  }

  public saveGeneralGroupSettings(group: GroupSettingsGeneral): Observable<GroupSettingsGeneral> {
    return this.http.put<GroupSettingsGeneral>(`${environment.baseUrl}/group/${group.uuid}/general`, group, { withCredentials: true });
  }

  public getAdminGroupMembers(groupUuid: string): Observable<GroupAdminMember[]> {
    return this.http.get<GroupAdminMember[]>(`${environment.baseUrl}/group/${groupUuid}/members`, { withCredentials: true });
  }

  public inviteMember(groupUuid: string, memberUuid: string): Observable<void> {
    return this.http.post<void>(`${environment.baseUrl}/group/${groupUuid}/invite`, JSON.stringify(memberUuid), { withCredentials: true, headers: { 'Content-Type': 'application/json' } });
  }

  public saveMemberChanges(groupUuid: string, memberUuid: string, request: GroupMemberAdminRequest): Observable<void> {
    return this.http.put<void>(`${environment.baseUrl}/group/${groupUuid}/members/${memberUuid}`, request, { withCredentials: true });
  }

  public removeMemberFromGroup(groupUuid: string, memberUuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/group/${groupUuid}/members/${memberUuid}`, { withCredentials: true });
  }

  public removeInvitationFromGroup(groupUuid: string, memberUuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/group/${groupUuid}/invite/${memberUuid}`, { withCredentials: true });
  }

  public deleteGroup(groupUuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/group/${groupUuid}`, { withCredentials: true });
  }

  // member settings
  public leaveGroup(groupUuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/group/${groupUuid}/leave`, { withCredentials: true });
  }

  // locations
  public getGroupLocations(groupUuid: string): Observable<LocationResponse[]> {
    return this.http.get<LocationResponse[]>(`${environment.baseUrl}/group/${groupUuid}/location`, { withCredentials: true });
  }

  public deleteGroupLocation(groupUuid: string, locationUuid: string): Observable<void> {
    return this.http.delete<void>(`${environment.baseUrl}/group/${groupUuid}/location/${locationUuid}`, { withCredentials: true });
  }

  public saveLocation(groupUuid: string, locationUuid: string, location: LocationResponse): Observable<LocationResponse> {
    return this.http.put<LocationResponse>(`${environment.baseUrl}/group/${groupUuid}/location/${locationUuid}`, location, { withCredentials: true });
  }

  public createTmpGroupLocationScreen(groupUuid: string, locationUuid: string): Observable<LocationResourceResponse> {
    return this.http.post<LocationResourceResponse>(`${environment.baseUrl}/group/${groupUuid}/location/${locationUuid}/screen`, {}, { withCredentials: true });
  }

  public createTmpGroupLocationClient(groupUuid: string, locationUuid: string): Observable<LocationResourceResponse> {
    return this.http.post<LocationResourceResponse>(`${environment.baseUrl}/group/${groupUuid}/location/${locationUuid}/client`, {}, { withCredentials: true });
  }
}
