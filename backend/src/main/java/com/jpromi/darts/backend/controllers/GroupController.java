package com.jpromi.darts.backend.controllers;

import com.jpromi.darts.backend.entities.Location;
import com.jpromi.darts.backend.entities.Session;
import com.jpromi.darts.backend.enums.InvitationStatusAccountEnum;
import com.jpromi.darts.backend.models.*;
import com.jpromi.darts.backend.services.AuthService;
import com.jpromi.darts.backend.services.GroupService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("GroupController")
@RequestMapping("/api/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuthService authService;

    @GetMapping("/search")
    public ResponseEntity<PageResponse<GroupLightResponse>> getGroupSearch(
            @CookieValue("dcn.session") String sessionCookie,
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "isMember", required = false, defaultValue = "") Boolean isMember,
            @RequestParam(value = "isPublic", required = false, defaultValue = "") Boolean isPublic,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "36") int size
    ) {
        size = Math.min(size, 120);

        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                PageResponse<GroupLightResponse> groups = this.groupService.searchGroups(query, session.getAccount(), Pageable.ofSize(size).withPage(page), isMember, isPublic);
                if(groups != null) {
                    return ResponseEntity.ok(groups);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GroupResponse> getGroupByUuid(@CookieValue("dcn.session") String sessionCookie, @PathVariable String uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupResponse group = this.groupService.getGroupByUuid(UUID.fromString(uuid), session.getAccount());
                if(group != null) {
                    return ResponseEntity.ok(group);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("")
    public ResponseEntity<GroupResponse> createGroup(@CookieValue("dcn.session") String sessionCookie, @RequestBody GroupRequest group) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupResponse createdGroup = this.groupService.createGroup(group, session.getAccount());
                if(createdGroup != null) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{uuid}/general")
    public ResponseEntity<GroupGeneralResponse> getGroupGeneralInfo(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupGeneralResponse group = this.groupService.getGroupGeneralByUuid(uuid, session.getAccount());
                if(group != null) {
                    return ResponseEntity.ok(group);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/{uuid}/general")
    public ResponseEntity<GroupGeneralResponse> updateGroupGeneralInfo(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID uuid, @RequestBody GroupGeneralRequest groupData) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupGeneralResponse updatedGroup = this.groupService.updateGroupGeneralByUuid(groupData, session.getAccount());
                return ResponseEntity.ok(updatedGroup);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{uuid}/members")
    public ResponseEntity<List<GroupMemberAdminResponse>> getGroupMembers(@CookieValue("dcn.session") String sessionCookie, @PathVariable String uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                GroupResponse group = this.groupService.getGroupByUuid(UUID.fromString(uuid), session.getAccount());
                if(group != null) {
                    List<GroupMemberAdminResponse> members = this.groupService.getGroupMembersSettings(UUID.fromString(uuid), session.getAccount());
                    return ResponseEntity.ok(members);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/{uuid}/members/{memberUuid}")
    public ResponseEntity<Void> updateGroupMemberSettings(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID memberUuid,
            @RequestBody GroupMemberAdminRequest request) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                    this.groupService.updateGroupMemberSettings(uuid, memberUuid, request, session.getAccount());
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/{uuid}/members/{memberUuid}")
    public ResponseEntity<Void> removeGroupMember(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID memberUuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                this.groupService.removeMemberFromGroup(uuid, memberUuid, session.getAccount());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/{uuid}/leave")
    public ResponseEntity<Void> leaveGroup(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                this.groupService.leaveGroup(uuid, session.getAccount());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/{uuid}/invite")
    public ResponseEntity<Void> inviteToGroup(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID uuid, @RequestBody UUID invitationPlayerUuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                this.groupService.inviteAccountToGroup(
                        uuid,
                        invitationPlayerUuid,
                        session.getAccount()
                );
                return ResponseEntity.status(HttpStatus.OK).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/{uuid}/invite/{memberUuid}")
    public ResponseEntity<Void> removeGroupInvitation(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID memberUuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if (session != null) {
                this.groupService.removeInvitationFromGroup(uuid, memberUuid, session.getAccount());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteGroup(@CookieValue("dcn.session") String sessionCookie, @PathVariable String uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                this.groupService.deleteGroup(UUID.fromString(uuid), session.getAccount());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("invitation")
    public ResponseEntity<List<GroupInvitationResponse>> getAccountInvitations(@CookieValue("dcn.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                List<GroupInvitationResponse> invitations = this.groupService.getAccountInvitations(session.getAccount(), InvitationStatusAccountEnum.PENDING);
                return ResponseEntity.ok(invitations);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("invitation/count")
    public ResponseEntity<Long> countAccountInvitations(@CookieValue("dcn.session") String sessionCookie) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                Long invitations = this.groupService.countAccountInvitations(session.getAccount(), InvitationStatusAccountEnum.PENDING);
                return ResponseEntity.ok(invitations);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("invitation/{uuid}")
    public ResponseEntity<Void> responseInvitation(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable String uuid,
            @RequestBody InvitationStatusAccountEnum status
    ) {
        if(!(status == InvitationStatusAccountEnum.ACCEPTED || status == InvitationStatusAccountEnum.REJECTED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                this.groupService.responseInvitation(UUID.fromString(uuid), session.getAccount(), status);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // Locations
    @GetMapping("/{uuid}/location")
    public ResponseEntity<List<LocationResponse>> getGroupLocations(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID uuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.ok(groupService.getLocationsInGroup(uuid, session.getAccount()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{uuid}/location/{locationUuid}")
    public ResponseEntity<LocationResponse> getGroupLocation(@CookieValue("dcn.session") String sessionCookie, @PathVariable UUID uuid, @PathVariable UUID locationUuid) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.ok(groupService.getLocation(uuid, session.getAccount(), locationUuid));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/{uuid}/location")
    public ResponseEntity<LocationResponse> addGroupLocations(
        @CookieValue("dcn.session") String sessionCookie,
        @PathVariable UUID uuid,
        @RequestBody LocationRequest location
    ) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.ok(groupService.createLocation(uuid, session.getAccount(), location));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/{uuid}/location/{locationUuid}")
    public ResponseEntity<LocationResponse> updateGroupLocations(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID locationUuid,
            @RequestBody LocationRequest location
    ) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.ok(groupService.updateLocation(uuid, session.getAccount(), locationUuid, location));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @DeleteMapping("/{uuid}/location/{locationUuid}")
    public ResponseEntity<Void> deleteGroupLocations(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID locationUuid
    ) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                try {
                    groupService.deleteLocation(uuid, session.getAccount(), locationUuid);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // location screen
    @PostMapping("/{uuid}/location/{locationUuid}/screen")
    public ResponseEntity<LocationResponse.Screen> addScreenGroupLocation(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID locationUuid
    ) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.status(HttpStatus.OK).body(groupService.createTmpLocationScreen(uuid, session.getAccount(), locationUuid));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    // location client
    @PostMapping("/{uuid}/location/{locationUuid}/client")
    public ResponseEntity<LocationResponse.Client> addClientGroupLocation(
            @CookieValue("dcn.session") String sessionCookie,
            @PathVariable UUID uuid,
            @PathVariable UUID locationUuid
    ) {
        if(sessionCookie != null) {
            Session session = this.authService.session(sessionCookie);

            if(session != null) {
                return ResponseEntity.status(HttpStatus.OK).body(groupService.createTmpLocationClient(uuid, session.getAccount(), locationUuid));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
